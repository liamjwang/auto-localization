package org.team1540.robot2018.commands.auto;

import static org.team1540.robot2018.Tuning.timeStep;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Trajectory.Config;
import jaci.pathfinder.Trajectory.Segment;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;
import org.team1540.base.motionprofiling.MotionProfilingProperties;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class AutonomousProfiling extends Command {

  private double maxVelocity = Tuning.maxVelocity;
  private double maxAcceleration = Tuning.maxAcceleration;
  private double maxJerk = Tuning.maxJerk;
  private double secondsFromNeutralToFull = Tuning.secondsFromNeutralToFull;
  private TrajectorySegment[] segments;

  private Timer isFinishedRunningTimer = new Timer();

  private double timeToFinish = 0;

  public AutonomousProfiling(TrajectorySegment... segments) {
    requires(Robot.drivetrain);
    this.segments = segments;
  }

  public AutonomousProfiling(double maxVelocity, TrajectorySegment... segments) {
    requires(Robot.drivetrain);
    this.maxVelocity = maxVelocity;
    this.segments = segments;
  }

  public AutonomousProfiling(double maxVelocity, double maxAcceleration, double maxJerk, double secondsFromNeutralToFull, TrajectorySegment... segments) {
    requires(Robot.drivetrain);
    this.maxVelocity = maxVelocity;
    this.maxAcceleration = maxAcceleration;
    this.maxJerk = maxJerk;
    this.secondsFromNeutralToFull = secondsFromNeutralToFull;
    this.segments = segments;
  }

  @Override
  protected void initialize() {
    Robot.drivetrain.zeroEncoders();
    double turningRadius = Math.sqrt(Math.pow(Tuning.wheelbaseWidth, 2) + Math.pow
        (Tuning.distanceBetweenWheels, 2));

    Config config = new Config(Tuning.fitMethod, Tuning.sampleRate, timeStep,
        Tuning.maxVelocity, Tuning.maxAcceleration, Tuning.maxJerk);

    Trajectory trajectory = generateSimpleTrajectory(segments[0].start, segments[0].end, config);
    TankModifier modifier = new TankModifier(trajectory).modify(turningRadius);
    Segment[] left = modifier.getLeftTrajectory().segments;
    Segment[] right = modifier.getRightTrajectory().segments;

    timeToFinish = left.length * left[0].dt;

    // TODO: Add option to flip and reverse profiles
    MotionProfilingProperties leftProperties = new MotionProfilingProperties
        (Tuning.lEncoderTicksPerUnit, Tuning.secondsFromNeutralToFull, Robot
            .drivetrain::getLeftVelocity,
            Robot.drivetrain::setLeftVelocity, Robot.drivetrain::getLeftPosition,
            modifier.getLeftTrajectory());
    MotionProfilingProperties rightProperties = new MotionProfilingProperties
        (Tuning.rEncoderTicksPerUnit, Tuning.secondsFromNeutralToFull, Robot
            .drivetrain::getRightVelocity,
            Robot.drivetrain::setRightVelocity, Robot.drivetrain::getRightPosition,
            modifier.getRightTrajectory());
    isFinishedRunningTimer.reset();
    Scheduler.getInstance().add(new RunMotionProfilesBackwards(leftProperties, rightProperties));
    isFinishedRunningTimer.start();
  }

  @Override
  protected void execute() {
  }

  private Trajectory generateSimpleTrajectory(Waypoint start, Waypoint end, Config config) {
    return Pathfinder.generate(new Waypoint[]{start, end}, config);
  }

  @Override
  protected boolean isFinished() {
    return isFinishedRunningTimer.get() > timeToFinish;
  }

  public static class TrajectorySegment {
    public Waypoint end;
    public boolean flip;
    public Waypoint start;

    public TrajectorySegment(Waypoint start, Waypoint end, boolean flip) {
      this.end = end;
      this.flip = flip;
      this.start = start;
    }
  }

  public class InvalidPathException extends Exception {
    public InvalidPathException() {
      super();
    }

    public InvalidPathException(String message) {
      super(message);
    }
  }
}

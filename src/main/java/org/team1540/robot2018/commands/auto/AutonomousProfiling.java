package org.team1540.robot2018.commands.auto;

import static org.team1540.robot2018.Tuning.timeStep;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Trajectory.Config;
import jaci.pathfinder.Trajectory.FitMethod;
import jaci.pathfinder.Trajectory.Segment;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;
import java.util.LinkedList;
import java.util.List;
import openrio.powerup.MatchData;
import openrio.powerup.MatchData.GameFeature;
import openrio.powerup.MatchData.OwnedSide;
import org.team1540.base.motionprofiling.MotionProfilingProperties;
import org.team1540.base.motionprofiling.RunMotionProfiles;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;
import org.team1540.robot2018.commands.auto.ProfileIO.MotionProfilingTypes;
import org.team1540.robot2018.commands.auto.ProfileIO.StartLocation;

public class AutonomousProfiling extends Command {

  // TODO Any changes based on the fact that the center of turning isn't in the center?

  private SendableChooser<AutoType> autoTypeChooser = new SendableChooser<>();
  private Trajectory.FitMethod fitMethod = FitMethod.HERMITE_CUBIC;
  private Timer isFinishedRunningTimer = new Timer();
  // Default command does nothing; this shouldn't ever happen, since it will be set to the real
  // thing during initialization
  private Command runMotionProfiles = new Command() {
    @Override
    protected boolean isFinished() {
      DriverStation.reportError("Motion profiling not initialized correctly!", false);
      return true;
    }
  };
  private TrajectorySegment[] segments;
  private SendableChooser<StartLocation> startLocationChooser = new SendableChooser<>();
  private double timeToFinish = 0;

  public AutonomousProfiling() {
    // TODO Can this be done at declaration?
    // TODO Is adding stuff to SmartDashboard in a decentralized manner okay?
    // FIXME null pointers for days
    for (AutoType type : AutoType.values()) {
      autoTypeChooser.addObject(type.name(), type);
    }
    autoTypeChooser.addDefault("DEFAULT: SWITCH_ALL_SIDES", AutoType.SWITCH_ALL_SIDES);
    autoTypeChooser.setName("Auto Type");
    SmartDashboard.putData(autoTypeChooser);
    for (StartLocation type : StartLocation.values()) {
      startLocationChooser.addObject(type.name(), type);
    }
    startLocationChooser.addDefault("DEFAULT: RIGHT_EDGE", StartLocation.RIGHT_EDGE);
    startLocationChooser.setName("Start Location");
    SmartDashboard.putData(startLocationChooser);
  }

  public AutonomousProfiling(TrajectorySegment... segments) {
    requires(Robot.drivetrain);
    this.segments = segments;
    System.out.println(segments[0].start.y + " ---------------- " + segments[0].end.y);
  }

  // private Trajectory generateTestTrajectory(Trajectory.Config config) {
  //   return Pathfinder.generate(new Waypoint[]{
  //       new Waypoint(0, 0, 0), new Waypoint
  //       (Tuning.distanceToTravelX, Tuning.distanceToTravelY,
  //           Pathfinder.d2r(Tuning.degreesToTurn))
  //   }, config);
  // }

  @Override
  protected void initialize() {
    Robot.drivetrain.zeroEncoders();

    // FIXME yeah this is totally good
    Trajectory[] trajectories = new Trajectory[]{null};

    switch (autoTypeChooser.getSelected()) {
      case CROSS_LINE:
        trajectories = ProfileIO.loadProfileFromCSV(MotionProfilingTypes.CROSS_LINE);
        break;
      case SWITCH_SAME_SIDE:
        if (MatchData.getOwnedSide(GameFeature.SWITCH_NEAR) == OwnedSide.LEFT
            && startLocationChooser.getSelected() == StartLocation.LEFT_EDGE) {
          trajectories = ProfileIO.loadProfileFromCSV(MotionProfilingTypes.CENTER_TO_SWITCH_LEFT);
        } else if (MatchData.getOwnedSide(GameFeature.SWITCH_NEAR) == OwnedSide.RIGHT
            && startLocationChooser.getSelected() == StartLocation.RIGHT_EDGE) {
          trajectories = ProfileIO.loadProfileFromCSV(MotionProfilingTypes.CENTER_TO_SWITCH_RIGHT);
        } else {
          trajectories = ProfileIO.loadProfileFromCSV(MotionProfilingTypes.CROSS_LINE);
        }
        break;
      case SWITCH_ALL_SIDES:
        switch (MatchData.getOwnedSide(GameFeature.SWITCH_NEAR)) {
          case LEFT:
            trajectories = ProfileIO.loadProfileFromCSV(MotionProfilingTypes.CENTER_TO_SWITCH_LEFT);
            break;
          case RIGHT:
            trajectories= ProfileIO.loadProfileFromCSV(MotionProfilingTypes.CENTER_TO_SWITCH_RIGHT);
            break;
        }
        break;
      case SWITCH_TOP:
        break;
    }

    timeToFinish = trajectories[0].segments.length * trajectories[0].segments[0].dt;
    // for (TrajectorySegment segment : segments) {
    //   Trajectory traj = generateSimpleTrajectory(segment.start, segment.end, config);
    //   timeToFinish += traj.segments.length * traj.segments[0].dt;
    //
    //   TankModifier modifier = new TankModifier(traj).modify(turningRadius);
    //   Segment[] left = modifier.getLeftTrajectory().segments;
    //   Segment[] right = modifier.getRightTrajectory().segments;
    //
    //   for (int i = 0; i < modifier.getLeftTrajectory().length(); i++) {
    //     Segment leftSegment = (segment.flip ? right : left)[i];
    //     Segment rightSegment = (segment.flip ? right : left)[i];
    //     if (segment.flip) {
    //       leftSegment.position *= -1;
    //       rightSegment.position *= -1;
    //       leftSegment.velocity *= -1;
    //       rightSegment.velocity *= -1;
    //     }
    //     leftSegments.add(leftSegment);
    //     rightSegments.add(rightSegment);
    //   }
    // }


    MotionProfilingProperties leftProperties = new MotionProfilingProperties
        (Tuning.lEncoderTicksPerUnit, Tuning.secondsFromNeutralToFull, Robot
            .drivetrain::getLeftVelocity,
            Robot.drivetrain::setLeftVelocity, Robot.drivetrain::getLeftPosition,
            trajectories[0]);
    MotionProfilingProperties rightProperties = new MotionProfilingProperties
        (Tuning.rEncoderTicksPerUnit, Tuning.secondsFromNeutralToFull, Robot
            .drivetrain::getRightVelocity,
            Robot.drivetrain::setRightVelocity, Robot.drivetrain::getRightPosition,
            trajectories[1]);

    isFinishedRunningTimer.reset();
    runMotionProfiles = new RunMotionProfiles(leftProperties, rightProperties);
    Scheduler.getInstance().add(runMotionProfiles);
    isFinishedRunningTimer.start();
  }

  @Override
  protected void execute() {
    // Don't need anything AFAIK
  }

  private Trajectory generateSimpleTrajectory(Waypoint start, Waypoint end, Config config) {
    return Pathfinder.generate(new Waypoint[]{start, end}, config);
  }

  private Waypoint makeTranslatedWaypoint(Waypoint toTranslate, double x, double y, double angle) {
    return new Waypoint(toTranslate.x + x, toTranslate.y + y, toTranslate.angle + angle);
  }

  @Override
  protected boolean isFinished() {
    SmartDashboard.putBoolean("PROFILING IS FINISHED", runMotionProfiles.isCompleted());
    // FIXME doesn't work so have to do dumb timer stuff
    // return runMotionProfiles.isCompleted();
    return isFinishedRunningTimer.get() > timeToFinish;
  }

  @Override
  protected void interrupted() {
    runMotionProfiles.cancel();
  }

  public enum AutoType {
    CROSS_LINE, SWITCH_SAME_SIDE, SWITCH_ALL_SIDES, SWITCH_TOP
  }

  public interface Location {
    public Waypoint getLocation();
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

  // Hey look it's a special exception cuz I couldn't find one I liked
  public class InvalidPathException extends Exception {
    public InvalidPathException() {
      super();
    }

    public InvalidPathException(String message) {
      super(message);
    }
  }
}

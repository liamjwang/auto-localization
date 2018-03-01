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
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class AutonomousProfiling extends Command {

  // TODO Any changes based on the fact that the center of turning isn't in the center?

  private static final double BOT_BUMPER_WIDTH = 3.25;
  private static final double BOT_LENGTH = 32.625;
  private static final double BOT_EFFECTIVE_LENGTH = BOT_LENGTH + 2 * BOT_BUMPER_WIDTH;
  private static final double BOT_SHORT_DISTANCE_TO_TURNING = 27.75;
  private static final double BOT_WIDTH = 27.75;
  private static final double BOT_EFFECTIVE_WIDTH = BOT_WIDTH + 2 * BOT_BUMPER_WIDTH;

  // X is the long side of the field, Y is the short side.
  // All measurements are relative to your alliance wall, since the field is fully symmetrical.
  private static final double FIELD_EFFECTIVE_LENGTH = 647.125;
  private static final double FIELD_EFFECTIVE_WIDTH = 324.5;
  // TODO Better way of storing these measurements
  private static final double FIELD_EXCHANGE_NEAR_DISTANCE_FROM_CENTER = 12;
  private static final double FIELD_PORTAL_HEIGHT = 60;
  private static final double FIELD_STARTING_LINE = 30;
  private static final double FIELD_SWITCH_MIDDLE_TO_CENTER = 54.53;
  private static final double FIELD_SWITCH_TO_ALLIANCE_WALL = 151;
  private static final double FIELD_SWITCH_TO_SIDE_WALLS = 85.75;
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
  // TODO Store these profiles instead of generating on the fly
  private List<Waypoint> waypoints = new LinkedList<>();

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
    // AdjustableManager.getInstance().add(this);
  }

  public AutonomousProfiling(TrajectorySegment... segments) {
    requires(Robot.drivetrain);
    this.segments = segments;
  }

  private Trajectory generateTestTrajectory(Trajectory.Config config) {
    return Pathfinder.generate(new Waypoint[]{
        new Waypoint(0, 0, 0), new Waypoint
        (Tuning.distanceToTravelX, Tuning.distanceToTravelY,
            Pathfinder.d2r(Tuning.degreesToTurn))
    }, config);
  }

  public Trajectory generateTrajectory(Trajectory.Config config, AutoType autoType, StartLocation
      startLocation) {
    waypoints.clear();

    double xStraight = 130;
    double xSide = 100;
    double ySide = 90;
    double degrees = Pathfinder.d2r(-15);

    // FIXME Temp hard coded values, doesn't even listen to start position
    switch (autoType) {
      case CROSS_LINE:
        waypoints.add(new Waypoint(0, 0, 0));
        waypoints.add(new Waypoint(130, 0, 0));
        break;
      case SWITCH_SAME_SIDE:
        waypoints.add(new Waypoint(0, 0, 0));
        if (MatchData.getOwnedSide(GameFeature.SWITCH_NEAR) == OwnedSide.LEFT
            && startLocation == StartLocation.LEFT_EDGE) {
          waypoints.add(new Waypoint(xSide, -ySide, -degrees));
        } else if (MatchData.getOwnedSide(GameFeature.SWITCH_NEAR) == OwnedSide.RIGHT
            && startLocation == StartLocation.RIGHT_EDGE) {
          waypoints.add(new Waypoint(xSide, ySide, degrees));
        } else {
          // Just cross the line
          waypoints.add(new Waypoint(130, 0, 0));
        }
        break;
      case SWITCH_ALL_SIDES:
        switch (MatchData.getOwnedSide(GameFeature.SWITCH_NEAR)) {
          case LEFT:
            waypoints.add(new Waypoint(xSide, -ySide, -degrees));
            break;
          case RIGHT:
            waypoints.add(new Waypoint(xSide, ySide, degrees));
            break;
        }
        break;
      case SWITCH_TOP:
        break;
    }

    /*
    waypoints.add(startLocation.getLocation());
    switch (autoType) {
      case CROSS_LINE:
        switch (startLocation) {
          case EXCHANGE_MIDDLE:
            throw new InvalidPathException(
                "Auto type " + autoType + " is not valid for starting position " + startLocation);
          default:
            waypoints.add(makeTranslatedWaypoint(waypoints.get(0),
                FIELD_STARTING_LINE + BOT_EFFECTIVE_LENGTH, 0, 0));
            break;
        }
        break;
      case SWITCH_SAME_SIDE:
        if (MatchData.getOwnedSide(GameFeature.SWITCH_NEAR) == OwnedSide.LEFT
            && startLocation == StartLocation.LEFT_EDGE) {
          waypoints.add(EndLocation.LEFT_SWITCH_SIDE.getLocation());
        } else if (MatchData.getOwnedSide(GameFeature.SWITCH_NEAR) == OwnedSide.RIGHT
            && startLocation == StartLocation.RIGHT_EDGE) {
          waypoints.add(EndLocation.RIGHT_SWITCH_SIDE.getLocation());
        } else {
          // Just cross the line
          waypoints.add(makeTranslatedWaypoint(waypoints.get(0),
              FIELD_STARTING_LINE + BOT_EFFECTIVE_LENGTH, 0, 0));
        }
        break;
      case SWITCH_ALL_SIDES:
        switch (MatchData.getOwnedSide(GameFeature.SWITCH_NEAR)) {
          case LEFT:
            waypoints.add(EndLocation.LEFT_SWITCH_SIDE.getLocation());
            break;
          case RIGHT:
            waypoints.add(EndLocation.RIGHT_SWITCH_SIDE.getLocation());
            break;
        }
        break;
      case SWITCH_TOP:
        // TODO
        break;
    }
    */

    return Pathfinder.generate(waypoints.toArray(new Waypoint[0]), config);
  }

  @Override
  protected void initialize() {
    Robot.drivetrain.zeroEncoders();
    // FIXME YOU DO NOT PASS IN THE TURNING RADIUS YOU PASS IN THE WHEELBASE WIDTH
    double turningRadius = Math.sqrt(Math.pow(Tuning.wheelbaseWidth, 2) + Math.pow
        (Tuning.distanceBetweenWheels, 2));

    // // TODO Yes this is very safe honhonhon
    Trajectory trajectory;
    // try {
    //   trajectory = generateTrajectory(new Config(fitMethod, Tuning.sampleRate, timeStep, Tuning.maxVelocity,
    //           Tuning.maxAcceleration, Tuning.maxJerk),
    //       autoTypeChooser.getSelected(), startLocationChooser.getSelected());
    //   // TODO exception handling
    // } catch (InvalidPathException e) {
    //   e.printStackTrace();
    //   return;
    // }


    Config config = new Config(fitMethod, Tuning.sampleRate, timeStep,
        Tuning.maxVelocity, Tuning.maxAcceleration, Tuning.maxJerk);

    List<Segment> leftSegments = new LinkedList<>();
    List<Segment> rightSegments = new LinkedList<>();

    timeToFinish = 0;
    for (TrajectorySegment segment : segments) {
      Trajectory traj = generateSimpleTrajectory(segment.start, segment.end, config);
      timeToFinish += traj.segments.length * traj.segments[0].dt;

      TankModifier modifier = new TankModifier(traj).modify(turningRadius);
      Segment[] left = modifier.getLeftTrajectory().segments;
      Segment[] right = modifier.getRightTrajectory().segments;

      for (int i = 0; i < modifier.getLeftTrajectory().length(); i++) {
        Segment leftSegment = (segment.flip ? right : left)[i];
        Segment rightSegment = (segment.flip ? right : left)[i];
        if (segment.flip) {
          leftSegment.position *= -1;
          rightSegment.position *= -1;
          leftSegment.velocity *= -1;
          rightSegment.velocity *= -1;
        }
        leftSegments.add(leftSegment);
        rightSegments.add(rightSegment);
      }
    }


    MotionProfilingProperties leftProperties = new MotionProfilingProperties
        (Tuning.lEncoderTicksPerUnit, Tuning.secondsFromNeutralToFull, Robot
            .drivetrain::getLeftVelocity,
            Robot.drivetrain::setLeftVelocity, Robot.drivetrain::getLeftPosition,
            new Trajectory(leftSegments.toArray(new Segment[0])));
    MotionProfilingProperties rightProperties = new MotionProfilingProperties
        (Tuning.rEncoderTicksPerUnit, Tuning.secondsFromNeutralToFull, Robot
            .drivetrain::getRightVelocity,
            Robot.drivetrain::setRightVelocity, Robot.drivetrain::getRightPosition,
            new Trajectory(leftSegments.toArray(new Segment[0])));
    isFinishedRunningTimer.reset();
    Scheduler.getInstance().add(new RunMotionProfiles(leftProperties, rightProperties));
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

  public enum AutoType {
    CROSS_LINE, SWITCH_SAME_SIDE, SWITCH_ALL_SIDES, SWITCH_TOP
  }

  public interface Location {
    public Waypoint getLocation();
  }

  public enum StartLocation implements Location {
    LEFT_EDGE(new Waypoint(
        BOT_EFFECTIVE_LENGTH / 2, FIELD_EFFECTIVE_WIDTH - FIELD_PORTAL_HEIGHT, 0)),
    EXCHANGE_MIDDLE(new Waypoint(
        BOT_EFFECTIVE_LENGTH / 2,
        FIELD_EFFECTIVE_WIDTH / 2 + FIELD_EXCHANGE_NEAR_DISTANCE_FROM_CENTER
            - BOT_EFFECTIVE_WIDTH / 2, 0)),
    RIGHT_EDGE(new Waypoint(BOT_EFFECTIVE_LENGTH / 2, FIELD_PORTAL_HEIGHT, 0));

    private final Waypoint location;

    StartLocation(Waypoint location) {
      this.location = location;
    }

    @Override
    public Waypoint getLocation() {
      return location;
    }
  }

  public enum EndLocation implements Location {
    LEFT_SWITCH_SIDE(new Waypoint(
        FIELD_SWITCH_TO_ALLIANCE_WALL - BOT_EFFECTIVE_LENGTH / 2,
        FIELD_EFFECTIVE_WIDTH - FIELD_SWITCH_MIDDLE_TO_CENTER, 0)),
    RIGHT_SWITCH_SIDE(new Waypoint(FIELD_SWITCH_TO_ALLIANCE_WALL
        - BOT_EFFECTIVE_LENGTH / 2, FIELD_SWITCH_MIDDLE_TO_CENTER, 0));

    private final Waypoint location;

    EndLocation(Waypoint location) {
      this.location = location;
    }

    @Override
    public Waypoint getLocation() {
      return location;
    }
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

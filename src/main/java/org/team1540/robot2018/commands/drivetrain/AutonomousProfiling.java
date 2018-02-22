package org.team1540.robot2018.commands.drivetrain;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Trajectory.Config;
import jaci.pathfinder.Trajectory.FitMethod;
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

public class AutonomousProfiling extends Command {

  // TODO Any changes based on the fact that the center of turning isn't in the center?

  private SendableChooser<AutoType> autoTypeChooser = new SendableChooser<>();
  private SendableChooser<StartLocation> startLocationChooser = new SendableChooser<>();
  // Default command does nothing; this shouldn't ever happen, since it will be set to the real thing during initialization
  private Command runMotionProfiles = new Command() {
    @Override
    protected boolean isFinished() {
      DriverStation.reportError("Motion profiling not initialized correctly!", false);
      return true;
    }
  };

  // Units in inches and seconds

  // Not a huge fan of having these all here
  private double wheelbaseWidth = 25.091;
  private double distanceBetweenWheels = 11.875;
  private double turningRadius = Math.sqrt(Math.pow(wheelbaseWidth, 2) + Math.pow(distanceBetweenWheels, 2));
  private double lEncoderTicksPerUnit = 51;
  private double rEncoderTicksPerUnit = 51;
  private double secondsFromNeutralToFull = 0;

  private Trajectory.FitMethod fitMethod = FitMethod.HERMITE_CUBIC;
  private int sampleRate = Config.SAMPLES_HIGH;
  private double timeStep = 0.05;
  private double maxVelocity = 80;
  private double maxAcceleration = 80;
  private double maxJerk = 2300;

  // X is the long side of the field, Y is the short side.
  // All measurements are relative to your alliance wall, since the field is fully symmetrical.

  private static final double BOT_LENGTH = 32.625;
  private static final double BOT_WIDTH = 27.75;
  private static final double BOT_SHORT_DISTANCE_TO_TURNING = 27.75;
  private static final double BOT_BUMPER_WIDTH = 3.25;
  private static final double BOT_EFFECTIVE_LENGTH = BOT_LENGTH + 2 * BOT_BUMPER_WIDTH;
  private static final double BOT_EFFECTIVE_WIDTH = BOT_WIDTH + 2 * BOT_BUMPER_WIDTH;
  private static final double FIELD_EFFECTIVE_LENGTH = 647.125;
  private static final double FIELD_EFFECTIVE_WIDTH = 324.5;
  // TODO Better way of storing these measurements
  private static final double FIELD_EXCHANGE_NEAR_DISTANCE_FROM_CENTER = 12;
  private static final double FIELD_PORTAL_HEIGHT = 60;
  private static final double FIELD_STARTING_LINE = 30;
  private static final double FIELD_SWITCH_TO_SIDE_WALLS = 85.75;
  private static final double FIELD_SWITCH_TO_ALLIANCE_WALL = 151;
  private static final double FIELD_SWITCH_MIDDLE_TO_CENTER = 54.53;

  // TODO Store these profiles instead of generating on the fly
  private List<Waypoint> waypoints = new LinkedList<>();

  public AutonomousProfiling() {
    // TODO Can this be done at declaration?
    // TODO Is adding stuff to SmartDashboard in a decentralized manner okay?
    // FIXME null pointers for days
    for (AutoType type : AutoType.values()) {
      autoTypeChooser.addObject(type.name(), type);
    }
    autoTypeChooser.addDefault("DEFAULT: SWITCH_SAME_SIDE", AutoType.SWITCH_SAME_SIDE);
    autoTypeChooser.setName("Auto Type");
    SmartDashboard.putData(autoTypeChooser);
    for (StartLocation type : StartLocation.values()) {
      startLocationChooser.addObject(type.name(), type);
    }
    startLocationChooser.addDefault("DEFAULT: LEFT_EDGE", StartLocation.LEFT_EDGE);
    startLocationChooser.setName("Start Location");
    SmartDashboard.putData(startLocationChooser);
  }

  @Override
  protected void initialize() {
    // TODO Yes this is very safe honhonhon
    Trajectory trajectory = null;
    try {
       trajectory = generateTrajectory(new Config(fitMethod, sampleRate, timeStep, maxVelocity, maxAcceleration, maxJerk),
          autoTypeChooser.getSelected(), startLocationChooser.getSelected());
    // TODO exception handling
    } catch (InvalidPathException e) {
      e.printStackTrace();
      return;
    }

    TankModifier modifier = new TankModifier(trajectory);
    modifier.modify(turningRadius);

    Robot.drivetrain.prepareForMotionProfiling();

    MotionProfilingProperties leftProperties = new MotionProfilingProperties(lEncoderTicksPerUnit, secondsFromNeutralToFull, Robot.drivetrain::getLeftVelocity, Robot.drivetrain::setLeftVelocity, Robot.drivetrain::getLeftPosition, modifier.getLeftTrajectory());
    MotionProfilingProperties rightProperties = new MotionProfilingProperties(rEncoderTicksPerUnit, secondsFromNeutralToFull, Robot.drivetrain::getRightVelocity, Robot.drivetrain::setRightVelocity, Robot.drivetrain::getRightPosition, modifier.getRightTrajectory());
    Scheduler.getInstance().add(new RunMotionProfiles(leftProperties, rightProperties));
  }

  @Override
  protected void execute() {
    // Don't need anything AFAIK
  }

  public Trajectory generateTrajectory(Trajectory.Config config, AutoType autoType, StartLocation startLocation) throws InvalidPathException {
    waypoints.add(startLocation.getLocation());
    switch (autoType) {
      case CROSS_LINE:
        switch (startLocation) {
          case EXCHANGE_MIDDLE:
            throw new InvalidPathException(
                "Auto type " + autoType + " is not valid for starting position " + startLocation);
          default:
            waypoints.add(makeTranslatedWaypoint(waypoints.get(0), FIELD_STARTING_LINE + BOT_EFFECTIVE_LENGTH, 0, 0));
            break;
        }
        break;
      case SWITCH_SAME_SIDE:
        if (MatchData.getOwnedSide(GameFeature.SWITCH_NEAR) == OwnedSide.LEFT && startLocation == StartLocation.LEFT_EDGE) {
          waypoints.add(EndLocation.LEFT_SWITCH_SIDE.getLocation());
        } else if (MatchData.getOwnedSide(GameFeature.SWITCH_NEAR) == OwnedSide.RIGHT && startLocation == StartLocation.RIGHT_EDGE) {
          waypoints.add(EndLocation.RIGHT_SWITCH_SIDE.getLocation());
        } else {
          // Just cross the line
          waypoints.add(makeTranslatedWaypoint(waypoints.get(0), FIELD_STARTING_LINE + BOT_EFFECTIVE_LENGTH, 0, 0));
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
    return Pathfinder.generate(waypoints.toArray(new Waypoint[waypoints.size()]), config);
  }

  @Override
  protected boolean isFinished() {
    // TODO
    return false;
  }

  private Waypoint makeTranslatedWaypoint(Waypoint toTranslate, double x, double y, double angle) {
    return new Waypoint(toTranslate.x + x, toTranslate.y + y, toTranslate.angle + angle);
  }

  public enum AutoType {
    CROSS_LINE, SWITCH_SAME_SIDE, SWITCH_ALL_SIDES, SWITCH_TOP
  }

  public interface Location {
    public Waypoint getLocation();
  }

  public enum StartLocation implements Location {
    LEFT_EDGE(new Waypoint(BOT_EFFECTIVE_LENGTH/2, FIELD_EFFECTIVE_WIDTH - FIELD_PORTAL_HEIGHT, 0)),
    EXCHANGE_MIDDLE(new Waypoint(BOT_EFFECTIVE_LENGTH/2, FIELD_EFFECTIVE_WIDTH/2 + FIELD_EXCHANGE_NEAR_DISTANCE_FROM_CENTER - BOT_EFFECTIVE_WIDTH/2, 0)),
    RIGHT_EDGE(new Waypoint(BOT_EFFECTIVE_LENGTH/2, FIELD_PORTAL_HEIGHT, 0));

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
    LEFT_SWITCH_SIDE(new Waypoint(FIELD_SWITCH_TO_ALLIANCE_WALL - BOT_EFFECTIVE_LENGTH/2, FIELD_EFFECTIVE_WIDTH - FIELD_SWITCH_MIDDLE_TO_CENTER, 0)),
    RIGHT_SWITCH_SIDE(new Waypoint(FIELD_SWITCH_TO_ALLIANCE_WALL - BOT_EFFECTIVE_LENGTH/2, FIELD_SWITCH_MIDDLE_TO_CENTER, 0));

    private final Waypoint location;

    EndLocation(Waypoint location) {
      this.location = location;
    }

    @Override
    public Waypoint getLocation() {
      return location;
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

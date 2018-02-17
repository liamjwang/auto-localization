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
import org.team1540.base.motionprofiling.MotionProfilingProperties;
import org.team1540.base.motionprofiling.RunMotionProfiles;
import org.team1540.robot2018.Robot;

public class AutonomousProfiling extends Command {

  private SendableChooser<Class<AutoType>> autoTypeChooser = new SendableChooser<>();
  private SendableChooser<Class<StartLocation>> startLocationChooser = new SendableChooser<>();
  // Default command does nothing; this shouldn't ever happen, since it will be set to the real thing during initialization
  private Command runMotionProfiles = new Command() {
    @Override
    protected boolean isFinished() {
      DriverStation.reportError("Motion profiling not initialized correctly!", false);
      return true;
    }
  };

  // Not a huge fan of having these all here
  // TODO Set these to some real values
  private final double wheelbaseWidth = 10;

  private final Trajectory.FitMethod fitMethod = FitMethod.HERMITE_CUBIC;
  private final int sampleRate = Config.SAMPLES_HIGH;
  private final double timeStep = 0.05;
  // TODO Set these to some real values
  private final double maxVelocity = 0.5;
  private final double maxAcceleration = 5.0;
  private final double maxJerk = 60;

  // Units in inches; x is the long side of the field, y is the short side.
  // All measurements are relative to your alliance wall, since the field is fully symmetrical.

  private static final double BOT_LENGTH = 32.625;
  private static final double BOT_WIDTH = 27.75;
  private static final double BOT_SHORT_DISTANCE_TO_TURNING = 27.75;
  private static final double BUMPER_WIDTH = 3.25;
  private static final double BOT_EFFECTIVE_LENGTH = BOT_LENGTH + 2 * BUMPER_WIDTH;
  private static final double BOT_EFFECTIVE_WIDTH = BOT_WIDTH + 2 * BUMPER_WIDTH;
  private static final double FIELD_EFFECTIVE_LENGTH = 647.125;
  private static final double FIELD_EFFECTIVE_WIDTH = 324.5;
  // TODO Better way of storing these measurements
  private static final double FIELD_EXCHANGE_NEAR_DISTANCE_FROM_CENTER = 12;
  private static final double FIELD_PORTAL_HEIGHT = 60;
  private static final double FIELD_STARTING_LINE = 30;
  private static final double FIELD_SWITCH_NEAR_DISTANCE_FROM_EDGE = 85.75;

  // TODO Store these profiles instead of generating on the fly
  private List<Waypoint> waypoints = new LinkedList<>();

  public AutonomousProfiling() {
    // TODO Can this be done at declaration?
    for (AutoType type : AutoType.values()) {
      autoTypeChooser.addObject(type.getClass().getName(), (Class<AutoType>) type.getClass());
    }
    SmartDashboard.putData(autoTypeChooser);
    for (StartLocation type : StartLocation.values()) {
      startLocationChooser.addObject(type.getClass().getName(), (Class<StartLocation>) type.getClass());
    }
    // TODO Is adding stuff to SmartDashboard in a decentralized manner okay?
    SmartDashboard.putData(startLocationChooser);
  }

  @Override
  protected void initialize() {
    // TODO Yes this is very safe honhonhon
    Trajectory trajectory = null;
    try {
       trajectory = generateTrajectory(new Config(fitMethod, sampleRate, timeStep, maxVelocity, maxAcceleration, maxJerk),
          autoTypeChooser.getSelected().newInstance(), startLocationChooser.getSelected().newInstance());
    // TODO exception handling
    } catch (InvalidPathException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }

    TankModifier modifier = new TankModifier(trajectory);
    modifier.modify(wheelbaseWidth);

    MotionProfilingProperties leftProperties = new MotionProfilingProperties(Robot.drivetrain::getLeftVelocity, Robot.drivetrain::setLeftVelocity, Robot.drivetrain::getLeftPosition, modifier.getLeftTrajectory());
    MotionProfilingProperties rightProperties = new MotionProfilingProperties(Robot.drivetrain::getRightVelocity, Robot.drivetrain::setRightVelocity, Robot.drivetrain::getRightPosition, modifier.getRightTrajectory());
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
      case SWITCH_SIMPLE:
        switch (MatchData.getOwnedSide(GameFeature.SWITCH_NEAR)) {
          case LEFT:
            // TODO
            break;
          case RIGHT:
            // TODO
            break;
        }
        break;
      case SWITCH_COMPLEX:
        switch (MatchData.getOwnedSide(GameFeature.SWITCH_NEAR)) {
          case LEFT:
            // TODO
            break;
          case RIGHT:
            // TODO
            break;
        }
        break;
    }
    return Pathfinder.generate((Waypoint[]) waypoints.toArray(), config);
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
    CROSS_LINE, SWITCH_SIMPLE, SWITCH_COMPLEX
  }

  public enum StartLocation {
    LEFT_EDGE(new Waypoint(BOT_EFFECTIVE_LENGTH/2, FIELD_EFFECTIVE_WIDTH - FIELD_PORTAL_HEIGHT, 0)),
    EXCHANGE_MIDDLE(new Waypoint(BOT_EFFECTIVE_LENGTH/2, FIELD_EFFECTIVE_WIDTH/2 + FIELD_EXCHANGE_NEAR_DISTANCE_FROM_CENTER - BOT_EFFECTIVE_WIDTH/2, 0)),
    RIGHT_EDGE(new Waypoint(BOT_EFFECTIVE_LENGTH/2, FIELD_PORTAL_HEIGHT, 0)),
    LEFT_SWITCH_ALIGN(new Waypoint(BOT_EFFECTIVE_LENGTH/2, FIELD_EFFECTIVE_WIDTH - FIELD_SWITCH_NEAR_DISTANCE_FROM_EDGE, 0)),
    RIGHT_SWITCH_ALIGN(new Waypoint(BOT_EFFECTIVE_LENGTH/2, FIELD_EFFECTIVE_WIDTH, 0));

    private final Waypoint location;

    StartLocation(Waypoint location) {
      this.location = location;
    }

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

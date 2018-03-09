package org.team1540.robot2018.commands.auto;

import static org.team1540.robot2018.Tuning.timeStep;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Trajectory.Config;
import jaci.pathfinder.Trajectory.FitMethod;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;
import java.io.File;
import org.team1540.robot2018.Tuning;
import org.team1540.robot2018.commands.auto.AutonomousProfiling.Location;

public class ProfileIO {

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

  static double xStraight = 130;
  static double xSide = 100;
  static double ySide = 90;
  static double degrees = Pathfinder.d2r(-15);
  static Trajectory.FitMethod fitMethod = FitMethod.HERMITE_CUBIC;

  public static void main(String[] args) {
    writeAllTrajectories();
  }

  public static void writeAllTrajectories() {
    for (MotionProfilingTypes motionProfilingType : MotionProfilingTypes.values()) {
      writeProfileToCSV(motionProfilingType);
    }
  }

  public static void writeProfileToCSV(MotionProfilingTypes toWrite) {
    TankModifier modifier = new TankModifier(Pathfinder.generate(toWrite.waypoints, new Config
        (fitMethod, Tuning.sampleRate, timeStep,
        Tuning.maxVelocity, Tuning.maxAcceleration, Tuning.maxJerk))).modify(Math.sqrt(
        Math.pow(Tuning.wheelbaseWidth, 2) + Math.pow
            (Tuning.distanceBetweenWheels, 2)));
    Pathfinder.writeToCSV(new File(
        Tuning.motionProfileOutPath + toWrite.name() + "_left.csv"), modifier.getLeftTrajectory());
    Pathfinder.writeToCSV(new File(Tuning.motionProfileOutPath + toWrite.name()
        + "_right.csv"), modifier.getRightTrajectory());
  }

  public static Trajectory[] loadProfileFromCSV(MotionProfilingTypes type) {
    return new Trajectory[]{
        Pathfinder.readFromCSV(new File(Tuning.motionProfileInPath + type.name()
            + "_left.csv")), Pathfinder.readFromCSV(new File(
        Tuning.motionProfileInPath + type.name() + "_right.csv"))
    };
  }

  public enum MotionProfilingTypes {

    CROSS_LINE(new Waypoint[]{new Waypoint(0, 0, 0), new Waypoint(130, 0, 0)}),
    CENTER_TO_SWITCH_LEFT(new Waypoint[]{new Waypoint(0, 0, 0), new Waypoint(xSide, -ySide,
        -degrees)}),
    CENTER_TO_SWITCH_RIGHT(new Waypoint[]{new Waypoint(0, 0, 0), new Waypoint(xSide, ySide, degrees)});

    public final Waypoint[] waypoints;

    MotionProfilingTypes(Waypoint[] waypoints) {
      this.waypoints = waypoints;
    }
  }
}

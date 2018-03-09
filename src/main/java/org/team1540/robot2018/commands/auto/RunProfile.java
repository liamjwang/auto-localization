package org.team1540.robot2018.commands.auto;

import edu.wpi.first.wpilibj.command.TimedCommand;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import java.io.File;
import org.team1540.base.motionprofiling.MotionProfilingProperties;
import org.team1540.base.motionprofiling.RunMotionProfiles;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

/**
 * Executes a motion profile from either left and right .csv files in {@code /home/lvuser/profiles},
 * or left and right .profile files. If set to load CSV files, the command will search for two
 * profiles (stored by {@link Pathfinder#writeToCSV(File, Trajectory)} named NAME_left.csv and
 * NAME_right.csv. If set to load binary files, the command will search for two profiles (stored by
 * {@link Pathfinder#writeToFile(File, Trajectory)} named NAME_left.profile and NAME_right.profile.
 */
public class RunProfile extends TimedCommand {

  private final RunMotionProfiles profileCommand;

  public RunProfile(String name, boolean loadBinary) {
    super("Run profile " + name, 0); // timeout will be set later
    // load the left and right profiles
    Trajectory left, right;
    if (loadBinary) {
      left = Pathfinder.readFromFile(new File("/home/lvuser/profiles/" + name + "_left.profile"));
      right = Pathfinder.readFromFile(new File("/home/lvuser/profiles/" + name + "_right.profile"));
    } else {
      left = Pathfinder.readFromCSV(new File("/home/lvuser/profiles/" + name + "_left.csv"));
      right = Pathfinder.readFromCSV(new File("/home/lvuser/profiles/" + name + "_right.csv"));
    }

    // figure out how long execution will take and set the timeout
    // don't anticipate left and right to take different amounts of points but better safe than
    // sorry
    setTimeout(Double.max(left.length() * left.segments[0].dt, right.length() * right
        .segments[0].dt));

    MotionProfilingProperties leftProperties = new MotionProfilingProperties(
        Tuning.lEncoderTicksPerUnit,
        0,
        Robot.drivetrain::getLeftVelocity,
        Robot.drivetrain::setLeftVelocity,
        Robot.drivetrain::getLeftPosition,
        left);

    MotionProfilingProperties rightProperties = new MotionProfilingProperties(
        Tuning.rEncoderTicksPerUnit,
        0,
        Robot.drivetrain::getRightVelocity,
        Robot.drivetrain::setRightVelocity,
        Robot.drivetrain::getRightPosition,
        right);

    profileCommand = new RunMotionProfiles(leftProperties, rightProperties);
  }

  @Override
  protected void initialize() {
    profileCommand.start();
  }

  @Override
  protected void end() {
    profileCommand.cancel();
  }
}

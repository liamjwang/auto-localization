package org.team1540.robot2018.commands.auto;

import edu.wpi.first.wpilibj.command.Command;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import java.io.File;
import org.team1540.base.motionprofiling.MotionProfilingProperties;
import org.team1540.robot2018.Robot;

/**
 * Executes a motion profile from either left and right .csv files in {@code /home/lvuser/profiles},
 * or left and right .profile files. If set to load CSV files, the command will search for two
 * profiles (stored by {@link Pathfinder#writeToCSV(File, Trajectory)} named NAME_left.csv and
 * NAME_right.csv. If set to load binary files, the command will search for two profiles (stored by
 * {@link Pathfinder#writeToFile(File, Trajectory)} named NAME_left.profile and NAME_right.profile.
 */
public class ExecuteProfile extends Command {

  private final RunMotionProfiles profileCommand;

  public ExecuteProfile(String name, double timeout, boolean loadBinary) {
    super("Run profile " + name); // timeout will be set later

    requires(Robot.drivetrain);

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
    // setTimeout(Double.max(left.length() * left.segments[0].dt, right.length() * right
    // .segments[0].dt) / 1000);

    MotionProfilingProperties leftProperties = new MotionProfilingProperties(
        Robot.drivetrain::getLeftVelocity,
        Robot.drivetrain::setLeftVelocity,
        Robot.drivetrain::getLeftPosition,
        left);

    MotionProfilingProperties rightProperties = new MotionProfilingProperties(
        Robot.drivetrain::getRightVelocity,
        Robot.drivetrain::setRightVelocity,
        Robot.drivetrain::getRightPosition,
        right);

    profileCommand = new RunMotionProfiles(leftProperties, rightProperties);
  }

  @Override
  protected boolean isFinished() {
    return profileCommand.isFinished();
  }

  @Override
  protected void initialize() {
    profileCommand.initialize();
  }

  @Override
  protected void execute() {
    profileCommand.execute();
  }

  @Override
  protected void end() {
    profileCommand.end();
  }

  @Override
  protected void interrupted() {
    profileCommand.end();
  }
}

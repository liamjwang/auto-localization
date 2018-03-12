package org.team1540.robot2018.commands.auto;

import edu.wpi.first.wpilibj.command.Command;
import jaci.pathfinder.Trajectory;
import org.team1540.base.motionprofiling.MotionProfilingProperties;
import org.team1540.base.motionprofiling.RunMotionProfiles;
import org.team1540.robot2018.CSVProfileManager.DriveProfile;
import org.team1540.robot2018.Robot;

public class RunProfile extends Command {
  private RunMotionProfiles profileCommand;

  @Override
  protected boolean isFinished() {
    return profileCommand.isFinished();
  }

  public RunProfile(String name) {
    super("Run profile " + name, 0); // timeout will be set later

    requires(Robot.drivetrain);

    DriveProfile driveProfile = Robot.profiles.getProfile(name);

    Trajectory left = driveProfile.getLeft();
    Trajectory right = driveProfile.getRight();

    // figure out how long execution will take and set the timeout
    // don't anticipate left and right to take different amounts of points but better safe than sorry
    setTimeout(
        Double.max(left.length() * left.segments[0].dt, right.length() * right.segments[0].dt)
            / 1000);

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
  protected void initialize() {
    profileCommand.start();
  }

  @Override
  protected void end() {
    profileCommand.cancel();
  }
}

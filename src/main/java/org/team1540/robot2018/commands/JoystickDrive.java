package org.team1540.robot2018.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.OI;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.RobotUtil;

public class JoystickDrive extends Command {
  public JoystickDrive() {
    requires(Robot.drivetrain);
  }

  @Override
  protected void initialize() {
  }

  @Override
  protected void execute() {
    double triggerValue = OI.getDriverRightTrigger() - OI.getDriverLeftTrigger();
    Robot.drivetrain.setLeft(RobotUtil.deadzone(OI.getDriverLeftX() + triggerValue));
    Robot.drivetrain.setRight(RobotUtil.deadzone(OI.getDriverRightX() + triggerValue));
  }

  @Override
  protected boolean isFinished() {
    return false; //Return true to stop the command
  }

  @Override
  protected void end() {
  }

  @Override
  protected void interrupted() {
  }
}
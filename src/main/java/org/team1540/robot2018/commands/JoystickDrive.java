package org.team1540.robot2018.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.OI;
import org.team1540.robot2018.Robot;

public class JoystickDrive extends Command {
  public JoystickDrive() {
    requires(Robot.drivetrain);
  }

  @Override
  protected void initialize() {
    double triggerValue = OI.getDriverLeftTrigger() + -OI.getDriverRightTrigger();
    Robot.drivetrain.setLeft(OI.getDriverLeftX() + triggerValue);
    Robot.drivetrain.setRight(OI.getDriverRightX() + triggerValue);
  }

  @Override
  protected void execute() {
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
    Robot.intake.stop();
  }
}
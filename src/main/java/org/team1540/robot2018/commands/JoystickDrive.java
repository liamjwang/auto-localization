package org.team1540.robot2018.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.OI;
import org.team1540.robot2018.Robot;

public class JoystickDrive extends Command {
  public JoystickDrive() {
    requires(Robot.drivetrain);
  }

  @Override
  protected void execute() {
    double triggerValue = OI.getDriverRightTrigger() - OI.getDriverLeftTrigger();
    Robot.drivetrain.setLeft(-OI.getDriverLeftY() + triggerValue);
    Robot.drivetrain.setRight(-OI.getDriverRightY() + triggerValue);
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}

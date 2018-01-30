package org.team1540.robot2018.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.Robot;

public class AutoEject extends Command {
  public AutoEject() {
    requires(Robot.intake);
  }

  @Override
  protected void initialize() {
    Robot.intake.EjectPowerup(0.5, 10, 1.0);
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
  }
}

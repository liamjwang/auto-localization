package org.team1540.robot2018.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class ManualEject extends Command {
  public ManualEject() {
    requires(Robot.intake);
  }

  @Override
  protected void initialize() {
    Robot.intake.manualIntake(Tuning.EjectSpeedA, Tuning.EjectSpeedB);
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

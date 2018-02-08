package org.team1540.robot2018.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class ManualIntake extends Command {
  public ManualIntake() {
    requires(Robot.intake);
  }

  @Override
  protected void initialize() {
    Robot.intake.manualIntake(Tuning.IntakeSpeedA, Tuning.IntakeSpeedB);
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
    Robot.intake.stop();
  }

  @Override
  protected void interrupted() {
  }
}
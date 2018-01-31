package org.team1540.robot2018.commands;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;


public class AutoIntake extends Command {

  public AutoIntake() {
    requires(Robot.intake);
  }

  @Override
  protected void initialize(){
    Robot.intake.intake_1.set(ControlMode.PercentOutput, Tuning.IntakeSpeed);
  }

  @Override
  protected void execute() {
  }

  @Override
  protected boolean isFinished() {
    return(Robot.intake.intake_1.getOutputCurrent() >= Tuning.IntakeSpikeCurrent && Robot.intake.intake_2.getOutputCurrent() >= Tuning.IntakeSpikeCurrent);
  }

  @Override
  protected void end() {
    Robot.intake.stop();
  }

  @Override
  protected void interrupted() {
  }
}

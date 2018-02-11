package org.team1540.robot2018.commands.intake;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;


public class AutoIntake extends Command {

  public AutoIntake() {
    requires(Robot.intake);
  }

  @Override
  protected void initialize(){
    Robot.intake.set(Tuning.IntakeSpeedA, Tuning.IntakeSpeedB);
  }

  @Override
  protected boolean isFinished() {
    return(Robot.intake.getCurrent1() >= Tuning.IntakeSpikeCurrent && Robot.intake.getCurrent2() >= Tuning.IntakeSpikeCurrent);
  }

  @Override
  protected void end() {
    Robot.intake.stop();
  }
}

package org.team1540.robot2018.commands.intake;

import static org.team1540.robot2018.Robot.intake;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class HoldIntake extends Command {
  public HoldIntake() {
    requires(intake);
  }

  @Override
  protected void execute() {
    Robot.intake.set(Tuning.intakeHoldSpeed);
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}

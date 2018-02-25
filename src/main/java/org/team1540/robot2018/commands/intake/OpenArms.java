package org.team1540.robot2018.commands.intake;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class OpenArms extends Command {

  public OpenArms() {
    requires(Robot.intakeArms);
  }

  @Override
  protected void execute() {
    Robot.intakeArms.set(Tuning.intakeArmSpeed); // No current current limiting but works well enough for now
  }

  @Override
  protected void end() {
    Robot.intakeArms.set(0);
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

}

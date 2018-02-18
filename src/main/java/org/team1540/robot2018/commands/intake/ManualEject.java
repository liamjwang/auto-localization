package org.team1540.robot2018.commands.intake;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class ManualEject extends Command {
  public ManualEject() {
    requires(Robot.intake);
  }

  @Override
  protected void initialize() {
    Robot.intake.set(Tuning.ejectSpeedA, Tuning.ejectSpeedB);
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

  @Override
  protected void end() {
    Robot.intake.stop();
  }
}

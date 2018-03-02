package org.team1540.robot2018.commands.intake;

import edu.wpi.first.wpilibj.command.TimedCommand;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class EjectAuto extends TimedCommand {

  public EjectAuto() {
    super(Tuning.ejectTime);
    requires(Robot.intake);
  }

  @Override
  protected void initialize() {
    setTimeout(Tuning.ejectTime);
  }

  @Override
  protected void execute() {
    Robot.intake.set(1, 1);
  }

  @Override
  protected void end() {
    Robot.intake.stop();
  }
}

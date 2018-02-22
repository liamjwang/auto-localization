package org.team1540.robot2018.commands.intake;

import edu.wpi.first.wpilibj.command.TimedCommand;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class EjectCube extends TimedCommand {

  public EjectCube() {
    super(Tuning.ejectTime);
    requires(Robot.intake);
  }

  @Override
  protected void initialize() {
    setTimeout(Tuning.ejectTime);
  }

  @Override
  protected void execute() {
    Robot.intake.set(Tuning.ejectSpeedA, Tuning.ejectSpeedB);
  }

  @Override
  protected void end() {
    Robot.intake.stop();
  }
}

package org.team1540.robot2018.commands.intake;

import edu.wpi.first.wpilibj.command.TimedCommand;
import org.team1540.robot2018.Robot;

public class EjectAutoSlow extends TimedCommand {

  public EjectAutoSlow() {
    super(2);
    requires(Robot.intake);
  }

  @Override
  protected void initialize() {
    setTimeout(2);
  }

  @Override
  protected void execute() {
    Robot.intake.set(0.6, 0.6);
  }

  @Override
  protected void end() {
    Robot.intake.holdCube();
  }
}

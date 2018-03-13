package org.team1540.robot2018.commands.intake;

import edu.wpi.first.wpilibj.command.TimedCommand;
import org.team1540.robot2018.OI;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class JoystickEject extends TimedCommand {

  private double ejectAxis;

  public JoystickEject() {
    super(Tuning.intakeEjectTime);
    requires(Robot.intake);
  }

  @Override
  protected void initialize() {
    setTimeout(Tuning.intakeEjectTime);
    ejectAxis = OI.getEjectAxis();
  }

  @Override
  protected void execute() {
    Robot.intake.set(Tuning.ejectSpeedA * ejectAxis);
  }

  @Override
  protected void end() {
    Robot.intake.holdCube();
  }
}

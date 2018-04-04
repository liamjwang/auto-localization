package org.team1540.robot2018.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import org.team1540.base.util.SimpleCommand;
import org.team1540.base.wrappers.ChickenVictor;
import org.team1540.robot2018.RobotMap;
import org.team1540.robot2018.commands.JoystickTape;

public class ClimberTape extends Subsystem {

  private ChickenVictor tapeMotor = new ChickenVictor(RobotMap.TAPE);

  public ClimberTape() {
    tapeMotor.setInverted(false);
  }

  public void set(double value) {
    tapeMotor.set(value);
  }

  public void stop() {
    set(0);
  }

  @Override
  protected void initDefaultCommand() {
    setDefaultCommand(new JoystickTape());
  }

}

package org.team1540.robot2018.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import org.team1540.base.util.SimpleCommand;
import org.team1540.base.wrappers.ChickenVictor;
import org.team1540.robot2018.RobotMap;

public class ClimberTapeMeasure extends Subsystem {
  private ChickenVictor tapeMotor = new ChickenVictor(RobotMap.TAPE_MEASURE);

  @Override
  protected void initDefaultCommand() {
    setDefaultCommand(new SimpleCommand("Stop tape measure", this::stop, this));
  }

  public void set(double value) {
    tapeMotor.set(value);
  }

  public void stop() {
    tapeMotor.set(0);
  }
}

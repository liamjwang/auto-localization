package org.team1540.robot2018.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.team1540.base.wrappers.ChickenVictor;
import org.team1540.robot2018.RobotMap;

public class Intake extends Subsystem {

  private ChickenVictor intake_1 = new ChickenVictor(RobotMap.intake_1);
  private ChickenVictor intake_2 = new ChickenVictor(RobotMap.intake_2);
  private double priority = 10;

  public Intake() {
    intake_1.setInverted(true);
    intake_2.setInverted(true);
  }

  public double getCurrent1() {
    return intake_1.getOutputCurrent();
  }

  public double getCurrent2() {
    return intake_2.getOutputCurrent();
  }

  @Override
  protected void initDefaultCommand() {
  }

  public void set(double value) {
    intake_1.set(ControlMode.PercentOutput, value);
    intake_2.set(ControlMode.PercentOutput, value);
  }

  public void set(double aValue, double bValue) {
    intake_1.set(ControlMode.PercentOutput, aValue);
    intake_2.set(ControlMode.PercentOutput, bValue);
  }

  public void stop() {
    intake_1.set(ControlMode.PercentOutput, 0);
    intake_2.set(ControlMode.PercentOutput, 0);
  }
}

package org.team1540.robot2018.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.team1540.base.util.SimpleCommand;
import org.team1540.base.wrappers.ChickenTalon;
import org.team1540.robot2018.RobotMap;
import org.team1540.robot2018.Tuning;

public class IntakeArms extends Subsystem {

  private ChickenTalon armMotorLeft = new ChickenTalon(RobotMap.ARM_LEFT);
  private ChickenTalon armMotorRight = new ChickenTalon(RobotMap.ARM_RIGHT);

  public IntakeArms() {
    armMotorLeft.setInverted(false);
    armMotorRight.setInverted(true);
    armMotorLeft.setBrake(false);
    armMotorRight.setBrake(false);
  }

  public void set(double value) {
    armMotorLeft.set(ControlMode.PercentOutput, value);
    armMotorRight.set(ControlMode.PercentOutput, value);
  }

  public void setLeft(double value) {
    armMotorLeft.set(ControlMode.PercentOutput, value);
  }

  public void setRight(double value) {
    armMotorRight.set(ControlMode.PercentOutput, value);
  }

  public double getCurrent() {
    return armMotorLeft.getOutputCurrent() + armMotorRight.getOutputCurrent();
  }

  @Override
  protected void initDefaultCommand() {
    setDefaultCommand(new SimpleCommand("Arm Hold", () -> {
      armMotorLeft.set(Tuning.intakeArmHoldSpeed);
      armMotorRight.set(Tuning.intakeArmHoldSpeed);
    }, this));
  }

}

package org.team1540.robot2018.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.team1540.base.util.SimpleCommand;
import org.team1540.base.wrappers.ChickenTalon;
import org.team1540.robot2018.RobotMap;
import org.team1540.robot2018.Tuning;

public class IntakeArms extends Subsystem {

  private ChickenTalon arm1 = new ChickenTalon(RobotMap.ARM_A);
  private ChickenTalon arm2 = new ChickenTalon(RobotMap.ARM_B);

  public IntakeArms() {
    arm1.setInverted(false);
    arm2.setInverted(true);
    arm1.setBrake(false);
    arm2.setBrake(false);
  }

  public void set(double value) {
    arm1.set(ControlMode.PercentOutput, value);
    arm2.set(ControlMode.PercentOutput, value);
  }

  public double getCurrent() {
    return arm1.getOutputCurrent() + arm2.getOutputCurrent();
  }

  @Override
  protected void initDefaultCommand() {
    setDefaultCommand(new SimpleCommand("Arm Hold", () -> {
      arm1.set(Tuning.intakeArmHoldSpeed);
      arm2.set(Tuning.intakeArmHoldSpeed);
    }, this));
  }

}

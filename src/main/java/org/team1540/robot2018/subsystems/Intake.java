package org.team1540.robot2018.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.base.wrappers.ChickenVictor;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.RobotMap;
import org.team1540.robot2018.Tuning;

public class Intake extends Subsystem {
  PowerDistributionPanel pdp = new PowerDistributionPanel();

  private ChickenVictor intake1 = new ChickenVictor(RobotMap.intake_1);
  private ChickenVictor intake2 = new ChickenVictor(RobotMap.intake_2);
  private double priority = 10;

  public Intake() {
    intake1.setInverted(true);
    intake2.setInverted(true);
  }

  @Override
  protected void initDefaultCommand() {
  }

  @Override
  public void periodic() {
    SmartDashboard.putNumber("Intake Current", getCurrent());
  }

  public double getCurrent() {
    return (Tuning.isPandora ? pdp.getCurrent(5) : pdp.getCurrent(10) + pdp.getCurrent(11))
        - Robot.wrist.getCurrent();
  }

  public void set(double value) {
    intake1.set(ControlMode.PercentOutput, value);
    intake2.set(ControlMode.PercentOutput, value);
  }

  public void set(double aValue, double bValue) {
    intake1.set(ControlMode.PercentOutput, aValue);
    intake2.set(ControlMode.PercentOutput, bValue);
  }

  public void stop() {
    intake1.set(ControlMode.PercentOutput, 0);
    intake2.set(ControlMode.PercentOutput, 0);
  }
}

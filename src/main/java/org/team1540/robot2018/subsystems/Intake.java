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

  private ChickenVictor intakeMotorA = new ChickenVictor(RobotMap.INTAKE_1);
  private ChickenVictor intakeMotorB = new ChickenVictor(RobotMap.INTAKE_2);

  public Intake() {
    intakeMotorA.setInverted(true);
    intakeMotorB.setInverted(true);
  }

  @Override
  protected void initDefaultCommand() {
  }

  @Override
  public void periodic() {
    SmartDashboard.putNumber("[Intake] getCurrent", getCurrent());
  }

  public double getCurrent() {
    // TODO: better method of adjusting tuning between robots
    if (Tuning.isPandora) {
      return pdp.getCurrent(5)
          - Robot.wrist.getCurrent();
    } else {
      return pdp.getCurrent(10) + pdp.getCurrent(11)
          - Robot.wrist.getCurrent();
    }
  }

  public void set(double value) {
    intakeMotorA.set(ControlMode.PercentOutput, value);
    intakeMotorB.set(ControlMode.PercentOutput, value);
  }

  public void set(double valueA, double valueB) {
    intakeMotorA.set(ControlMode.PercentOutput, valueA);
    intakeMotorB.set(ControlMode.PercentOutput, valueB);
  }

  public void stop() {
    intakeMotorA.set(ControlMode.PercentOutput, 0);
    intakeMotorB.set(ControlMode.PercentOutput, 0);
  }
}

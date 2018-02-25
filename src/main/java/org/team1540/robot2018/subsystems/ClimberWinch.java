package org.team1540.robot2018.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.team1540.base.util.SimpleCommand;
import org.team1540.base.wrappers.ChickenTalon;
import org.team1540.base.wrappers.ChickenVictor;
import org.team1540.robot2018.RobotMap;

/**
 * MADE_OF_ALUMINIUM -> IS_WINCH
 */
public class ClimberWinch extends Subsystem {
  private ChickenTalon winchMotorA = new ChickenTalon(RobotMap.WINCH_A);
  private ChickenVictor winchMotorB = new ChickenVictor(RobotMap.WINCH_B);
  private ChickenVictor winchMotorC = new ChickenVictor(RobotMap.WINCH_C);
  private ChickenVictor winchMotorD = new ChickenVictor(RobotMap.WINCH_D);

  public ClimberWinch() {
    winchMotorA.setInverted(false);
    winchMotorB.setInverted(false);
    winchMotorC.setInverted(true);
    winchMotorD.setInverted(true);
  }

  @Override
  protected void initDefaultCommand() {
    setDefaultCommand(new SimpleCommand("Stop winch", this::stop, this));
  }

  public void set(double throttle) {
    winchMotorA.set(ControlMode.PercentOutput, throttle);
    winchMotorB.set(ControlMode.PercentOutput, throttle);
    winchMotorC.set(ControlMode.PercentOutput, throttle);
    winchMotorD.set(ControlMode.PercentOutput, throttle);
  }

  public void stop() {
    set(0);
  }
}

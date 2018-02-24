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
  private ChickenTalon winchA = new ChickenTalon(RobotMap.WINCH_A);
  private ChickenVictor winchB = new ChickenVictor(RobotMap.WINCH_B);
  private ChickenVictor winchC = new ChickenVictor(RobotMap.WINCH_C);
  private ChickenVictor winchD = new ChickenVictor(RobotMap.WINCH_D);

  public ClimberWinch() {
    winchA.setInverted(false);
    winchB.setInverted(false);
    winchC.setInverted(true);
    winchD.setInverted(true);
  }

  @Override
  protected void initDefaultCommand() {
    setDefaultCommand(new SimpleCommand("Stop winch", this::stop, this));
  }

  public void set(double throttle) {
    winchA.set(ControlMode.PercentOutput, throttle);
    winchB.set(ControlMode.PercentOutput, throttle);
    winchC.set(ControlMode.PercentOutput, throttle);
    winchD.set(ControlMode.PercentOutput, throttle);
  }

  public void stop() {
    set(0);
  }
}

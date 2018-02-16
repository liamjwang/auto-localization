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
  private ChickenTalon winchA = new ChickenTalon(RobotMap.winchA);
  private ChickenVictor winchB = new ChickenVictor(RobotMap.winchB);
  private ChickenVictor winchC = new ChickenVictor(RobotMap.winchC);
  private ChickenVictor winchD = new ChickenVictor(RobotMap.winchD);

  public ClimberWinch() {
    winchA.setInverted(false);
    winchB.setInverted(true);
    winchC.setInverted(false);
    winchD.setInverted(true);
  }

  @Override
  protected void initDefaultCommand() {
    setDefaultCommand(new SimpleCommand("Stop winch", () -> set(0), this));
  }

  public void set(double throttle) {
    winchA.set(ControlMode.PercentOutput, throttle);
    winchB.set(ControlMode.PercentOutput, throttle);
    winchC.set(ControlMode.PercentOutput, throttle);
    winchD.set(ControlMode.PercentOutput, throttle);
  }
}

package org.team1540.robot2018.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import org.team1540.base.wrappers.ChickenTalon;
import org.team1540.robot2018.RobotMap;
import org.team1540.base.ChickenSubsystem;
import org.team1540.robot2018.commands.JoystickDrive;

public class DriveTrain extends ChickenSubsystem {

  private ChickenTalon left = new ChickenTalon(RobotMap.left);
  private ChickenTalon left2 = new ChickenTalon(RobotMap.left2);
  private ChickenTalon left3 = new ChickenTalon(RobotMap.left3);

  private ChickenTalon right = new ChickenTalon(RobotMap.right);
  private ChickenTalon right2 = new ChickenTalon(RobotMap.right2);
  private ChickenTalon right3 = new ChickenTalon(RobotMap.right3);


  // public double getLeftVelocity() {}
  // public double getRightVelocity() {}
  // public void setLeftVelocity(double velocity) {}
  // public void setLeftThrottle(double throttle){}
  // public void setRightThrottle(double throttle){}
  // public void setRightVelocity(double velocity){}


  public DriveTrain() {
    this.add(left, left2, left3, right, right2, right3);
    this.setPriority(10);

    left.setInverted(false);
    left2.setInverted(false);
    left3.setInverted(false);

    right.setInverted(false);
    right2.setInverted(false);
    right3.setInverted(false);

    left2.set(ControlMode.Follower, left.getDeviceID());
    left3.set(ControlMode.Follower, left.getDeviceID());

    right2.set(ControlMode.Follower, right.getDeviceID());
    right3.set(ControlMode.Follower, right.getDeviceID());
  }

  public void setLeft(double value){
    this.left.set(ControlMode.PercentOutput, value);
  }

  public void setRight(double value){
    this.right.set(ControlMode.PercentOutput, value);
  }

  @Override
  public void initDefaultCommand() {
    setDefaultCommand(new JoystickDrive());
  }
}

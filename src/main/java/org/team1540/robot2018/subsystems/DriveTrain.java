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

    right.setInverted(true);
    right2.setInverted(true);
    right3.setInverted(true);

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
  
  
  public double getLeftPosition() {
    return left.getQuadraturePosition();
  }

  public double getRightPosition() {
    return right.getQuadraturePosition();
  }
  
  public double getLeftVelocity() {
    return left.getQuadratureVelocity();
  }

  public double getRightVelocity() {
    return right.getQuadratureVelocity();
  }

  public void setLeftVelocity(double velocity) {
    left.set(ControlMode.Velocity, velocity);
  }

  public void setRightVelocity(double velocity) {
    left.set(ControlMode.Velocity, velocity);
  }

  public void prepareForMotionProfiling() {
    left.setControlMode(ControlMode.Velocity);
    right.setControlMode(ControlMode.Velocity);

    // driveRightTalon.setSensorPhase(false);
    // driveLeftTalon.setSensorPhase(true);

    // This needs to be here, as PIDFiZone values are stored in memory
    // TODO clean up tuning
    left.config_IntegralZone(left.getDefaultPidIdx(), 1000);
    right.config_IntegralZone(right.getDefaultPidIdx(), 1000);
    left.config_kI(left.getDefaultPidIdx(), 0.01);
    right.config_kI(right.getDefaultPidIdx(), 0.01);
    left.config_kF(left.getDefaultPidIdx(), 0.1);
    right.config_kF(right.getDefaultPidIdx(), 0.1);
    left.configClosedloopRamp(0);
    left2.configClosedloopRamp(0);
    left3.configClosedloopRamp(0);
    right.configClosedloopRamp(0);
    right2.configClosedloopRamp(0);
    right3.configClosedloopRamp(0);

    left.setSelectedSensorPosition(0);
    right.setSelectedSensorPosition(0);
  }

  @Override
  public void initDefaultCommand() {
    setDefaultCommand(new JoystickDrive());
  }
}

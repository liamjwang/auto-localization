package org.team1540.robot2018.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import org.team1540.base.ChickenSubsystem;
import org.team1540.base.wrappers.ChickenTalon;
import org.team1540.robot2018.RobotMap;
import org.team1540.robot2018.Tuning;
import org.team1540.robot2018.commands.JoystickDrive;

public class DriveTrain extends ChickenSubsystem {

  private ChickenTalon left = new ChickenTalon(RobotMap.DRIVE_LEFT);
  private ChickenTalon left2 = new ChickenTalon(RobotMap.DRIVE_LEFT_2);
  private ChickenTalon left3 = new ChickenTalon(RobotMap.DRIVE_LEFT_3);
  private ChickenTalon[] lefts = new ChickenTalon[]{left, left2, left3};
  private ChickenTalon right = new ChickenTalon(RobotMap.DRIVE_RIGHT);
  private ChickenTalon right2 = new ChickenTalon(RobotMap.DRIVE_RIGHT_2);
  private ChickenTalon right3 = new ChickenTalon(RobotMap.DRIVE_RIGHT_3);
  private ChickenTalon[] rights = new ChickenTalon[]{right, right2, right3};
  private ChickenTalon[] talons = new ChickenTalon[]{left, left2, left3, right, right2, right3};
  private ChickenTalon[] masters = new ChickenTalon[]{left, right};

  public DriveTrain() {
    add(talons);
    setPriority(10);

    left.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
    right.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);

    left.setSensorPhase(true);

    for (ChickenTalon talon : lefts) {
      talon.setInverted(false);
    }

    right.setSensorPhase(true);

    for (ChickenTalon talon : rights) {
      talon.setInverted(true);
    }

    left2.set(ControlMode.Follower, left.getDeviceID());
    left3.set(ControlMode.Follower, left.getDeviceID());

    right2.set(ControlMode.Follower, right.getDeviceID());
    right3.set(ControlMode.Follower, right.getDeviceID());

    for (ChickenTalon talon : talons) {
      talon.setBrake(true);
    }
  }

  @Override
  public void initDefaultCommand() {
    setDefaultCommand(new JoystickDrive());
  }

  public void setLeft(double value) {
    this.left.set(ControlMode.PercentOutput, value);
  }

  public void setRight(double value) {
    this.right.set(ControlMode.PercentOutput, value);
  }

  @Override
  public void periodic() {
    for (ChickenTalon talon : masters) {
      talon.config_kP(0, Tuning.drivetrainP);
      talon.config_kI(0, Tuning.drivetrainI);
      talon.config_kD(0, Tuning.drivetrainD);
      talon.config_kF(0, Tuning.drivetrainF);
      talon.config_IntegralZone(0, Tuning.drivetrainIZone);
    }

    for (ChickenTalon talon : talons) {
      talon.configClosedloopRamp(Tuning.drivetrainRampRate);
    }
  }

  public double getLeftPosition() {
    return left.getSelectedSensorPosition();
  }

  public double getRightPosition() {
    return right.getSelectedSensorPosition();
  }

  public double getLeftVelocity() {
    return left.getSelectedSensorVelocity();
  }

  public double getRightVelocity() {
    return right.getSelectedSensorVelocity();
  }

  public void setLeftVelocity(double velocity) {
    left.set(ControlMode.Velocity, velocity);
  }

  public void setRightVelocity(double velocity) {
    right.set(ControlMode.Velocity, velocity);
  }
}
package org.team1540.robot2018.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import org.team1540.base.ChickenSubsystem;
import org.team1540.base.drive.PidDriveFactory;
import org.team1540.base.drive.PowerJoystickScaling;
import org.team1540.base.wrappers.ChickenTalon;
import org.team1540.robot2018.OI;
import org.team1540.robot2018.RobotMap;
import org.team1540.robot2018.Tuning;

public class DriveTrain extends ChickenSubsystem {

  private ChickenTalon driveLeftMotorA = new ChickenTalon(RobotMap.DRIVE_LEFT_A);
  private ChickenTalon driveLeftMotorB = new ChickenTalon(RobotMap.DRIVE_LEFT_B);
  private ChickenTalon driveLeftMotorC = new ChickenTalon(RobotMap.DRIVE_LEFT_C);
  private ChickenTalon[] driveLeftMotors = new ChickenTalon[]{driveLeftMotorA, driveLeftMotorB, driveLeftMotorC};
  private ChickenTalon driveRightMotorA = new ChickenTalon(RobotMap.DRIVE_RIGHT_A);
  private ChickenTalon driveRightMotorB = new ChickenTalon(RobotMap.DRIVE_RIGHT_B);
  private ChickenTalon driveRightMotorC = new ChickenTalon(RobotMap.DRIVE_RIGHT_C);
  private ChickenTalon[] driveRightMotors = new ChickenTalon[]{driveRightMotorA, driveRightMotorB, driveRightMotorC};
  private ChickenTalon[] driveMotorAll = new ChickenTalon[]{driveLeftMotorA, driveLeftMotorB, driveLeftMotorC, driveRightMotorA, driveRightMotorB, driveRightMotorC};
  private ChickenTalon[] driveMotorMasters = new ChickenTalon[]{driveLeftMotorA, driveRightMotorA};

  public DriveTrain() {
    add(driveMotorAll);
    setPriority(10);

    driveLeftMotorA.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
    driveRightMotorA.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);

    driveLeftMotorA.setSensorPhase(Tuning.isPandora);

    for (ChickenTalon talon : driveLeftMotors) {
      talon.setInverted(false);
    }

    driveRightMotorA.setSensorPhase(true);

    for (ChickenTalon talon : driveRightMotors) {
      talon.setInverted(true);
    }

    driveLeftMotorB.set(ControlMode.Follower, driveLeftMotorA.getDeviceID());
    driveLeftMotorC.set(ControlMode.Follower, driveLeftMotorA.getDeviceID());

    driveRightMotorB.set(ControlMode.Follower, driveRightMotorA.getDeviceID());
    driveRightMotorC.set(ControlMode.Follower, driveRightMotorA.getDeviceID());

    for (ChickenTalon talon : driveMotorAll) {
      talon.setBrake(true);
    }

    for (ChickenTalon talon : driveMotorMasters) {
      talon.config_kP(0, Tuning.drivetrainP);
      talon.config_kI(0, Tuning.drivetrainI);
      talon.config_kD(0, Tuning.drivetrainD);
      talon.config_kF(0, Tuning.drivetrainF);
      talon.config_IntegralZone(0, Tuning.drivetrainIZone);
    }

    for (ChickenTalon talon : driveMotorAll) {
      talon.configClosedloopRamp(Tuning.drivetrainRampRate);
      talon.configOpenloopRamp(Tuning.drivetrainRampRate);
    }
  }

  @Override
  public void initDefaultCommand() {
    setDefaultCommand(new PidDriveFactory()
        .setSubsystem(this)
        .setLeft(driveLeftMotorA)
        .setRight(driveRightMotorA)
        .setJoystick(OI.driver)
        .setLeftAxis(1)
        .setRightAxis(5)
        .setForwardTrigger(3)
        .setBackTrigger(2)
        .setDeadzone(Tuning.axisDeadzone)
        .setScaling(new PowerJoystickScaling(Tuning.drivetrainJoystickPower))
        .setInvertLeft(true)
        .setInvertRight(true)
        .setInvertLeftBrakeDirection(false)
        .setInvertRightBrakeDirection(false)
        .setBrakeOverrideThresh(Tuning.drivetrainBrakeOverrideThreshold)
        .setBrakingStopZone(Tuning.axisDeadzone)
        .setMaxBrakePct(Tuning.drivetrainBrakingPercent)
        .setMaxVel(Tuning.drivetrainVelocity)
        .createPidDrive()
    );
  }

  public void setLeft(double value) {
    this.driveLeftMotorA.set(ControlMode.PercentOutput, value);
  }

  public void setRight(double value) {
    this.driveRightMotorA.set(ControlMode.PercentOutput, value);
  }

  @Override
  public void periodic() {

  }

  public double getLeftPosition() {
    return driveLeftMotorA.getSelectedSensorPosition();
  }

  public double getRightPosition() {
    return driveRightMotorA.getSelectedSensorPosition();
  }

  public double getLeftVelocity() {
    return driveLeftMotorA.getSelectedSensorVelocity();
  }

  public double getRightVelocity() {
    return driveRightMotorA.getSelectedSensorVelocity();
  }

  public void setLeftVelocity(double velocity) {
    driveLeftMotorA.set(ControlMode.Velocity, velocity);
  }

  public void setRightVelocity(double velocity) {
    driveRightMotorA.set(ControlMode.Velocity, velocity);
  }

  public void prepareForMotionProfiling() {
    driveLeftMotorA.setControlMode(ControlMode.Velocity);
    driveRightMotorA.setControlMode(ControlMode.Velocity);

    driveLeftMotorA.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
    driveRightMotorA.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);

    driveLeftMotorA.setSensorPhase(false);
    driveRightMotorA.setSensorPhase(true);

    // This needs to be here, as PIDFiZone values are stored in memory
    driveLeftMotorA.config_IntegralZone(driveLeftMotorA.getDefaultPidIdx(), Tuning.drivetrainIZone);
    driveRightMotorA.config_IntegralZone(driveRightMotorA.getDefaultPidIdx(), Tuning.drivetrainIZone);
    driveLeftMotorA.config_kP(driveLeftMotorA.getDefaultPidIdx(), Tuning.drivetrainP);
    driveRightMotorA.config_kP(driveRightMotorA.getDefaultPidIdx(), Tuning.drivetrainP);
    driveLeftMotorA.config_kI(driveLeftMotorA.getDefaultPidIdx(), Tuning.drivetrainI);
    driveRightMotorA.config_kI(driveRightMotorA.getDefaultPidIdx(), Tuning.drivetrainI);
    driveLeftMotorA.config_kD(driveLeftMotorA.getDefaultPidIdx(), Tuning.drivetrainD);
    driveRightMotorA.config_kD(driveRightMotorA.getDefaultPidIdx(), Tuning.drivetrainD);
    driveLeftMotorA.config_kF(driveLeftMotorA.getDefaultPidIdx(), Tuning.drivetrainF);
    driveRightMotorA.config_kF(driveRightMotorA.getDefaultPidIdx(), Tuning.drivetrainF);
    driveLeftMotorA.configClosedloopRamp(0);
    driveLeftMotorB.configClosedloopRamp(0);
    driveLeftMotorC.configClosedloopRamp(0);
    driveRightMotorA.configClosedloopRamp(0);
    driveRightMotorB.configClosedloopRamp(0);
    driveRightMotorC.configClosedloopRamp(0);

    driveLeftMotorA.setSelectedSensorPosition(0);
    driveRightMotorA.setSelectedSensorPosition(0);
  }

  public void zeroEncoders() {
    driveLeftMotorA.setSelectedSensorPosition(0);
    driveLeftMotorB.setSelectedSensorPosition(0);
  }
}

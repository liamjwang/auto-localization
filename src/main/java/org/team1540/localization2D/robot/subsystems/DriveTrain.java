package org.team1540.localization2D.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.localization2D.datastructures.twod.Twist2D;
import org.team1540.localization2D.robot.Robot;
import org.team1540.localization2D.robot.RobotMap;
import org.team1540.localization2D.robot.Tuning;
import org.team1540.localization2D.robot.commands.drivetrain.PercentDrive;
import org.team1540.localization2D.robot.commands.drivetrain.VelocityDrive;
import org.team1540.rooster.wrappers.ChickenTalon;

public class DriveTrain extends Subsystem {

  public ChickenTalon driveLeftMotorA = new ChickenTalon(RobotMap.DRIVE_LEFT_A);
  private ChickenTalon driveLeftMotorB = new ChickenTalon(RobotMap.DRIVE_LEFT_B);
  private ChickenTalon driveLeftMotorC = new ChickenTalon(RobotMap.DRIVE_LEFT_C);
  private ChickenTalon[] driveLeftMotors = new ChickenTalon[]{driveLeftMotorA, driveLeftMotorB, driveLeftMotorC};
  public ChickenTalon driveRightMotorA = new ChickenTalon(RobotMap.DRIVE_RIGHT_A);
  private ChickenTalon driveRightMotorB = new ChickenTalon(RobotMap.DRIVE_RIGHT_B);
  private ChickenTalon driveRightMotorC = new ChickenTalon(RobotMap.DRIVE_RIGHT_C);
  private ChickenTalon[] driveRightMotors = new ChickenTalon[]{driveRightMotorA, driveRightMotorB, driveRightMotorC};
  private ChickenTalon[] driveMotorAll = new ChickenTalon[]{driveLeftMotorA, driveLeftMotorB, driveLeftMotorC, driveRightMotorA, driveRightMotorB, driveRightMotorC};
  private ChickenTalon[] driveMotorMasters = new ChickenTalon[]{driveLeftMotorA, driveRightMotorA};

  public DriveTrain() {
    reset();
  }

  @Override
  public void initDefaultCommand() {
    setDefaultCommand(new VelocityDrive());
    // setDefaultCommand(new PercentDrive());
  }

  public void setLeft(ControlMode mode, double value) {
    this.driveLeftMotorA.set(mode, value);
  }

  public void setLeft(ControlMode mode, double value, double bump) {
    this.driveLeftMotorA.set(mode, value, DemandType.ArbitraryFeedForward, bump);
  }

  public void setRight(ControlMode mode, double value) {
    this.driveRightMotorA.set(mode, value);
  }

  public void setRight(ControlMode mode, double value, double bump) {
    this.driveRightMotorA.set(mode, value, DemandType.ArbitraryFeedForward, bump);
  }

  public void setLeftPercent(double value) {
    this.driveLeftMotorA.set(ControlMode.PercentOutput, value);
  }

  public void setRightPercent(double value) {
    this.driveRightMotorA.set(ControlMode.PercentOutput, value);
  }

  public void setLeftVelocity(double velocity) {
    driveLeftMotorA.set(ControlMode.Velocity, velocity);
  }

  public void setRightVelocity(double velocity) {
    driveRightMotorA.set(ControlMode.Velocity, velocity);
  }

  public void setLeftVelocityMetersPerSecond(double velocity) {
    setLeftVelocity(velocity / 10 * Tuning.drivetrainTicksPerMeter);
  }

  public void setRightVelocityMetersPerSecond(double velocity) {
    setRightVelocity(velocity / 10 * Tuning.drivetrainTicksPerMeter);
  }

  public void setTwist(Twist2D cmdVel) {
    double leftSetpoint = (cmdVel.getX() - cmdVel.getOmega() * Tuning.drivetrainRadius);
    double rightSetpoint = (cmdVel.getX() + cmdVel.getOmega() * Tuning.drivetrainRadius);
    setLeftVelocityMetersPerSecond(leftSetpoint);
    setRightVelocityMetersPerSecond(rightSetpoint);
  }

  public void configTalonsForPosition() {
    for (ChickenTalon talon : driveMotorMasters) {
      talon.config_kP(0, Tuning.drivetrainPositionP);
      talon.config_kI(0, 0);
      talon.config_kD(0, Tuning.drivetrainPositionD);
      talon.config_kF(0, 0);
      talon.config_IntegralZone(0, 0);
    }
  }

  public void configTalonsForVelocity() {
    for (ChickenTalon talon : driveMotorMasters) {
      talon.config_kP(0, Tuning.drivetrainVelocityP);
      talon.config_kI(0, Tuning.drivetrainVelocityI);
      talon.config_kD(0, Tuning.drivetrainVelocityD);
      talon.config_kF(0, 0);
      talon.config_IntegralZone(0, Tuning.drivetrainVelocityIZone);
    }
  }

  public void reset() {
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

    setBrake(true);

    configTalonsForVelocity();

    for (ChickenTalon talon : driveMotorAll) {
      talon.configClosedloopRamp(Tuning.drivetrainRampRate);
      talon.configOpenloopRamp(Tuning.drivetrainRampRate);
      talon.configPeakOutputForward(1);
      talon.configPeakOutputReverse(-1);
      talon.enableCurrentLimit(false);
    }
  }

  public void enableCurrentLimiting() {
    System.out.println("Current limiting enabled!");
    for (ChickenTalon talon : driveMotorAll) {
      talon.configPeakCurrentLimit(0, 20); // Set peak to zero to just use continuous current limit
      talon.configPeakCurrentDuration(0, 20);
      talon.configContinuousCurrentLimit(Tuning.drivetrainCurrentLimit, 20);
      talon.enableCurrentLimit(true);
    }
  }

  public void setBrake(Boolean state) {
    for (ChickenTalon talon : driveMotorAll) {
      talon.setBrake(state);
    }
  }

  public double getLeftPosition() {
    return driveLeftMotorA.getSelectedSensorPosition();
  }

  public double getRightPosition() {
    return driveRightMotorA.getSelectedSensorPosition();
  }

  public double getLeftPositionMeters() {
    return getLeftPosition() / Tuning.drivetrainTicksPerMeter;
  }

  public double getRightPositionMeters() {
    return getRightPosition() / Tuning.drivetrainTicksPerMeter;
  }

  public double getLeftVelocity() {
    return driveLeftMotorA.getSelectedSensorVelocity();
  }

  public double getRightVelocity() {
    return driveRightMotorA.getSelectedSensorVelocity();
  }

  public double getLeftVelocityMetersPerSecond() {
    return getLeftVelocity() * 10 / Tuning.drivetrainTicksPerMeter;
  }

  public double getRightVelocityMetersPerSecond() {
    return getRightVelocity() * 10 / Tuning.drivetrainTicksPerMeter;
  }

  public Twist2D getTwist() {
    double xvel = (getLeftVelocityMetersPerSecond() + getRightVelocityMetersPerSecond()) / 2;
    double thetavel = (getLeftVelocityMetersPerSecond() - getRightVelocityMetersPerSecond()) / (Tuning.drivetrainRadius) / 2;
    SmartDashboard.putNumber("LineupDebug/Actual/x", xvel);
    SmartDashboard.putNumber("LineupDebug/Actual/z", -thetavel);
    return new Twist2D(xvel, 0, thetavel);
  }

  @Override
  public void periodic() {
  }

  public void zeroEncoders() {
    driveLeftMotorA.setSelectedSensorPosition(0);
    driveRightMotorA.setSelectedSensorPosition(0);
  }

  public void stop() {
    setLeftVelocity(0);
    setRightVelocity(0);
  }
}

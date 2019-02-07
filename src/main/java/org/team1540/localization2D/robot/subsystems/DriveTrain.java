package org.team1540.localization2D.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import edu.wpi.first.networktables.NetworkTableInstance;
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
    NetworkTableInstance.getDefault().getTable("Debug/DriveTrain/CmdTankVelRaw").getEntry("left").setNumber(velocity);
  }

  public void setRightVelocity(double velocity) {
    driveRightMotorA.set(ControlMode.Velocity, velocity);
    NetworkTableInstance.getDefault().getTable("Debug/DriveTrain/CmdTankVelRaw").getEntry("right").setNumber(velocity);
  }

  public void setLeftVelocityMetersPerSecond(double velocity) {
    setLeftVelocity(velocity / 10 * Tuning.drivetrainTicksPerMeter);
    NetworkTableInstance.getDefault().getTable("Debug/DriveTrain/CmdTankVel").getEntry("left").setNumber(velocity);
  }

  public void setRightVelocityMetersPerSecond(double velocity) {
    setRightVelocity(velocity / 10 * Tuning.drivetrainTicksPerMeter);
    NetworkTableInstance.getDefault().getTable("Debug/DriveTrain/CmdTankVel").getEntry("right").setNumber(velocity);
  }

  public void setTwist(Twist2D cmdVel) {
    double leftSetpoint = (cmdVel.getX() - cmdVel.getOmega() * Tuning.drivetrainRadius);
    double rightSetpoint = (cmdVel.getX() + cmdVel.getOmega() * Tuning.drivetrainRadius);
    setLeftVelocityMetersPerSecond(leftSetpoint);
    setRightVelocityMetersPerSecond(rightSetpoint);
    cmdVel.putToNetworkTable("Debug/DriveTrain/CmdVel");
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
    int posRaw = driveLeftMotorA.getSelectedSensorPosition();
    NetworkTableInstance.getDefault().getTable("Debug/DriveTrain/TankPosRaw").getEntry("left").setNumber(posRaw);
    return posRaw;
  }

  public double getRightPosition() {
    int posRaw = driveRightMotorA.getSelectedSensorPosition();
    NetworkTableInstance.getDefault().getTable("Debug/DriveTrain/TankPosRaw").getEntry("right").setNumber(posRaw);
    return posRaw;
  }

  public double getLeftPositionMeters() {
    double pos = getLeftPosition() / Tuning.drivetrainTicksPerMeter;
    NetworkTableInstance.getDefault().getTable("Debug/DriveTrain/TankPos").getEntry("left").setNumber(pos);
    return pos;
  }

  public double getRightPositionMeters() {
    double pos = getRightPosition() / Tuning.drivetrainTicksPerMeter;
    NetworkTableInstance.getDefault().getTable("Debug/DriveTrain/TankPos").getEntry("right").setNumber(pos);
    return pos;
  }

  public double getLeftVelocity() {
    int rawVelocity = driveLeftMotorA.getSelectedSensorVelocity();
    NetworkTableInstance.getDefault().getTable("Debug/DriveTrain/TankVelRaw").getEntry("left").setNumber(rawVelocity);
    return rawVelocity;
  }

  public double getRightVelocity() {
    int rawVelocity = driveRightMotorA.getSelectedSensorVelocity();
    NetworkTableInstance.getDefault().getTable("Debug/DriveTrain/TankVelRaw").getEntry("right").setNumber(rawVelocity);
    return rawVelocity;
  }

  public double getLeftVelocityMetersPerSecond() {
    double velocity = getLeftVelocity() * 10 / Tuning.drivetrainTicksPerMeter;
    NetworkTableInstance.getDefault().getTable("Debug/DriveTrain/TankVel").getEntry("left").setNumber(velocity);
    return velocity;
  }

  public double getRightVelocityMetersPerSecond() {
    double velocity = getRightVelocity() * 10 / Tuning.drivetrainTicksPerMeter;
    NetworkTableInstance.getDefault().getTable("Debug/DriveTrain/TankVel").getEntry("right").setNumber(velocity);
    return velocity;
  }

  public Twist2D getTwist() {
    double xvel = (getLeftVelocityMetersPerSecond() + getRightVelocityMetersPerSecond()) / 2;
    double thetavel = (getLeftVelocityMetersPerSecond() - getRightVelocityMetersPerSecond()) / (Tuning.drivetrainRadius) / 2;
    Twist2D twist2D = new Twist2D(xvel, 0, thetavel);
    twist2D.putToNetworkTable("Debug/DriveTrain/Vel");
    return twist2D;
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

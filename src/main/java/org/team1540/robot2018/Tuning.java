package org.team1540.robot2018;

import jaci.pathfinder.Trajectory.Config;
import jaci.pathfinder.Trajectory.FitMethod;
import org.team1540.base.adjustables.Tunable;

public class Tuning {

  // GENERAL
  @Tunable("-[General] Dead Zone")
  public static double axisDeadzone = 0.1;

  @Tunable("-[General] Manual Superstructure Control Deadzone") // Deadzone for wrist and lift
  public static double axisWristLiftDeadzone = 0.2;

  // TODO: Better method of switching tuning values globally between robots
  @Tunable("-[General] Is Pandora")
  public static boolean isPandora = true;

  // CAMERA
  public static int camID = 0;

  // AUTO
  @Tunable("-[Auto] Stupid Drive Time")
  public static double stupidDriveTime = 2.5;

  // INTAKE
  @Tunable("[Intake] Auto Intake Spike Current")
  public static double intakeSpikeCurrent = 30.0;

  @Tunable("[Intake] Auto Intake Speed Motor A")
  public static double intakeSpeedA = -1;

  @Tunable("[Intake] Auto Intake Speed Motor B")
  public static double intakeSpeedB = -0.4;

  @Tunable("[Intake] Auto Intake Min Time")
  public static double intakeMinTime = 1;

  @Tunable("[Intake] Auto Intake Max Time")
  public static double intakeMaxTime = 10;

  @Tunable("[Intake] Intake Hold Speed")
  public static double intakeHoldSpeed = -0.1;

  @Tunable("[Intake] Eject Seconds")
  public static double ejectTime = 1.0;

  @Tunable("[Intake] Eject Speed Motor A")
  public static double ejectSpeedA = 0.8;

  @Tunable("[Intake] Eject Speed Motor B")
  public static double ejectSpeedB = 0.8;

  @Tunable("[Intake] Arm Hold Speed")
  public static double intakeArmHoldSpeed = -0.1;

  @Tunable("[Intake] Arm Joystick Constant")
  public static double intakeArmJoystickConstant = 0.5;

  // ELEVATOR
  public static double elevatorP = 2;

  public static double elevatorI = 0.0025;

  public static double elevatorD = 10;

  @Tunable("[Elevator] kF Going Up")
  public static double elevatorFGoingUp = 2.046;

  @Tunable("[Elevator] kF Going Down")
  public static double elevatorFGoingDown = 1.2;

  public static int elevatorIZone = 150;

  @Tunable("[Elevator] Error Tolerance")
  public static double elevatorTolerance = 100;

  public static int elevatorMaxAccel = 750;

  public static int elevatorCruiseVel = 475;

  public static double elevatorGroundPosition = 5;

  public static double elevatorExchangePosition = 500;

  public static double elevatorFrontSwitchPosition = 2900;

  public static double elevatorLowScalePosition = 6200;

  public static double elevatorMaxPosition = 7400;

  public static double elevatorRungPosition; //TODO

  @Tunable("[Elevator] Obstacle Position")
  public static double elevatorObstaclePosition = 1300;

  @Tunable("[Elevator] Obstacle Upper Position")
  public static double elevatorObstacleUpperPosition = 3750;

  @Tunable("[Elevator] Max Elevator Deviation")
  public static double maxElevatorDeviation = 200;

  @Tunable("[Elevator] Down Multiplier")
  public static double elevatorDownMult = 0.5;

  @Tunable("[Elevator] Max Spike Time")
  public static double elevatorSpikeTime = 0;

  @Tunable("[Elevator] Max Spike Current")
  public static double elevatorCurrentThreshold = 60;

  // WRIST
  public static double wristP = 10.0;

  public static double wristI = 0;

  public static double wristD = 0;

  public static double wristF = 1.364;

  public static int wristIzone = 0;

  public static int wristCruiseVelocity = 600;

  public static int wristMaxAccel = 1000;

  @Tunable("[Wrist] Peak Current Limit")
  public static int wristCurrentLimit = 30;

  @Tunable("[Wrist] Peak Duration")
  public static int wristPeakDuration = 0;

  @Tunable("[Wrist] Stall Current")
  public static double wristStallCurrent = 30;

  @Tunable("[Wrist] Stop Tolerance")
  public static double wristTolerance = 50;

  public static double wristOutPosition = 8250;

  public static double wristBackPosition = 0;

  public static double wristTransitPosition = 3900;

  public static double wrist45BackPosition = 1500;

  public static double wrist45FwdPosition = 6200;

  @Tunable("[Wrist] Max Wrist Deviation")
  public static double maxWristDeviation = 200;

  // DRIVETRAIN
  public static double drivetrainP = 2;

  public static double drivetrainI = 0.001;

  public static double drivetrainD = 4;

  public static double drivetrainF = 1.2;

  public static int drivetrainIZone = 100;

  public static double drivetrainBrakingPercent = 0.2;

  public static double drivetrainBrakeOverrideThreshold = 0.9;

  public static double drivetrainRampRate = 0.1;

  public static double drivetrainVelocity = 750;

  public static double drivetrainJoystickPower = 2;

  public static double drivetrainEncoderTPU = 52;

  // Units in inches and seconds
  @Tunable("[MotionP] MaxVelocity")
  public static double maxVelocity = 40;
  @Tunable("[MotionP] MaxVelocityFast")
  public static double maxVelocityFast = 80;
  @Tunable("[MotionP] MaxAcceleration")
  public static double maxAcceleration = 40;
  @Tunable("[MotionP] MaxJerk")
  public static double maxJerk = 2300;
  @Tunable("[MotionP] SecondsFromNeutralToFull")
  public static double secondsFromNeutralToFull = 0;
  @Tunable("[MotionP] SampleRate")
  public static int sampleRate = Config.SAMPLES_FAST;
  @Tunable("[MotionP] TimeStep")
  public static double timeStep = 0.05;
  public static FitMethod fitMethod = FitMethod.HERMITE_CUBIC;

  @Tunable("[MotionP] LeftEncoderTicksPerUnit")
  public static double lEncoderTicksPerUnit = 52;
  @Tunable("[MotionP] RightEncoderTicksPerUnit")
  public static double rEncoderTicksPerUnit = 52;
  @Tunable("[MotionP] WheelbaseWidth")
  public static double wheelbaseWidth = 25.091;
  @Tunable("[MotionP] distanceBetweenWheels")
  public static double distanceBetweenWheels = 11.812;
  @Tunable("[MotionP] Profile Heading P")
  public static double profileHeadingP = 0;
  @Tunable("[MotionP] Profile Acceleration P")
  public static double profileAccelP = 0;
  @Tunable("[MotionP] Profile Position P")
  public static double profilePositionP = 0;
  @Tunable("[MotionP] Profile Loop Frequency")
  public static double profileLoopFrequency;
}

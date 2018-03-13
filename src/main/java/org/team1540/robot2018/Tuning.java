package org.team1540.robot2018;

import jaci.pathfinder.Trajectory.Config;
import jaci.pathfinder.Trajectory.FitMethod;
import org.team1540.base.adjustables.Tunable;

public class Tuning {

  // GENERAL
  public static double axisDeadzone = 0.1;

  // Deadzone for wrist and lift
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
  public static double intakeSpikeCurrent = 30.0;

  public static double intakeSpeedA = -1;

  public static double intakeSpeedB = -0.4;

  public static double intakeMinTime = 1;

  public static double intakeMaxTime = 10;

  public static double intakeHoldSpeed = -0.1;

  public static double intakeEjectTime = 1.0;

  public static double intakeEjectSpeed = 0.8;

  // ARMS
  public static double armHoldSpeed = -0.1;

  public static double armJoystickConstant = 0.5;

  // ELEVATOR
  public static double elevatorP = 2;

  public static double elevatorI = 0.0025;

  public static double elevatorD = 10;

  public static double elevatorFGoingUp = 2.046;

  public static double elevatorFGoingDown = 1.2;

  public static int elevatorIZone = 150;

  public static double elevatorTolerance = 100;

  public static int elevatorMaxAccel = 750;

  public static int elevatorCruiseVel = 475;

  public static double elevatorGroundPosition = 5;

  public static double elevatorExchangePosition = 500;

  public static double elevatorFrontSwitchPosition = 2900;

  public static double elevatorLowScalePosition = 6200;

  public static double elevatorMaxPosition = 7400;

  public static double elevatorObstaclePosition = 1300;

  public static double elevatorObstacleUpperPosition = 3750;

  public static double elevatorMaxDeviation = 200;

  public static double elevatorDownMult = 0.5;

  public static double elevatorSpikeTime = 0;

  public static double elevatorCurrentThreshold = 60;

  // WRIST
  public static double wristP = 10.0;

  public static double wristI = 0;

  public static double wristD = 0;

  public static double wristF = 1.364;

  public static int wristIzone = 0;

  public static int wristCruiseVelocity = 600;

  public static int wristMaxAccel = 1000;

  public static int wristCurrentLimit = 30;

  public static int wristPeakDuration = 0;

  public static double wristStallCurrent = 30;

  public static double wristTolerance = 50;

  public static double wristOutPosition = 8250;

  public static double wristBackPosition = 0;

  public static double wristTransitPosition = 3900;

  public static double wrist45BackPosition = 1500;

  public static double wrist45FwdPosition = 6200;

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

  public static double drivetrainMaxVelocity = 750;

  public static double drivetrainJoystickPower = 2;

  public static double drivetrainEncoderTPU;

  public static double profileMaxVel = 40;

  public static double profileMaxAccel = 40;

  public static double profileMaxJerk = 2300;

  public static double profileSecondsFromNeutralToFull = 0;

  public static int profileSampleRate = Config.SAMPLES_FAST;

  public static double profileTimeStep = 0.05;

  public static FitMethod profileFitMethod = FitMethod.HERMITE_CUBIC;

  public static double profileLeftTPU = 52;

  public static double profileRightTPU = 52;

  public static double profileBaseWidth = 25.091;

  // TODO: Rename profileWheelDistance to be more specific
  public static double profileWheelDistance = 11.812;
}

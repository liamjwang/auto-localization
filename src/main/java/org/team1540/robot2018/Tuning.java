package org.team1540.robot2018;

import jaci.pathfinder.Trajectory.Config;
import org.team1540.base.adjustables.Tunable;

public class Tuning {
  @Tunable("Dead Zone")
  public static double deadZone = 0.1;

  // Units in inches and seconds
  @Tunable("mpMaxVelocity")
  public static double maxVelocity = 40;
  @Tunable("mpMaxAcceleration")
  public static double maxAcceleration = 40;
  @Tunable("mpMaxJerk")
  public static double maxJerk = 2300;
  @Tunable("mpSecondsFromNeutralToFull")
  public static double secondsFromNeutralToFull = 0;
  @Tunable("mpSampleRate")
  public static int sampleRate = Config.SAMPLES_HIGH;
  @Tunable("mpTimeStep")
  public static double timeStep = 0.05;
  @Tunable("mpDistanceToTravel")
  public static double distanceToTravel = 78.74;
  
  @Tunable("lEncoderTicksPerUnit")
  public static double lEncoderTicksPerUnit = 8289/159;
  @Tunable("rEncoderTicksPerUnit")
  public static double rEncoderTicksPerUnit = 8358/159;
  @Tunable("wheelbaseWidth")
  public static double wheelbaseWidth = 25.091;
  @Tunable("distanceBetweenWheels")
  public static double distanceBetweenWheels = 11.812;

  @Tunable("Drivetrain P")
  public static double driveTrainP = 1.5;
  @Tunable("Drivetrain I")
  public static double driveTrainI = 0.001;
  @Tunable("Drivetrain D")
  public static double driveTrainD = 4;
  @Tunable("Drivetrain F")
  public static double driveTrainF = 1.2;

  // @Tunable("Auto Intake Spike Current")
  // public static double IntakeSpikeCurrent = 5.0;
  //
  // @Tunable("Auto Intake Spike Length")
  // public static double IntakeSpikeLength = 1.0;
  //
  // @Tunable("Auto Intake Speed Motor A")
  // public static double IntakeSpeedA = 1;
  //
  // @Tunable("Auto Intake Speed Motor B")
  // public static double IntakeSpeedB = 0.4;
  //
  // @Tunable("Eject Seconds")
  // public static double EjectTime = 1.0;
  //
  // @Tunable("Eject Speed Motor A")
  // public static double EjectSpeedA = -1;
  //
  // @Tunable("Eject Speed Motor B")
  // public static double EjectSpeedB = -1;
  //
  //
  // @Tunable("Elevator I")
  // public static double elevatorI = 0;
  //
  // @Tunable("Elevator P")
  // public static double elevatorP = 0.1;
  //
  // @Tunable("Elevator D")
  // public static double elevatorD = 1;
  //
  // @Tunable("Elevator Up Setpoint")
  // public static double elevatorUpLimit = 0;
  //
  // @Tunable("Elevator Down Setpoint")
  // public static double elevatorDownLimit = 0;
  //
  // @Tunable("Elevator Bounce Back")
  // public static double elevatorBounceBack = 10;
  //
  // @Tunable("Elevator Multiplier")
  // public static double elevatorMult = 0.1;
  //
  //
  // @Tunable("Motion Cruise Velocity")
  // public static int wristCruiseVelocity = 600;
  //
  // @Tunable("Wrist I")
  // public static double wristI = 0;
  // @Tunable("Motion Max Accelleration")
  // public static int wristMaxAccel = 1000;
  // @Tunable("Wrist D")
  // public static double wristD = 0;
  // @Tunable("Wrist F")
  // public static double wristF = 1.364;
  // @Tunable("Wrist I Zone")
  // public static int wristIzone = 0;
  // @Tunable("Wrist P")
  // public static double wristP = 10.0;
  //
  // @Tunable("Wrist Up Setpoint")
  // public static double wristUpLimit = -8300; //Wrist 45 deg: -7000, Vertical: 4500
  //
  // @Tunable("Wrist Down Setpoint")
  // public static double wristDownLimit = 0;
  //
  // @Tunable("Wrist Bounce Back")
  // public static double wristBounceBack = 10;
  //
  // @Tunable("Wrist Multiplier")
  // public static double wristMult = 0.1;
  //
  //
  // @Tunable("Winch In Speed")
  // public static double winchInSpeed = 0.2;
  //
  // @Tunable("Winch Out Speed")
  // public static double winchOutSpeed = -0.2; // 0.75
  //
  // @Tunable("Tape In Speed")
  // public static double tapeInSpeed = 0.5;
  //
  // @Tunable("Tape Out Speed")
  // public static double tapeOutSpeed = -0.25;
  //
  // @Tunable("Tape Measure Multiplier")
  // public static double tapeMeasureMultiplier = 1;
  //
  // @Tunable("Winch Multiplier")
  // public static double winchMultiplier = 1;
  //
  // @Tunable("Climber In Speed")
  // public static double climberInSpeed = 0.5;
  //
  //
  // @Tunable("Manual Elevator Up Speed")
  // public static double manualElevatorUpSpeed = 1;
  //
  // @Tunable("Manual Elevator Down Speed")
  // public static double manualElevatorDownSpeed = -0.5;
  //
  @Tunable("Standard Deadzone")
  public static double standardDeadzone = 0.1;
}

package org.team1540.robot2018;

import org.team1540.base.adjustables.Tunable;

public class Tuning {
  public static final double MAX_ELEVATOR_DEVIATION = 200;
  public static final double MAX_WRIST_DEVIATION = 200;

  // GENERAL
  @Tunable("Dead Zone")
  public static double deadZone = 0.1;

  // INTAKE
  @Tunable("Auto Intake Spike Current")
  public static double IntakeSpikeCurrent = 5.0;

  @Tunable("Auto Intake Spike Length")
  public static double IntakeSpikeLength = 1.0;

  @Tunable("Auto Intake Speed Motor A")
  public static double IntakeSpeedA = 1;

  @Tunable("Auto Intake Speed Motor B")
  public static double IntakeSpeedB = 0.4;

  @Tunable("Eject Seconds")
  public static double EjectTime = 1.0;

  @Tunable("Eject Speed Motor A")
  public static double EjectSpeedA = -1;

  @Tunable("Eject Speed Motor B")
  public static double EjectSpeedB = -1;


  // ELEVATOR
  @Tunable("Elevator P")
  public static double elevatorP = 2;

  @Tunable("Elevator I")
  public static double elevatorI = 0;

  @Tunable("Elevator D")
  public static double elevatorD = 0;

  @Tunable("Elevator F Upwards")
  public static double elevatorFGoingUp = 2.5575;
  @Tunable("Elevator F Downwards")
  public static double elevatorFGoingDown;

  @Tunable("Elevator Tolerance")
  public static double elevatorTolerance;

  @Tunable("Elevator Max Acceleration")
  public static int elevatorMaxAccel = 300;

  @Tunable("Elevator Cruise Velocity")
  public static int elevatorCruiseVel = 400;

  @Tunable("Elevator Up Setpoint")
  public static double elevatorUpLimit = 0;

  @Tunable("Elevator Down Setpoint")
  public static double elevatorDownLimit = 0;

  @Tunable("Elevator Bounce Back")
  public static double elevatorBounceBack = 10;

  @Tunable("Elevator Multiplier")
  public static double elevatorMult = 100;

  @Tunable("Elevator Ground Position")
  public static double elevatorGroundPosition = 0;

  @Tunable("Elevator Exchange Position")
  public static double elevatorExchangePosition = 500;

  @Tunable("Elevator Front Switch Position")
  public static double elevatorFrontSwitchPosition = 2900;

  @Tunable("Elevator Scale Lower Position")
  public static double elevatorScalePosition = 7400;

  @Tunable("Elevator Low Scale Position")
  public static double elevatorLowScalePosition = 6200;

  @Tunable("Elevator Obstacle Position")
  public static double elevatorObstaclePosition = 1300;

  @Tunable("Elevator Obstacle Upper Position")
  public static double elevatorObstacleUpperPosition = 3750;

  // WRIST
  @Tunable("Wrist P")
  public static double wristP = 0;

  @Tunable("Wrist I")
  public static double wristI = 0;

  @Tunable("Wrist D")
  public static double wristD = 0;

  @Tunable("Wrist Stop Tolerance")
  public static double wristTolerance;

  @Tunable("Wrist Up Setpoint")
  public static double wristUpLimit = 0;

  @Tunable("Wrist Down Setpoint")
  public static double wristDownLimit = 0;

  @Tunable("Wrist Bounce Back")
  public static double wristBounceBack = 10;

  @Tunable("Wrist Multiplier")
  public static double wristMult = 0.1;

  @Tunable("Wrist Out Position")
  public static double wristOutPosition = 0;
  @Tunable("Wrist Back Position")
  public static double wristBackPosition = 0;
  @Tunable("Wrist Transit Position")
  public static double wristTransitPosition = 0;
  @Tunable("Wrist 45 Back Position")
  public static double wrist45BackPosition = 0;
  @Tunable("Wrist 45 Forward Position")
  public static double wrist45FwdPosition = 0;
  // WINCH
  @Tunable("Winch In Speed")
  public static double winchInSpeed = 1;

  @Tunable("Winch Out Speed")
  public static double winchOutSpeed = -1; // 0.75

  // TAPE
  @Tunable("Tape In Speed")
  public static double tapeInSpeed = 0.5;

  @Tunable("Tape Out Speed")
  public static double tapeOutSpeed = -0.25;

  @Tunable("Tape Measure Multiplier")
  public static double tapeMeasureMultiplier = 1;

  @Tunable("Winch Multiplier")
  public static double winchMultiplier = 1;

  @Tunable("Climber In Speed")
  public static double climberInSpeed = 0.5;

  @Tunable("Climber Out Speed")
  public static double climberOutSpeed;

  @Tunable("Manual Elevator Up Speed")
  public static double manualElevatorUpSpeed = 1;

  @Tunable("Manual Elevator Down Speed")
  public static double manualElevatorDownSpeed = -0.5;


  @Tunable("Standard Deadzone")
  public static double standardDeadzone = 0.1;
}

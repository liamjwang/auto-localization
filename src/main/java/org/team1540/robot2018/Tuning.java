package org.team1540.robot2018;

import org.team1540.base.adjustables.Tunable;

public class Tuning {
  @Tunable("Dead Zone")
  public static double deadZone = 0.1;

  @Tunable("Auto Intake Spike Current")
  public static double IntakeSpikeCurrent = 5.0;

  @Tunable("Auto Intake Spike Length")
  public static double IntakeSpikeLength = 1.0;

  @Tunable("Auto Intake Speed Motor A")
  public static double IntakeSpeedA = 0.5;

  @Tunable("Auto Intake Speed Motor B")
  public static double IntakeSpeedB = 0.5;

  @Tunable("Eject Seconds")
  public static double EjectTime = 1.0;

  @Tunable("Eject Speed Motor A")
  public static double EjectSpeedA = 0.5;

  @Tunable("Eject Speed Motor B")
  public static double EjectSpeedB = 0.5;

  @Tunable("Wrist P")
  public static double wristP = 1;

  @Tunable("Wrist I")
  public static double wristI = 1;

  @Tunable("Wrist D")
  public static double wristD = 1;

  @Tunable("Wrist Up Setpoint")
  public static double wristUpLimit = 0;

  @Tunable("Wrist Down Setpoint")
  public static double wristDownLimit = 0;

  @Tunable("Wrist Bounce Back")
  public static double wristBounceBack = 10;

  @Tunable("Wrist Multiplier")
  public static double wristMult = 0.1;

  @Tunable("Winch In Speed")
  public static double winchInSpeed = 0.2;

  @Tunable("Winch Out Speed")
  public static double winchOutSpeed = -0.75;

  @Tunable("Tape In Speed")
  public static double tapeInSpeed = 0.5;

  @Tunable("Tape Out Speed")
  public static double tapeOutSpeed = -0.75;

  @Tunable("Tape Measure Multiplier")
  public static double tapeMeasureMultiplier = 0;

  @Tunable("Winch Multiplier")
  public static double winchMultiplier = 0;

  @Tunable("Climber In Speed")
  public static double climberInSpeed = 0.5;
}

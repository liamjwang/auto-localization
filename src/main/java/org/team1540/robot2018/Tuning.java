package org.team1540.robot2018;

import org.team1540.base.adjustables.Tunable;

public class Tuning {
  @Tunable("Dead Zone")
  public static double deadZone = 0.1;

  @Tunable("Auto Intake Spike Current")
  public static double IntakeSpikeCurrent = 5.0;

  @Tunable("Auto Intake Spike Length")
  public static double IntakeSpikeLength = 1.0;

  @Tunable("Auto Intake Speed")
  public static double IntakeSpeed = 0.5;

  @Tunable("Eject Seconds")
  public static double EjectTime = 1.0;

  @Tunable("Eject Speed")
  public static double EjectSpeed = 0.5;
}

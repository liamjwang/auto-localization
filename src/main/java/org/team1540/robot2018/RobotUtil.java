package org.team1540.robot2018;

import org.team1540.base.Utilities;

public class RobotUtil {
  public static double deadzone(double value) {
    return Utilities.processAxisDeadzone(value, Tuning.standardDeadzone);
  }
}

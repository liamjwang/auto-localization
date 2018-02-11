package org.team1540.robot2018;

public class RobotUtil {

    public static double deadzone(double value, double deadzone) {
        if (Math.abs(value) < deadzone) {
            return 0;
        }
        return value;
    }

    public static double deadzone(double value) {
        return deadzone(value, Tuning.standardDeadzone);
    }

}

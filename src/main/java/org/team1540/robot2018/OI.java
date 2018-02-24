package org.team1540.robot2018;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import org.team1540.base.Utilities;
import org.team1540.base.triggers.AxisButton;
import org.team1540.base.triggers.DPadButton;
import org.team1540.base.triggers.DPadButton.DPadAxis;

/*
 * Button Mapping
 * X: 3
 * Y: 4
 * A: 1
 * B: 2
 * Back: 7
 * Start: 8
 * LB: 5
 * RB: 6
 * Right joystick center: 9
 * Left joystick center: 10
 *
 * Axis Mapping:
 * Left analog trigger: 2 (Only positive values)
 * Right analog trigger: 3 (Only positive values)
 *
 * Left joystick:
 * Up/Down: 1 (Up is negative)
 * Left/Right: 0 (Left is negative)
 *
 * Right joystick:
 * Up/Down: 5 (Up is negative)
 * Left/Right: 4 (Left is negative)
 */


public class OI {

  private static Joystick driver = new Joystick(0);
  private static Joystick copilot = new Joystick(1);

  public static final int X = 3;
  public static final int Y = 4;
  public static final int A = 1;
  public static final int B = 2;

  public static final int BACK = 7;
  public static final int START = 8;
  public static final int LB = 5;
  public static final int RB = 6;

  // auto intake
  static Button copilotLB = new JoystickButton(copilot, LB);
  // auto eject
  static Button copilotRB = new JoystickButton(copilot, RB);

  // wrist to forward 45 degrees
  static Button copilotX = new JoystickButton(copilot, X);
  // wrist to full down
  static Button copilotA = new JoystickButton(copilot, A);
  // wrist to backward 45 degrees
  static Button copilotY = new JoystickButton(copilot, Y);
  // wrist to full back
  static Button copilotB = new JoystickButton(copilot, B);

  // to max height + wrist up
  static Button copilotDPadUp = new DPadButton(copilot, 0, DPadAxis.UP);
  // to lower scale height
  static Button copilotDPadDown = new DPadButton(copilot, 0, DPadAxis.DOWN);
  // to max scale height (no wrist)
  static Button copilotDPadLeft = new DPadButton(copilot, 0, DPadAxis.LEFT);
  // to switch height
  static Button copilotDPadRight = new DPadButton(copilot, 0, DPadAxis.RIGHT);

  // manual tape out
  static Button copilotBack = new JoystickButton(copilot, BACK);
  // manual tape in
  static Button copilotStart = new JoystickButton(copilot, START);

  static Button elevatorJoystickActivation = new Button() {
    @Override
    public boolean get() {
      return Utilities.processDeadzone(copilot.getRawAxis(1), Tuning.manualControlDeadzone)
          != 0; // zero values mean it's within the deadzone
    }
  };

  static Button wristJoystickActivation = new Button() {
    @Override
    public boolean get() {
      return Utilities.processDeadzone(copilot.getRawAxis(5), Tuning.manualControlDeadzone)
          != 0; // zero values mean it's within the deadzone
    }
  };

  static Button copilotLeftTrigger = new AxisButton(copilot, Tuning.deadZone, 2);

  static Button copilotRightTriggerSmallPress = new Button() {
    @Override
    public boolean get() {
      return getCopilotRightTrigger() > Tuning.deadZone && getCopilotRightTrigger() < 0.5;
    }
  };
  static Button copilotRightTriggerLargePress = new AxisButton(copilot, 0.5, 3);

  public static double getDriverLeftX() {
    return Utilities.processDeadzone(driver.getRawAxis(0), Tuning.deadZone);
  }

  public static double getCopilotLeftX() {
    return Utilities.processDeadzone(copilot.getRawAxis(0), Tuning.deadZone);
  }

  public static double getDriverRightX() {
    return Utilities.processDeadzone(driver.getRawAxis(4), Tuning.deadZone);
  }

  public static double getCopilotRightX() {
    return Utilities.processDeadzone(copilot.getRawAxis(4), Tuning.deadZone);
  }

  public static double getDriverLeftY() {
    return Utilities.processDeadzone(driver.getRawAxis(1), Tuning.deadZone);
  }

  public static double getCopilotLeftY() {
    return Utilities.processDeadzone(copilot.getRawAxis(1), Tuning.deadZone);
  }

  public static double getDriverRightY() {
    return Utilities.processDeadzone(driver.getRawAxis(5), Tuning.deadZone);
  }

  public static double getCopilotRightY() {
    return Utilities.processDeadzone(copilot.getRawAxis(5), Tuning.deadZone);
  }

  public static double getDriverLeftTrigger() {
    return Utilities.processDeadzone(driver.getRawAxis(2), Tuning.deadZone);
  }

  public static double getDriverRightTrigger() {
    return Utilities.processDeadzone(driver.getRawAxis(3), Tuning.deadZone);
  }

  public static double getCopilotLeftTrigger() {
    return Utilities.processDeadzone(copilot.getRawAxis(2), Tuning.deadZone);
  }

  public static double getCopilotRightTrigger() {
    return Utilities.processDeadzone(copilot.getRawAxis(3), Tuning.deadZone);
  }

  public static double isOutsideRange(double value) {
    return Utilities.constrain(value, 0, 1);
  }
}

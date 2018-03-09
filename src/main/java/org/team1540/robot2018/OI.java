package org.team1540.robot2018;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import org.team1540.base.Utilities;
import org.team1540.robot2018.triggers.StrictDPadButton;
import org.team1540.robot2018.triggers.StrictDPadButton.DPadAxis;

// import org.team1540.base.triggers.StrictDPadButton;

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
  private static double scale(double input, double pow) {
    return Math.copySign(Math.pow(Math.abs(input), pow), input);
  }

  public static Joystick driver = new Joystick(0);
  private static Joystick copilot = new Joystick(1);

  // Buttons
  public static final int A = 1;
  public static final int B = 2;
  public static final int X = 3;
  public static final int Y = 4;

  public static final int LB = 5;
  public static final int RB = 6;
  public static final int BACK = 7;
  public static final int START = 8;

  // Joysticks
  public static final int LEFT_X = 0;
  public static final int LEFT_Y = 1;
  public static final int LEFT_TRIG = 2;
  public static final int RIGHT_TRIG = 3;
  public static final int RIGHT_X = 4;
  public static final int RIGHT_Y = 5;

  // TODO: Remove unused button
  // wrist to forward 45 degrees
  // static Button copilotX = new JoystickButton(copilot, X);

  // INTAKE
  public static double getEjectAxis() {
    return 1 - Utilities.processDeadzone(copilot.getRawAxis(OI.LEFT_TRIG), Tuning.axisDeadzone);
  }

  // ELEVATOR
  public static double getElevatorAxis() {
    return scale(Utilities.processDeadzone(copilot.getRawAxis(LEFT_Y), Tuning.axisDeadzone), 2);
  }

  // TODO: Add a deadzone button to ROOSTER
  // TODO: Add something (function/interface) for disabling a command when joystick is not being used
  static Button enableElevatorAxisControlButton = new Button() {
    // Button is pressed when the specified axis is not within the deadzone
    @Override
    public boolean get() {
      return Utilities.processDeadzone(copilot.getRawAxis(LEFT_Y), Tuning.axisWristLiftDeadzone)
          != 0; // zero values mean it's within the deadzone
    }
  };

  // Move elevator to ground position and run intake until cube is detected
  public static Button autoIntakeButton = new JoystickButton(copilot, LB);
  // Eject the cube regardless of the position of the intake
  static Button autoEjectButton = new JoystickButton(copilot, RB);

  // Move elevator to exchange position
  static Button elevatorExchangeButton = new JoystickButton(copilot, Y);

  // Move elevator to full height and TODO: raise wrist slightly
  static Button elevatorFrontScaleButton = new StrictDPadButton(copilot, 0, DPadAxis.UP);
  // Move elevator to ground position and flip wrist out
  static Button elevatorLowerButton = new StrictDPadButton(copilot, 0, DPadAxis.DOWN);
  // Move elevator to full height
  static Button elevatorRaiseButton = new StrictDPadButton(copilot, 0, DPadAxis.LEFT);
  // Move elevator to switch height
  static Button elevatorSwitchButton = new StrictDPadButton(copilot, 0, DPadAxis.RIGHT);

  static Button wristBackButton = new JoystickButton(copilot, B);
  static Button wristFwdButton = new JoystickButton(copilot, A);
  static Button wrist45DegButton = new JoystickButton(copilot, X);

  static Button holdElevatorWristButton = new JoystickButton(copilot, BACK);

  // WRIST
  public static double getWristAxis() {
    // Note: Same axis as servo tilt, see button that switches between modes
    return scale(Utilities.processDeadzone(copilot.getRawAxis(RIGHT_Y), Tuning.axisDeadzone), 2);
  }

  static Button enableWristAxisControlButton = new Button() {
    // Button is pressed when the specified axis is not within the deadzone
    @Override
    public boolean get() {
      return Utilities.processDeadzone(copilot.getRawAxis(RIGHT_Y), Tuning.axisWristLiftDeadzone)
          != 0; // zero values mean it's within the deadzone
    }
  };

  // INTAKE

  // Stop the intake
  static Button stopIntakeButton = new JoystickButton(copilot, START);

  // DRIVETRAIN
  public static double getTankdriveLeftAxis() {
    return scale(Utilities.processDeadzone(driver.getRawAxis(LEFT_Y), Tuning.axisDeadzone), 2);
  }

  public static double getTankdriveRightAxis() {
    return scale(Utilities.processDeadzone(driver.getRawAxis(RIGHT_Y), Tuning.axisDeadzone), 2);
  }

  public static double getTankdriveBackwardsAxis() {
    return scale(Utilities.processDeadzone(driver.getRawAxis(LEFT_TRIG), Tuning.axisDeadzone), 2);
  }

  public static double getTankdriveForwardsAxis() {
    return scale(Utilities.processDeadzone(driver.getRawAxis(RIGHT_TRIG), Tuning.axisDeadzone), 2);
  }

  // WINCH
  public static double getWinchInAxis() {
    return scale(Utilities.processDeadzone(copilot.getRawAxis(RIGHT_TRIG), Tuning.axisDeadzone), 2);
  }

  // TODO: Add a joystick range funtion to ROOSTER
  // TODO: Rewrite this logic into a single command
  static Button winchInSlowButton = new Button() {
    // Button is pressed when axis is between the deadzone and 0.5
    @Override
    public boolean get() {
      return getWinchInAxis() > Tuning.axisDeadzone && getWinchInAxis() < 0.5;
    }
  };
  static Button winchInFastButton = new Button() {
    // Button is pressed when axis is greater or equal to 0.5
    @Override
    public boolean get() {
      return getWinchInAxis() >= 0.5;
    }
  };

  static Button climbSequenceButton = new Button() {

    @Override
    public boolean get() {
      return copilot.getRawAxis(OI.LEFT_TRIG) > 0.8;
    }
  };

}

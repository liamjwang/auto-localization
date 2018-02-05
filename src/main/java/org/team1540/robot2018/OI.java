package org.team1540.robot2018;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import org.team1540.base.Utilities;
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

  public static Joystick driver = new Joystick(0);
  public static Joystick copilot = new Joystick(1);

  //
  // public static final int autoIntake = 3; //Auto intake (X)
  // public static final int autoEject = 4;
  //
  // public static final int manualEject = 4; //Run intake backwards (no auto control) (Y)
  // public static final int manualIntake = 1; //Run intake backwards (no auto control) (A)
  //
  // public static final int manualElevatorUp = 5; //While held move elevator up (LB)
  // public static final int manualIntakeDown = 6; //While held move elevator down (RB)
  //
  // public static final int manualWinchIn = 1;
  // public static final int manualWinchOut = 2;
  //
  // public static final int manualTapeIn = 0;
  // public static final int manualTapeOut = 0;


  public static final int X = 3;
  public static final int Y = 4;
  public static final int A = 1;
  public static final int B = 2;

  public static final int back = 7;
  public static final int start = 8;
  public static final int LB = 5;
  public static final int RB = 6;

  static Button auto_intake = new JoystickButton(copilot, LB);
  static Button auto_eject = new JoystickButton(copilot, RB);

  static Button manual_eject = new JoystickButton(copilot, X);
  static Button manual_intake = new JoystickButton(copilot, A);
  static Button manual_elevator_up = new JoystickButton(copilot, Y);
  static Button manual_elevator_down = new JoystickButton(copilot, B);
  static Button manual_winch_in = new DPadButton(copilot, 0, DPadAxis.DOWN);
  static Button manual_winch_out = new DPadButton(copilot, 0, DPadAxis.UP);
  static Button manual_tape_in = new JoystickButton(copilot, back);
  static Button manual_tape_out = new JoystickButton(copilot, start);

  public static double getDriverLeftX(){
    return Utilities.processAxisDeadzone(driver.getRawAxis(0), Tuning.deadZone);
  }
  public static double getCopilotLeftX(){
    return Utilities.processAxisDeadzone(copilot.getRawAxis(0), Tuning.deadZone);
  }

  public static double getDriverRightX(){
    return Utilities.processAxisDeadzone(driver.getRawAxis(4), Tuning.deadZone);
  }
  public static double getCopilotRightX(){
    return Utilities.processAxisDeadzone(copilot.getRawAxis(4), Tuning.deadZone);
  }

  public static double getDriverLeftTrigger(){
    return Utilities.processAxisDeadzone(driver.getRawAxis(2), Tuning.deadZone);
  }
  public static double getDriverRightTrigger(){
    return Utilities.processAxisDeadzone(driver.getRawAxis(3), Tuning.deadZone);
  }

  public static double getCopilotLeftTrigger(){
    return Utilities.processAxisDeadzone(copilot.getRawAxis(2), Tuning.deadZone);
  }
  public static double getCopilotRightTrigger(){
    return Utilities.processAxisDeadzone(copilot.getRawAxis(3), Tuning.deadZone);
  }

  public static double getCopilotLeftY(){
    return Utilities.processAxisDeadzone(copilot.getRawAxis(1), Tuning.deadZone);
  }
  public static double getCopilotRightY(){
    return Utilities.processAxisDeadzone(copilot.getRawAxis(5), Tuning.deadZone);
  }
}

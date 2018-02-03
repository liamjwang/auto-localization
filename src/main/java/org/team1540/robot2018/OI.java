package org.team1540.robot2018;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.base.Utilities;

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

  public static final int driverLeftX = 1;  //Left thumbstick up/down
  public static final int driverRightX = 5; //Right thumbstick up/down

  public static final int driverLeftTrigger = 2;  //Left trigger
  public static final int driverRightTrigger = 3; //Right trigger

  public static final int copilotLeftX = 1;  //Left thumbstick up/down
  public static final int copilotRightX = 0; //Right thumbstick up/down

  public static final int copilotLeftY =  0;
  public static final int copilotRightY = 4;

  public static final int autoIntake = 3; //Auto intake (X)
  public static final int autoEject = 4;

  public static final int manualEject = 4; //Run intake backwards (no auto control) (Y)
  public static final int manualIntake = 1; //Run intake backwards (no auto control) (A)

  public static final int manualElevatorUp = 5; //While held move elevator up (LB)
  public static final int manualIntakeDown = 6; //While held move elevator down (RB)

  public static final int manualWinchIn = 1;
  public static final int manualWinchOut = 2;

  public static final int manualTapeIn = 0;
  public static final int manualTapeOut = 0;


  static Button auto_intake = new JoystickButton(copilot, autoIntake);
  static Button manual_eject = new JoystickButton(copilot, manualEject);
  static Button manual_intake = new JoystickButton(copilot, manualIntake);
  static Button manual_elevator_up = new JoystickButton(copilot, manualElevatorUp);
  static Button manual_elevator_down = new JoystickButton(copilot, manualIntakeDown);
  static Button auto_eject = new JoystickButton(copilot, autoEject);
  static Button manual_winch_in = new JoystickButton(copilot, manualWinchIn);
  static Button manual_winch_out = new JoystickButton(copilot, manualWinchOut);
  static Button manual_tape_in = new JoystickButton(copilot, manualTapeIn);
  static Button manual_tape_out = new JoystickButton(copilot, manualTapeOut);


  public static double getDriverLeftX(){
    SmartDashboard.putNumber("Throttle Left", driver.getRawAxis(driverLeftX));
    return Utilities.processAxisDeadzone(driver.getRawAxis(driverLeftX), Tuning.deadZone);
  }
  public static double getCopilotLeftX(){
    return Utilities.processAxisDeadzone(copilot.getRawAxis(driverLeftX), Tuning.deadZone);
  }

  public static double getDriverRightX(){
    SmartDashboard.putNumber("Throttle Right", driver.getRawAxis(driverRightX));
    return Utilities.processAxisDeadzone(driver.getRawAxis(driverRightX), Tuning.deadZone);
  }
  public static double getCopilotRightX(){
    return Utilities.processAxisDeadzone(copilot.getRawAxis(driverRightX), Tuning.deadZone);
  }

  public static double getDriverLeftTrigger(){
    return Utilities.processAxisDeadzone(driver.getRawAxis(driverLeftTrigger), Tuning.deadZone);
  }
  public static double getDriverRightTrigger(){
    return Utilities.processAxisDeadzone(copilot.getRawAxis(driverRightTrigger), Tuning.deadZone);
  }

  public static double getCopilotLeftY(){
    return Utilities.processAxisDeadzone(copilot.getRawAxis(copilotRightY), Tuning.deadZone);
  }
  public static double getCopilotRightY(){
    return Utilities.processAxisDeadzone(copilot.getRawAxis(copilotLeftY), Tuning.deadZone);
  }
}

package org.team1540.robot2018;

import org.team1540.base.Utilities;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

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
 * Left analog trigger: 2 (Only positive)
 * Right analog trigger: 3 (Only positive)
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
  public static final int driverRightX = 0; //Right thumbstick up/down

  public static final int driverLeftTrigger = 2;  //Left trigger
  public static final int driverRightTrigger = 3; //Right trigger

  public static final int copilotLeftX = 1;  //Left thumbstick up/down
  public static final int copilotRightX = 0; //Right thumbstick up/down

  static final double DeadZone = 0.1;

  public static double getDriverLeftX(){
    return Utilities.processAxisDeadzone(driver.getRawAxis(driverLeftX), DeadZone);
  }
  public static double getCopilotLeftX(){
    return Utilities.processAxisDeadzone(copilot.getRawAxis(driverLeftX), DeadZone);
  }

  public static double getDriverRightX(){
    return Utilities.processAxisDeadzone(driver.getRawAxis(driverRightX), DeadZone);
  }
  public static double getCopilotRightX(){
    return Utilities.processAxisDeadzone(copilot.getRawAxis(driverRightX), DeadZone);
  }

  public static double getDriverLeftTrigger(){
    return Utilities.processAxisDeadzone(driver.getRawAxis(driverLeftTrigger), DeadZone);
  }
  public static double getDriverRightTrigger(){
    return Utilities.processAxisDeadzone(copilot.getRawAxis(driverRightTrigger), DeadZone);
  }

}

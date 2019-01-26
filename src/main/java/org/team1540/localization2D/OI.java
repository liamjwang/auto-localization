package org.team1540.localization2D;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.localization2D.commands.drivetrain.UDPAutoLineup;
import org.team1540.rooster.Utilities;
import org.team1540.rooster.util.SimpleCommand;

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

  static JoystickButton autoAlignButton = new JoystickButton(driver, RB);
  static JoystickButton autoAlignCancelButton = new JoystickButton(driver, LB);

  static Command alignCommand = null;

  static {
    OI.autoAlignButton.whenPressed(new SimpleCommand("Start Lineup", () -> {
      double pos_x = SmartDashboard.getNumber("limelight-pose/position/x", 0);
      double pos_y = SmartDashboard.getNumber("limelight-pose/position/y", 0);
      double ori_z = SmartDashboard.getNumber("limelight-pose/orientation/z", 0);
      alignCommand = new UDPAutoLineup();
      // alignCommand = new TestSequence(pos_x, pos_y, ori_z);
      alignCommand.start();
    }));
    OI.autoAlignCancelButton.whenPressed(new SimpleCommand("Cancel Lineup", () -> {
      if (alignCommand != null) {
        alignCommand.cancel();
      }
    }));
  }
}

package org.team1540.robot2018;

import static org.team1540.robot2018.Robot.intake;
import static org.team1540.robot2018.Robot.intakeArms;
import static org.team1540.robot2018.Robot.winch;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import org.team1540.base.Utilities;
import org.team1540.base.util.SimpleCommand;
import org.team1540.robot2018.commands.arms.JoystickArms;
import org.team1540.robot2018.commands.elevator.JoystickElevator;
import org.team1540.robot2018.commands.elevator.MoveElevatorToPosition;
import org.team1540.robot2018.commands.groups.FrontScale;
import org.team1540.robot2018.commands.groups.GroundPosition;
import org.team1540.robot2018.commands.groups.HoldElevatorWrist;
import org.team1540.robot2018.commands.groups.IntakeSequence;
import org.team1540.robot2018.commands.intake.EjectCube;
import org.team1540.robot2018.commands.wrist.CalibrateWrist;
import org.team1540.robot2018.commands.wrist.JoystickWrist;
import org.team1540.robot2018.commands.wrist.MoveWristToPosition;
import org.team1540.robot2018.triggers.StrictDPadButton;
import org.team1540.robot2018.triggers.StrictDPadButton.DPadAxis;

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

  public OI() {
    // INTAKE
    OI.intakeSequenceButton.whenPressed(new IntakeSequence());
    OI.ejectButton.whenPressed(new EjectCube());

    OI.stopIntakeButton.whenPressed(new SimpleCommand("Stop intake", intake::holdCube, intake,
        intakeArms));

    // ARMS
    OI.intakeSequenceButton.whileHeld(new JoystickArms());
    // OI.intakeSequenceButton.whileHeld(new SimpleCommand("Intake Arm Open", () -> intakeArms.set
    //     (Tuning.intakeArmSpeed), intakeArms));

    // ELEVATOR
    OI.enableElevatorAxisControlButton.whileHeld(new JoystickElevator());

    OI.elevatorExchangeButton.whenPressed(new MoveElevatorToPosition(Tuning
        .elevatorExchangePosition));
    OI.elevatorSwitchButton.whenPressed(new MoveElevatorToPosition(Tuning
        .elevatorFrontSwitchPosition));

    // WRIST
    OI.enableWristAxisControlButton.whileHeld(new JoystickWrist());

    OI.wristFwdButton.whenPressed(new CalibrateWrist());
    OI.wrist45DegButton.whenPressed(new MoveWristToPosition(Tuning.wrist45FwdPosition));
    OI.wristBackButton.whenPressed(new MoveWristToPosition(Tuning.wristBackPosition));

    // ELEVATOR AND WRIST
    OI.elevatorLowerButton.whenPressed(new GroundPosition());
    OI.elevatorFrontScaleButton.whenPressed(new FrontScale());

    OI.holdElevatorWristButton.whenPressed(new HoldElevatorWrist());

    // WINCH
    OI.winchInSlowButton.whileHeld(new SimpleCommand("Winch In Low", () -> winch.set(Tuning
        .winchInLowVel), winch));

    OI.winchInFastButton.whileHeld(new SimpleCommand("Winch In High", () -> winch.set(Tuning
        .winchInHighVel), winch));
  }

  // INTAKE
  public static double getEjectAxis() {
    return 1 - Utilities.processDeadzone(copilot.getRawAxis(OI.LEFT_TRIG), Tuning.axisDeadzone);
  }

  // ELEVATOR
  public static double getElevatorAxis() {
    return scale(Utilities.processDeadzone(copilot.getRawAxis(LEFT_Y), Tuning.axisDeadzone), 2);
  }

  // TODO: Add a deadzone button to ROOSTER
  static Button enableElevatorAxisControlButton = new Button() {
    // Button is pressed when the specified axis is not within the deadzone
    @Override
    public boolean get() {
      return Utilities.processDeadzone(copilot.getRawAxis(LEFT_Y), Tuning.axisWristLiftDeadzone)
          != 0; // zero values mean it's within the deadzone
    }
  };

  public static Button intakeSequenceButton = new JoystickButton(copilot, LB);
  static Button ejectButton = new JoystickButton(copilot, RB);

  static Button elevatorExchangeButton = new JoystickButton(copilot, Y);

  static Button elevatorFrontScaleButton = new StrictDPadButton(copilot, 0, DPadAxis.UP);
  static Button elevatorLowerButton = new StrictDPadButton(copilot, 0, DPadAxis.DOWN);
  static Button elevatorSwitchButton = new StrictDPadButton(copilot, 0, DPadAxis.RIGHT);

  static Button wristBackButton = new JoystickButton(copilot, B);
  static Button wristFwdButton = new JoystickButton(copilot, A);
  static Button wrist45DegButton = new JoystickButton(copilot, X);

  static Button holdElevatorWristButton = new JoystickButton(copilot, BACK);

  // WRIST
  public static double getWristAxis() {
    return scale(Utilities.processDeadzone(copilot.getRawAxis(RIGHT_Y), Tuning.axisDeadzone), 2);
  }

  public static double getArmLeftAxis() {
    return scale(Utilities.processDeadzone(copilot.getRawAxis(LEFT_X), Tuning.axisDeadzone), 2);
  }

  public static double getArmRightAxis() {
    return scale(Utilities.processDeadzone(copilot.getRawAxis(RIGHT_X), Tuning.axisDeadzone), 2);
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

  // TODO: Add a joystick range button to ROOSTER
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

}

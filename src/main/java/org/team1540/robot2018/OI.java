package org.team1540.robot2018;

import static org.team1540.robot2018.Robot.arms;
import static org.team1540.robot2018.Robot.intake;
import static org.team1540.robot2018.Robot.winch;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import org.team1540.base.Utilities;
import org.team1540.base.triggers.SimpleButton;
import org.team1540.base.util.SimpleCommand;
import org.team1540.robot2018.commands.arms.DropCube;
import org.team1540.robot2018.commands.arms.JoystickArms;
import org.team1540.robot2018.commands.elevator.JoystickElevator;
import org.team1540.robot2018.commands.elevator.MoveElevatorSafe;
import org.team1540.robot2018.commands.groups.FrontScale;
import org.team1540.robot2018.commands.groups.GroundPosition;
import org.team1540.robot2018.commands.groups.HoldElevatorWrist;
import org.team1540.robot2018.commands.groups.IntakeSequence;
import org.team1540.robot2018.commands.intake.JoystickEject;
import org.team1540.robot2018.commands.wrist.CalibrateWristMotionMagic;
import org.team1540.robot2018.commands.wrist.JoystickWrist;
import org.team1540.robot2018.commands.wrist.MoveWrist;
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

  // INTAKE
  public static double getEjectAxis() {
    return 1 - Utilities.processDeadzone(copilot.getRawAxis(OI.LEFT_TRIG), Tuning.axisDeadzone);
  }

  // ELEVATOR
  public static double getElevatorAxis() {
    return scale(Utilities.processDeadzone(copilot.getRawAxis(LEFT_Y), Tuning.axisDeadzone), 2);
  }

  // TODO: Add a deadzone button to ROOSTER
  // Button is pressed when the specified axis is not within the deadzone
  // zero values mean it's within the deadzone
  static Button enableElevatorAxisControlButton = new SimpleButton(() ->
      Utilities.processDeadzone(copilot.getRawAxis(LEFT_Y), Tuning.axisWristLiftDeadzone) != 0);

  public static Button intakeSequenceButton = new JoystickButton(copilot, LB);
  static Button ejectButton = new JoystickButton(copilot, RB);

  static Button elevatorExchangeButton = new JoystickButton(copilot, Y);

  static Button elevatorFrontScaleButton = new StrictDPadButton(copilot, 0, DPadAxis.UP);
  static Button elevatorLowerButton = new StrictDPadButton(copilot, 0, DPadAxis.DOWN);
  static Button elevatorSwitchButton = new StrictDPadButton(copilot, 0, DPadAxis.RIGHT);

  static Button wristBackButton = new JoystickButton(copilot, B);
  static Button wristFwdButton = new JoystickButton(copilot, A);
  static Button wristTransitButton = new JoystickButton(copilot, X);

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

  // zero values mean it's within the deadzone
  static Button enableWristAxisControlButton = new SimpleButton(() ->
      Utilities.processDeadzone(copilot.getRawAxis(RIGHT_Y), Tuning.axisWristLiftDeadzone) != 0);

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
  static Button winchInSlowButton = new SimpleButton(() -> getWinchInAxis() > Tuning.axisDeadzone
      && getWinchInAxis() < 0.5);
  static Button winchInFastButton = new SimpleButton(() -> getWinchInAxis() >= 0.5);

  // TAPE
  public static double getTapeAxis() {
    // TODO: Negate tape motor instead of negating it here
    return scale(Utilities.processDeadzone(-copilot.getRawAxis(LEFT_X), Tuning.axisDeadzone), 2);
  }

  public static boolean getTapeEnabled() {
    return copilot.getRawAxis(LEFT_TRIG) > Tuning.triggerFullPressThreshold;
  }

  static {
    // INTAKE
    OI.intakeSequenceButton.whenPressed(new IntakeSequence());

    // OI.ejectButton.whenPressed(new JoystickEject());

    OI.ejectButton.whenPressed(new ConditionalCommand(new DropCube(Tuning.armDropTime), new JoystickEject()) {
      @Override
      protected boolean condition() {
        return OI.getTapeEnabled();
      }
    });

    OI.stopIntakeButton.whenPressed(new SimpleCommand("Stop intake", intake::holdCube, intake,
        arms));

    // ARMS
    OI.intakeSequenceButton.whileHeld(new JoystickArms());
    // OI.intakeSequenceButton.whileHeld(new SimpleCommand("Intake Arm Open", () -> arms.set
    //     (Tuning.intakeArmSpeed), arms));

    // ELEVATOR
    OI.enableElevatorAxisControlButton.whileHeld(new JoystickElevator());

    OI.elevatorExchangeButton.whenPressed(new MoveElevatorSafe(true, Tuning
        .elevatorExchangePosition));
    OI.elevatorSwitchButton.whenPressed(new MoveElevatorSafe(true, Tuning
        .elevatorFrontSwitchPosition));

    // WRIST
    OI.enableWristAxisControlButton.whileHeld(new JoystickWrist());

    OI.wristFwdButton.whenPressed(new CalibrateWristMotionMagic());
    OI.wristTransitButton.whenPressed(new MoveWrist(Tuning.wristTransitPosition));
    OI.wristBackButton.whenPressed(new MoveWrist(Tuning.wristBackPosition));

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
}

package org.team1540.robot2018;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.base.Utilities;
import org.team1540.base.adjustables.AdjustableManager;
import org.team1540.base.power.PowerManager;
import org.team1540.base.util.SimpleCommand;
import org.team1540.robot2018.commands.climber.AlignClimber;
import org.team1540.robot2018.commands.auto.AutonomousProfiling;
import org.team1540.robot2018.commands.elevator.JoystickElevator;
import org.team1540.robot2018.commands.elevator.MoveElevatorToPosition;
import org.team1540.robot2018.commands.groups.FrontScale;
import org.team1540.robot2018.commands.groups.GroundPosition;
import org.team1540.robot2018.commands.groups.IntakeSequence;
import org.team1540.robot2018.commands.intake.EjectCube;
import org.team1540.robot2018.commands.wrist.JoystickWrist;
import org.team1540.robot2018.subsystems.ClimberTapeMeasure;
import org.team1540.robot2018.subsystems.ClimberTurret;
import org.team1540.robot2018.subsystems.ClimberWinch;
import org.team1540.robot2018.subsystems.DriveTrain;
import org.team1540.robot2018.subsystems.Elevator;
import org.team1540.robot2018.subsystems.Intake;
import org.team1540.robot2018.subsystems.Wrist;

public class Robot extends IterativeRobot {
  public static final DriveTrain drivetrain = new DriveTrain();
  public static final Intake intake = new Intake();
  public static final Elevator elevator = new Elevator();
  public static final Wrist wrist = new Wrist();
  public static final ClimberTurret turret = new ClimberTurret();
  public static final ClimberTapeMeasure tape = new ClimberTapeMeasure();
  public static final ClimberWinch winch = new ClimberWinch();

  private Command autoCommand = new AutonomousProfiling();

  @Override
  public void robotInit() {
    AdjustableManager.getInstance().add(new Tuning());
    AdjustableManager.getInstance().add(drivetrain);
    AdjustableManager.getInstance().add(autoCommand);
    PowerManager.getInstance().setRunning(false);

    // configure controls

    Command intakeCommand = new IntakeSequence();
    OI.copilotLB.whenPressed(intakeCommand);
    OI.copilotRB.whenPressed(new EjectCube());
    OI.copilotStart.whenPressed(new SimpleCommand("Stop intake", intake::stop, intake));

    OI.copilotA.whenPressed(new MoveElevatorToPosition(Tuning.elevatorExchangePosition));

    OI.copilotB.whileHeld(new SimpleCommand("Tape out", () -> tape.set(Tuning.tapeOutSpeed), tape));
    OI.copilotY.whileHeld(new SimpleCommand("Tape in", () -> tape.set(Tuning.tapeInLowSpeed), tape));

    OI.copilotDPadRight.whenPressed(new MoveElevatorToPosition(Tuning.elevatorFrontSwitchPosition));
    OI.copilotDPadLeft.whenPressed(new MoveElevatorToPosition(Tuning.elevatorScalePosition));
    OI.copilotDPadUp.whenPressed(new FrontScale());
    OI.copilotDPadDown.whenPressed(new GroundPosition());

    OI.elevatorJoystickActivation.whileHeld(new JoystickElevator());

    /*
    The left trigger changes the mode of the right joystick. Normally (the left trigger is not
    pressed) the JoystickWrist command is run and the right stick controls the wrist. When the left
    trigger is pressed, the JoystickWrist command does not run, while the AlignClimber command does;
    therefore, the right stick controls the climber turret.

    The wristJoystickActivation activates when the joystick is outside of its deadzone, and only
    runs the joystick control commands when the joystick is in fact being moved. This allows other
    commands that require the wrist (or turret) to run when the joystick is not being moved.
    */
    OI.wristJoystickActivation.whileHeld(new ConditionalCommand(new AlignClimber(), new JoystickWrist()) {
      @Override
      protected boolean condition() {
        return OI.copilotLeftTrigger.get();
      }
    });

    OI.copilotRightTriggerSmallPress.whileHeld(new SimpleCommand("Winch In Low", () -> {
      tape.set(Tuning.tapeInLowSpeed);
      winch.set(Tuning.winchInLowSpeed);
    }, tape, winch));

    OI.copilotRightTriggerLargePress.whileHeld(new SimpleCommand("Winch In High", () -> {
      tape.set(Tuning.tapeInHighSpeed);
      winch.set(Tuning.winchInHighSpeed);
    }, tape, winch));

    // configure SmartDashboard
    Command zeroWrist = new SimpleCommand("Zero Wrist", wrist::resetEncoder);
    zeroWrist.setRunWhenDisabled(true);
    SmartDashboard.putData(zeroWrist);

    Command zeroElevator = new SimpleCommand("Zero Elevator", elevator::resetEncoder);
    zeroElevator.setRunWhenDisabled(true);
    SmartDashboard.putData(zeroElevator);
  }

  @Override
  public void disabledInit() {
    // turret.disableServos();
  }

  @Override
  public void autonomousInit() {
    Scheduler.getInstance().add(autoCommand);
  }

  @Override
  public void teleopInit() {
    turret.enableServos();
    turret.init();
  }

  @Override
  public void testInit() {
  }

  @Override
  public void robotPeriodic() {
    Scheduler.getInstance().run();
    SmartDashboard.putNumber("lVelocity", Robot.drivetrain.getLeftVelocity());
    SmartDashboard.putNumber("rVelocity", Robot.drivetrain.getRightVelocity());
  }

  @Override
  public void disabledPeriodic() {
  }

  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopPeriodic() {
    Robot.drivetrain.prepareForMotionProfiling();
    Robot.drivetrain.setLeftVelocity(Utilities.processDeadzone((OI.getDriverLeftY() + OI
        .getDriverLeftTrigger() - OI.getDriverRightTrigger()), 0.1) * 1000);
    Robot.drivetrain.setRightVelocity(Utilities.processDeadzone((OI.getDriverRightY() + OI
        .getDriverLeftTrigger() - OI.getDriverRightTrigger()), 0.1) * 1000);
  }
}

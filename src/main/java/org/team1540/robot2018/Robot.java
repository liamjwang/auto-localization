package org.team1540.robot2018;

import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.base.adjustables.AdjustableManager;
import org.team1540.base.util.SimpleCommand;
import org.team1540.robot2018.commands.climber.AlignClimber;
import org.team1540.robot2018.commands.climber.WinchOut;
import org.team1540.robot2018.commands.elevator.JoystickElevator;
import org.team1540.robot2018.commands.elevator.MoveElevatorToPosition;
import org.team1540.robot2018.commands.groups.FrontScale;
import org.team1540.robot2018.commands.groups.GroundPosition;
import org.team1540.robot2018.commands.groups.IntakeSequence;
import org.team1540.robot2018.commands.intake.AutoEject;
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

  @Override
  public void robotInit() {
    AdjustableManager.getInstance().add(new Tuning());

    //    OI.copilotLB.whenPressed(new AutoIntake());
    //    OI.copilotRB.whenPressed(new AutoEject());

    // OI.copilotX.whileHeld(new SimpleCommand("Eject",
    //     () -> Robot.intake.set(Tuning.ejectSpeedA, Tuning.ejectSpeedB),
    //     intake));
    // OI.copilotA.whileHeld(new SimpleCommand("Intake",
    //     () -> Robot.intake.set(Tuning.IntakeSpeedA, Tuning.IntakeSpeedB),
    //     intake));

    // OI.manualElevatorUp.whileHeld(new ManualElevatorUp());
    // OI.copilotB.whileHeld(new ManualElevatorDown());
    //
    IntakeSequence intakeSequence = new IntakeSequence();
    OI.copilotLB.whenPressed(intakeSequence);
    OI.copilotRB.whenPressed(new AutoEject());
    OI.copilotStart.cancelWhenPressed(intakeSequence);
    OI.copilotBack.whileHeld(new WinchOut());

       // OI.copilotBack.whileHeld(new TapeIn());
       // OI.copilotStart.whileHeld(new TapeOut());
    //
    //     OI.copilotBack.whileHeld(new SimpleCommand("Tape in", () -> tape.set(Tuning.tapeInSpeed), tape));
    //     OI.copilotStart.whileHeld(new SimpleCommand("Tape out", () -> tape.set(Tuning.tapeOutSpeed), tape));

    // OI.copilotA.whenPressed(new MoveWristToPosition(Tuning.wristOutPosition));
    // OI.copilotB.whenPressed(new MoveWristToPosition(Tuning.wristBackPosition));
    // OI.copilotX.whenPressed(new MoveWristToPosition(Tuning.wrist45FwdPosition));
    // OI.copilotY.whenPressed(new GroundPosition());

    OI.copilotA.whenPressed(new MoveElevatorToPosition(Tuning.elevatorExchangePosition));

    OI.copilotB.whileHeld(new SimpleCommand("Tape out", () -> tape.set(Tuning.tapeOutSpeed), tape));
    OI.copilotY.whileHeld(new SimpleCommand("Tape in", () -> tape.set(Tuning.tapeInSpeed), tape));

    OI.copilotDPadRight.whenPressed(new MoveElevatorToPosition(Tuning.elevatorFrontSwitchPosition));
    OI.copilotDPadLeft.whenPressed(new MoveElevatorToPosition(Tuning.elevatorScalePosition));
    OI.copilotDPadUp.whenPressed(new FrontScale());
    OI.copilotDPadDown.whenPressed(new GroundPosition());

    OI.elevatorJoystickActivation.whileHeld(new JoystickElevator());
    // OI.elevatorJoystickActivation.whenReleased(new SimpleCommand("Stop Elevator", elevator::stop, elevator));

    OI.wristJoystickActivation.whileHeld(new ConditionalCommand(new AlignClimber(), new JoystickWrist()) {
      @Override
      protected boolean condition() {
        return OI.copilotLeftTrigger.get();
      }
    });

    // OI.copilotLeftTriggerSmallPress.whenPressed(turretControl);
    // OI.copilotLeftTriggerLargePress.cancelWhenPressed(turretControl);
    // OI.copilotLeftTriggerLargePress.whileHeld(new RunClimber(Tuning.climberOutSpeed));

    OI.copilotRightTriggerSmallPress.whileHeld(new SimpleCommand("Winch In Low", () -> {
      // tape.set(Tuning.climberInLowSpeed * Tuning.tapeMeasureMultiplier);
      winch.set(Tuning.climberInLowSpeed * Tuning.winchMultiplier);
    }, tape, winch));

    OI.copilotRightTriggerLargePress.whileHeld(new SimpleCommand("Winch In High", () -> {
      // tape.set(Tuning.climberInHighSpeed * Tuning.tapeMeasureMultiplier);
      winch.set(Tuning.climberInHighSpeed * Tuning.winchMultiplier);
    }, tape, winch));

    UsbCamera camera = CameraServer.getInstance().startAutomaticCapture("Camera", 0);
    camera.setResolution(640, 480);
    MjpegServer mjpegServer = new MjpegServer("Camera Server", 1181);
    mjpegServer.setSource(camera);
  }

  @Override
  public void disabledInit() {
    turret.disableServos();
  }

  @Override
  public void autonomousInit() {
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
    SmartDashboard.putData("Scheduler", Scheduler.getInstance());
    Scheduler.getInstance().run();
    SmartDashboard.putNumber("Wrist Encoder", wrist.getPosition());
    SmartDashboard.putNumber("Elevator", elevator.getPosition());
//    SmartDashboard.putData(new PowerDistributionPanel());

    SmartDashboard.putNumber("DT Left", drivetrain.getLeftVelocity());
    SmartDashboard.putNumber("Drive Right", drivetrain.getRightVelocity());
  }

  @Override
  public void disabledPeriodic() {
  }

  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopPeriodic() {
  }
}

package org.team1540.robot2018;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import org.team1540.base.adjustables.AdjustableManager;
import org.team1540.robot2018.commands.TurretControl;
import org.team1540.robot2018.commands.climber.RunClimber;
import org.team1540.robot2018.commands.elevator.JoystickElevator;
import org.team1540.robot2018.commands.elevator.MoveElevatorToPosition;
import org.team1540.robot2018.commands.groups.FrontScale;
import org.team1540.robot2018.commands.groups.GroundPosition;
import org.team1540.robot2018.commands.groups.IntakeSequence;
import org.team1540.robot2018.commands.intake.AutoEject;
import org.team1540.robot2018.commands.wrist.MoveWristToPosition;
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
    //     () -> Robot.intake.set(Tuning.EjectSpeedA, Tuning.EjectSpeedB),
    //     intake));
    // OI.copilotA.whileHeld(new SimpleCommand("Intake",
    //     () -> Robot.intake.set(Tuning.IntakeSpeedA, Tuning.IntakeSpeedB),
    //     intake));

    // OI.manualElevatorUp.whileHeld(new ManualElevatorUp());
    // OI.copilotB.whileHeld(new ManualElevatorDown());

    //    OI.manualWinchIn.whileHeld(new WinchIn());
    //    OI.manualWinchOut.whileHeld(new WinchOut());

    //    OI.copilotBack.whileHeld(new TapeIn());
    //    OI.copilotStart.whileHeld(new TapeOut());
    //
    //     OI.copilotBack.whileHeld(new SimpleCommand("Tape in", () -> tape.set(Tuning.tapeInSpeed), tape));
    //     OI.copilotStart.whileHeld(new SimpleCommand("Tape out", () -> tape.set(Tuning.tapeOutSpeed), tape));

    OI.copilotA.whenPressed(new MoveWristToPosition(Tuning.wristOutPosition));
    OI.copilotB.whenPressed(new MoveWristToPosition(Tuning.wristBackPosition));
    OI.copilotX.whenPressed(new MoveWristToPosition(Tuning.wrist45FwdPosition));
    OI.copilotY.whenPressed(new GroundPosition());

    OI.copilotLB.whenPressed(new IntakeSequence());
    OI.copilotRB.whenPressed(new AutoEject());

    OI.copilotDPadRight.whenPressed(new MoveElevatorToPosition(Tuning.elevatorFrontSwitchPosition));
    OI.copilotDPadLeft.whenPressed(new MoveElevatorToPosition(Tuning.elevatorLowScalePosition));
    OI.copilotDPadUp.whenPressed(new FrontScale());
    OI.copilotDPadDown.whenPressed(new MoveElevatorToPosition(0));

    OI.elevatorJoystickActivation.whileHeld(new JoystickElevator());

    Command turretControl = new TurretControl();
    OI.copilotLeftTriggerSmallPress.whenPressed(turretControl);
    OI.copilotLeftTriggerLargePress.cancelWhenPressed(turretControl);
    OI.copilotLeftTriggerLargePress.whileHeld(new RunClimber(Tuning.climberOutSpeed));
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
  }

  @Override
  public void testInit() {
  }

  @Override
  public void robotPeriodic() {
    Scheduler.getInstance().run();
//    SmartDashboard.putData(new PowerDistributionPanel());
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

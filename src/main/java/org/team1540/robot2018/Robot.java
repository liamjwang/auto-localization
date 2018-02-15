package org.team1540.robot2018;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import org.team1540.base.adjustables.AdjustableManager;
import org.team1540.base.util.SimpleCommand;
import org.team1540.robot2018.commands.elevator.MoveElevatorToPosition;
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

//    OI.auto_intake.whenPressed(new AutoIntake());
//    OI.auto_eject.whenPressed(new AutoEject());

    OI.manual_eject.whileHeld(new SimpleCommand("Eject",
        () -> Robot.intake.set(Tuning.EjectSpeedA, Tuning.EjectSpeedB),
        intake));
    OI.manual_intake.whileHeld(new SimpleCommand("Eject",
        () -> Robot.intake.set(Tuning.IntakeSpeedA, Tuning.IntakeSpeedB),
        intake));

    // OI.manual_elevator_up.whileHeld(new ManualElevatorUp());
    // OI.manual_elevator_down.whileHeld(new ManualElevatorDown());

//    OI.manual_winch_in.whileHeld(new WinchIn());
//    OI.manual_winch_out.whileHeld(new WinchOut());

//    OI.manual_tape_in.whileHeld(new TapeIn());
//    OI.manual_tape_out.whileHeld(new TapeOut());

    OI.manual_tape_in.whileHeld(new SimpleCommand("Tape in", () -> tape.set(Tuning.tapeInSpeed), tape));
    OI.manual_tape_out.whileHeld(new SimpleCommand("Tape out", () -> tape.set(Tuning.tapeOutSpeed), tape));

    OI.manual_winch_in.whileHeld(new SimpleCommand("Winch in", () -> winch.set(Tuning.winchInSpeed), winch));
    OI.manual_winch_out.whileHeld(new SimpleCommand("Winch out", () -> winch.set(Tuning.winchOutSpeed), winch));

    OI.toSwitchHeight.whenPressed(new MoveElevatorToPosition(Tuning.elevatorFrontSwitchPosition));
    OI.toScaleHeight.whenPressed(new MoveElevatorToPosition(Tuning.elevatorScalePosition));
    OI.toLowerScaleHeight.whenPressed(new MoveElevatorToPosition(0));
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

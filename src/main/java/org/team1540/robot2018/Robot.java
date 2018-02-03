package org.team1540.robot2018;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.base.adjustables.AdjustableManager;

import org.team1540.robot2018.commands.AutoEject;
import org.team1540.robot2018.subsystems.Climber;
import org.team1540.robot2018.subsystems.DriveTrain;
import org.team1540.robot2018.subsystems.Intake;
import org.team1540.robot2018.subsystems.Elevator;
import org.team1540.robot2018.subsystems.Wrist;

import org.team1540.robot2018.commands.AutoIntake;
import org.team1540.robot2018.commands.ManualEject;
import org.team1540.robot2018.commands.ManualIntake;
import org.team1540.robot2018.commands.ManualElevatorUp;
import org.team1540.robot2018.commands.ManualElevatorDown;

public class Robot extends IterativeRobot {
  public static final DriveTrain drivetrain = new DriveTrain();
  public static final Intake intake = new Intake();
  public static final Elevator elevator = new Elevator();
  public static final Climber climber = new Climber();
  public static final Wrist wrist = new Wrist();

  @Override
  public void robotInit() {
    AdjustableManager.getInstance().add(new Tuning());

    OI.auto_intake.whenPressed(new AutoIntake());
    OI.auto_eject.whenPressed(new AutoEject());
    OI.manual_eject.whileHeld(new ManualEject());
    OI.manual_intake.whileHeld(new ManualIntake());
    OI.manual_elevator_up.whileHeld(new ManualElevatorUp());
    OI.manual_elevator_down.whileHeld(new ManualElevatorDown());
  }

  @Override
  public void disabledInit() {
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
    AdjustableManager.getInstance().update();
    SmartDashboard.putData(new PowerDistributionPanel());
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

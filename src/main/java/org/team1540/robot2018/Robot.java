package org.team1540.robot2018;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import org.team1540.base.adjustables.AdjustableManager;

import org.team1540.robot2018.subsystems.DriveTrain;
import org.team1540.robot2018.subsystems.Intake;

import org.team1540.robot2018.commands.AutoIntake;

public class Robot extends IterativeRobot {
  public static final DriveTrain drivetrain = new DriveTrain();
  public static final Intake intake = new Intake();

  @Override
  public void robotInit() {
    AdjustableManager.getInstance().add(new Tuning());
    OI.auto_intake.whileHeld(new AutoIntake());
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
  }

  @Override
  public void disabledPeriodic() {
  }

  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopPeriodic() {
    drivetrain.drive();
  }
}

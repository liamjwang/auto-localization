package org.team1540.robot2018;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import org.team1540.base.adjustables.AdjustableManager;

import org.team1540.robot2018.subsystems.DriveTrain;

public class Robot extends IterativeRobot {
  public static final DriveTrain drivetrain = new DriveTrain();

  @Override
  public void robotInit() {
    AdjustableManager.getInstance().add(new Tuning());
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

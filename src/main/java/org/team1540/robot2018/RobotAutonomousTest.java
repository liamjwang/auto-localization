package org.team1540.robot2018;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.base.adjustables.AdjustableManager;
import org.team1540.base.power.PowerManager;
import org.team1540.robot2018.commands.drivetrain.AutonomousProfiling;
import org.team1540.robot2018.subsystems.DriveTrain;

public class RobotAutonomousTest extends IterativeRobot {
  public static final DriveTrain drivetrain = new DriveTrain();

  private Command autoCommand = new AutonomousProfiling();

  @Override
  public void robotInit() {
    AdjustableManager.getInstance().add(new Tuning());
    PowerManager.getInstance().setRunning(false);
  }

  @Override
  public void disabledInit() {
  }

  @Override
  public void autonomousInit() {
    Scheduler.getInstance().add(autoCommand);
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
    SmartDashboard.putNumber("lVelocity", RobotAutonomousTest.drivetrain.getLeftVelocity());
    SmartDashboard.putNumber("rVelocity", RobotAutonomousTest.drivetrain.getRightVelocity());
  }

  @Override
  public void disabledPeriodic() {
  }

  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopPeriodic() {
    RobotAutonomousTest.drivetrain.prepareForMotionProfiling();
    RobotAutonomousTest.drivetrain.setLeftVelocity(RobotUtil.deadzone((OI.getDriverLeftY() + OI
        .getDriverLeftTrigger() - OI.getDriverRightTrigger()) *
        1000));
    RobotAutonomousTest.drivetrain.setRightVelocity(RobotUtil.deadzone((OI.getDriverRightY() + OI
        .getDriverLeftTrigger() - OI.getDriverRightTrigger()) * 1000));
  }
}

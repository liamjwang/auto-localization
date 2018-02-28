package org.team1540.robot2018;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.base.Utilities;
import org.team1540.base.adjustables.AdjustableManager;
import org.team1540.base.power.PowerManager;
import org.team1540.base.util.SimpleCommand;
import org.team1540.robot2018.commands.auto.SwitchAuto;
import org.team1540.robot2018.subsystems.DriveTrain;

public class RobotAutonomousTest extends IterativeRobot {
  public static final DriveTrain drivetrain = new DriveTrain();

  private Command autoCommand = new SwitchAuto();

  @Override
  public void robotInit() {
    AdjustableManager.getInstance().add(new Tuning());
    PowerManager.getInstance().setRunning(false);

    Command zeroElevator = new SimpleCommand("Zero Elevator", Robot.elevator::resetEncoder);
    zeroElevator.setRunWhenDisabled(true);
    SmartDashboard.putData(zeroElevator);
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
    autoCommand.cancel();
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
    Robot.drivetrain.prepareForMotionProfiling();
    Robot.drivetrain.setLeftVelocity(Utilities.processDeadzone((OI.getDriverLeftY() + OI
        .getDriverLeftTrigger() - OI.getDriverRightTrigger()), 0.1) * 1000);
    Robot.drivetrain.setRightVelocity(Utilities.processDeadzone((OI.getDriverRightY() + OI
        .getDriverLeftTrigger() - OI.getDriverRightTrigger()), 0.1) * 1000);
  }
}

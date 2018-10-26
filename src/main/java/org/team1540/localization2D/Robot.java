package org.team1540.localization2D;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.base.power.PowerManager;
import org.team1540.localization2D.subsystems.DriveTrain;

public class Robot extends IterativeRobot {
  public static final DriveTrain drivetrain = new DriveTrain();
  public static AHRS navx = new AHRS(Port.kMXP);

  private SendableChooser<String> autoPosition;
  private SendableChooser<Boolean> driveMode;

  private Command autoCommand;


  @Override
  public void robotInit() {
    // disable unused things
    LiveWindow.disableAllTelemetry();
    PowerManager.getInstance().interrupt();

    SmartDashboard.putData(drivetrain);
    SmartDashboard.putData(PowerManager.getInstance());
  }

  @Override
  public void disabledInit() {
    Robot.drivetrain.reset();
    Robot.drivetrain.configTalonsForVelocity();
    Robot.drivetrain.setBrake(false);
  }

  @Override
  public void autonomousInit() {
    Robot.drivetrain.reset();
  }

  @Override
  public void teleopInit() {
    Robot.drivetrain.reset();
    Robot.drivetrain.enableCurrentLimiting();
    Robot.drivetrain.configTalonsForVelocity();
  }

  @Override
  public void testInit() {
  }

  @Override
  public void robotPeriodic() {
    double leftDistance = drivetrain.getLeftPosition();
    double rightDistance = -drivetrain.getRightPosition();
    double gyroAngle = Robot.navx.getAngle();

    SmartDashboard.putNumber("Left Distance", leftDistance);
    SmartDashboard.putNumber("Right Distance", rightDistance);

    SmartDashboard.putNumber("Gyro Angle", gyroAngle);
    SmartDashboard.putData(Scheduler.getInstance());


    Scheduler.getInstance().run();
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

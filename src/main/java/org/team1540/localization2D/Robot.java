package org.team1540.localization2D;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.io.IOException;
import org.team1540.base.power.PowerManager;
import org.team1540.localization2D.commands.drivetrain.UDPVelocityTwistDrive;
import org.team1540.localization2D.commands.drivetrain.VelocityDrive;
import org.team1540.localization2D.subsystems.DriveTrain;

public class Robot extends IterativeRobot {
  public static final DriveTrain drivetrain = new DriveTrain();
  public static AHRS navx = new AHRS(Port.kMXP);
  public static UDPServer serv;

  static {
    try {
      serv = new UDPServer();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

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
    Robot.drivetrain.zeroEncoders();
    Robot.drivetrain.setBrake(false);
    localizationInit();
  }

  @Override
  public void autonomousInit() {
    Robot.drivetrain.reset();
    Robot.drivetrain.zeroEncoders();
    Robot.drivetrain.configTalonsForVelocity();
    new UDPVelocityTwistDrive().start();
    localizationInit();
  }

  @Override
  public void teleopInit() {
    Robot.drivetrain.reset();
    Robot.drivetrain.enableCurrentLimiting();
    Robot.drivetrain.configTalonsForVelocity();
    new VelocityDrive().start();
    localizationInit();
  }

  @Override
  public void testInit() {
    SmartDashboard.putNumber("goal-position-x", 10);
    SmartDashboard.putNumber("goal-position-y", 0);
    SmartDashboard.putNumber("goal-orientation-z", 0);
  }

  @Override
  public void robotPeriodic() {
    SmartDashboard.putData(Scheduler.getInstance());
    Scheduler.getInstance().run();
    localizationPeriodic();
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

  @Override
  public void testPeriodic() {
  }

  private LocalizationAccum2D accum2D = new LocalizationAccum2D();

  private void localizationInit() {
    Robot.navx.zeroYaw();
    accum2D.reset();
    SmartDashboard.putBoolean("timertest", false);
  }

  private void localizationPeriodic() {
    double leftDistance = drivetrain.getLeftPosition()/Tuning.drivetrainTicksPerMeter;
    double rightDistance = drivetrain.getRightPosition()/Tuning.drivetrainTicksPerMeter;
    double gyroAngle = Robot.navx.getAngle();

    accum2D.update(leftDistance, rightDistance, Math.toRadians(gyroAngle));

    SmartDashboard.putNumber("pose-position-x", accum2D.getXpos());
    SmartDashboard.putNumber("pose-position-y", accum2D.getYpos());
    SmartDashboard.putNumber("pose-orientation-z", gyroAngle);

    double leftVelocity = drivetrain.getLeftVelocity()*10/Tuning.drivetrainTicksPerMeter;
    double rightVelocity = drivetrain.getRightVelocity()*10/Tuning.drivetrainTicksPerMeter;

    double xvel = (leftVelocity + rightVelocity) / 2;
    double thetavel = (leftVelocity-rightVelocity)/(Tuning.drivetrainRadius)/2;

    SmartDashboard.putNumber("twist-linear-x", xvel);
    SmartDashboard.putNumber("twist-angular-z", thetavel);

    if (serv != null) {
        try {
            serv.sendPoseAndTwist(
                    accum2D.getXpos(),
                    accum2D.getYpos(),
                    gyroAngle,
                    xvel,
                    thetavel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // SmartDashboard.putNumber("Left Distance", leftDistance);
    // SmartDashboard.putNumber("Right Distance", rightDistance);


    if (SmartDashboard.getBoolean("timertest", false)) {
      SmartDashboard.putBoolean("timertest", false);
    }
  }
}

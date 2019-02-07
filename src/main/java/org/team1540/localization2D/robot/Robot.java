package org.team1540.localization2D.robot;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.SPI.Port;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.io.IOException;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.team1540.localization2D.datastructures.Odometry;
import org.team1540.localization2D.datastructures.threed.Transform3D;
import org.team1540.localization2D.networking.UDPOdometryGoalSender;
import org.team1540.localization2D.networking.UDPTwistReceiver;
import org.team1540.localization2D.robot.commands.drivetrain.NetworkTablesTest;
import org.team1540.localization2D.robot.commands.drivetrain.UDPVelocityTwistDrive;
import org.team1540.localization2D.robot.commands.drivetrain.VelocityDrive;
import org.team1540.localization2D.robot.subsystems.DriveTrain;
import org.team1540.localization2D.utils.DualVisionTargetLocalizationUtils;
import org.team1540.localization2D.utils.LimelightLocalization;
import org.team1540.localization2D.utils.StateChangeDetector;
import org.team1540.localization2D.utils.TankDriveOdometryRunnable;
import org.team1540.rooster.power.PowerManager;
import org.team1540.rooster.util.SimpleCommand;
import org.team1540.rooster.wrappers.RevBlinken;
import org.team1540.rooster.wrappers.RevBlinken.ColorPattern;

public class Robot extends IterativeRobot {
  public static final DriveTrain drivetrain = new DriveTrain();
  public static AHRS navx = new AHRS(Port.kMXP);
  public static RevBlinken leds = new RevBlinken(9);

  private static Transform3D map_to_odom = Transform3D.IDENTITY;
  private static Transform3D odom_to_base_link = Transform3D.IDENTITY;

  public static TankDriveOdometryRunnable wheelOdometry;

  public static UDPOdometryGoalSender udpSender;
  public static UDPTwistReceiver udpReceiver;
  public static LimelightLocalization limelightLocalization;

  public static Transform3D lastOdomToLimelight;

  @Override
  public void robotInit() {

    SmartDashboard.putNumber("lp_p", 0);
    SmartDashboard.putNumber("lp_i", 0);
    SmartDashboard.putNumber("lp_d", 0);
    // disable unused things
    LiveWindow.disableAllTelemetry();
    PowerManager.getInstance().interrupt();

    // NetworkTablesTest networkTablesTest = new NetworkTablesTest();
    // networkTablesTest.setRunWhenDisabled(true);
    // networkTablesTest.start();

    Robot.navx.zeroYaw();
    Robot.drivetrain.zeroEncoders();

    wheelOdometry = new TankDriveOdometryRunnable(
        drivetrain::getLeftPositionMeters,
        drivetrain::getRightPositionMeters,
        () -> Math.toRadians(-Robot.navx.getAngle())
    );

    udpReceiver = new UDPTwistReceiver(5801, () -> {
      new Notifier(udpReceiver::attemptConnection).startSingle(1);
    });

    udpSender = new UDPOdometryGoalSender("10.15.40.202", 5800, () -> {
      new Notifier(udpSender::attemptConnection).startSingle(1);
    });

    limelightLocalization = new LimelightLocalization("limelight-a");

    StateChangeDetector limelightStateDetector = new StateChangeDetector(false);

    new Notifier(() -> {
      wheelOdometry.run();
      odom_to_base_link = wheelOdometry.getOdomToBaseLink();
      udpSender.setOdometry(new Odometry(odom_to_base_link, drivetrain.getTwist()));
      odom_to_base_link.toTransform2D().putToNetworkTable("Odometry/Debug/WheelOdometry");
      boolean targetFound = limelightLocalization.attemptUpdatePose();
      if (targetFound) {
        limelightLocalization.getBaseLinkToVisionTarget().toTransform2D().putToNetworkTable("LimelightLocalization/Debug/BaseLinkToVisionTarget");
        Transform3D goal = Robot.wheelOdometry.getOdomToBaseLink()
            .add(Robot.limelightLocalization.getBaseLinkToVisionTarget())
            .add(new Transform3D(new Vector3D(-0.65, 0, 0), Rotation.IDENTITY));

        lastOdomToLimelight = goal;
        goal.toTransform2D().putToNetworkTable("LimelightLocalization/Debug/BaseLinkToGoal");
      }
      if (OI.alignCommand == null || !OI.alignCommand.isRunning()) {
        if (targetFound) {
          leds.set(ColorPattern.LIME);
        } else {
          leds.set(ColorPattern.RED);
        }
      }
      try {
        udpSender.sendIt();
      } catch (IOException e) {
        DriverStation.reportWarning("Unable to send Odometry packet!", false);
      }
    }).startPeriodic(0.02);

    // Testing code
    Command testTEB = new SimpleCommand("Test TEB", () -> {
      new UDPVelocityTwistDrive().start();
    });
    SmartDashboard.putData(testTEB);
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
    Robot.drivetrain.configTalonsForVelocity();
  }

  @Override
  public void teleopInit() {
    Robot.drivetrain.reset();
    Robot.drivetrain.enableCurrentLimiting();
    Robot.drivetrain.configTalonsForVelocity();
  }

  @Override
  public void robotPeriodic() {
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

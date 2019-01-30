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
import org.team1540.localization2D.robot.commands.drivetrain.UDPVelocityTwistDrive;
import org.team1540.localization2D.robot.commands.drivetrain.VelocityDrive;
import org.team1540.localization2D.robot.subsystems.DriveTrain;
import org.team1540.localization2D.runnables.TankDriveOdometryRunnable;
import org.team1540.localization2D.utils.DualVisionTargetLocalizationUtils;
import org.team1540.localization2D.utils.LimelightLocalization;
import org.team1540.localization2D.utils.StateChangeDetector;
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
    // disable unused things
    LiveWindow.disableAllTelemetry();
    PowerManager.getInstance().interrupt();

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

    udpSender = new UDPOdometryGoalSender("10.15.40.201", 5800, () -> {
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
        if (limelightStateDetector.didChangeToTrue(targetFound)) {
          leds.set(ColorPattern.LIME);
        }
        if (limelightStateDetector.didChangeToFalse(targetFound)) {
          leds.set(ColorPattern.RED);
        }
      }
      try {
        udpSender.sendIt();
      } catch (IOException e) {
        DriverStation.reportWarning("Unable to send Odometry packet!", false);
      }
    }).startPeriodic(0.01);

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
    // new PercentDrive().start();
    new VelocityDrive().start();
  }

  @Override
  public void robotPeriodic() {
    Scheduler.getInstance().run();
    hatchCamPeriodic();
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

  private void hatchCamPeriodic() {
    NetworkTable hatchTable = NetworkTableInstance.getDefault().getTable("hatch-cam");

    Number[] centers = hatchTable.getEntry("hatch-centers").getNumberArray(new Number[0]);

    if (centers.length == 0) {
      return;
    }

    double CAMERA_TILT = Math.toRadians(-54);
    // double CAMERA_TILT = Math.toRadians((90-54)*-1);
    double CAMERA_ROLL = Math.toRadians(0);
    double CAMERA_YAW = Math.toRadians(-11.42);

    double hoz_fov = Math.toRadians(59.70);
    double vert_fov = Math.toRadians(33.58);

    double planeHeight = 0; // Height of vision targets in meters
    Vector3D cameraPosition = new Vector3D(0.076, -0.18, 1.32); // Position of camera in meters

    double screenHeight = 198;
    double screenWidth = 320;

    Vector2D topAngle = new Vector2D(0, 1);
    Vector2D bottomAngle = new Vector2D(0, -1);

    Rotation cameraTilt = new Rotation(Vector3D.PLUS_J, CAMERA_TILT, RotationConvention.FRAME_TRANSFORM);
    Rotation cameraRoll = new Rotation(Vector3D.PLUS_I, CAMERA_ROLL, RotationConvention.FRAME_TRANSFORM);
    Rotation cameraYaw = new Rotation(Vector3D.PLUS_K, CAMERA_YAW, RotationConvention.FRAME_TRANSFORM);

    Rotation cameraRotation = cameraYaw.applyTo(cameraTilt.applyTo(cameraRoll));

    Vector3D topPoint = DualVisionTargetLocalizationUtils.getIntersection(
        DualVisionTargetLocalizationUtils.lineFromScreenAngles(
            DualVisionTargetLocalizationUtils.anglesFromScreenSpace(topAngle, hoz_fov, vert_fov), cameraPosition, cameraRotation), planeHeight);
    Vector3D bottomPoint = DualVisionTargetLocalizationUtils.getIntersection(
        DualVisionTargetLocalizationUtils.lineFromScreenAngles(
            DualVisionTargetLocalizationUtils.anglesFromScreenSpace(bottomAngle, hoz_fov, vert_fov), cameraPosition, cameraRotation), planeHeight);

    hatchTable.getEntry("debug/top").setNumberArray(new Number[]{topPoint.getX(), topPoint.getY(), topPoint.getZ()});
    hatchTable.getEntry("debug/bottom").setNumberArray(new Number[]{bottomPoint.getX(), bottomPoint.getY(), topPoint.getZ()});

    double distance = topPoint.distance(bottomPoint);

    hatchTable.getEntry("debug/distance").setNumber(distance);

    Vector3D hatchCenterPoint = new Vector3D(
        -((double) centers[1] - screenHeight) / screenHeight * distance,
        -((double) centers[0] - screenWidth / 2) / screenHeight * distance,
        0);

    hatchTable.getEntry("debug/center-point").setNumberArray(new Number[]{hatchCenterPoint.getX(), hatchCenterPoint.getY()});

    Transform3D base_link_to_hatch_flat = new Transform3D(bottomPoint, new Rotation(Vector3D.PLUS_I, topPoint.subtract(bottomPoint)));

    Transform3D base_link_to_hatch = base_link_to_hatch_flat.add(new Transform3D(hatchCenterPoint, Rotation.IDENTITY));
  }
}

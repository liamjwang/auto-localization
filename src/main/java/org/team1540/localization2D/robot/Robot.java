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
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.team1540.localization2D.datastructures.Odometry;
import org.team1540.localization2D.datastructures.threed.Transform3D;
import org.team1540.localization2D.networking.UDPOdometryGoalSender;
import org.team1540.localization2D.networking.UDPTwistReceiver;
import org.team1540.localization2D.robot.commands.drivetrain.UDPVelocityTwistDrive;
import org.team1540.localization2D.robot.commands.drivetrain.VelocityDrive;
import org.team1540.localization2D.robot.rumble.RumbleForTime;
import org.team1540.localization2D.robot.subsystems.DriveTrain;
import org.team1540.localization2D.runnables.TankDriveOdometryRunnable;
import org.team1540.localization2D.utils.CameraLocalization;
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

    new Notifier(() -> {
      wheelOdometry.run();
      udpSender.setOdometry(new Odometry(wheelOdometry.getOdomToBaseLink(), drivetrain.getTwist()));
      odom_to_base_link = wheelOdometry.getOdomToBaseLink();

      // Debug
      SmartDashboard.putNumber("pose-position-x", wheelOdometry.getOdomToBaseLink().getPosition().getX());
      SmartDashboard.putNumber("pose-position-y", wheelOdometry.getOdomToBaseLink().getPosition().getY());
      SmartDashboard.putNumber("pose-orientation-z", wheelOdometry.getOdomToBaseLink().getOrientation().getAngles(RotationOrder.XYZ, RotationConvention.FRAME_TRANSFORM)[2]);
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
    limelightLocalizationPeriodic();
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

  private boolean isFound = false;

  private void limelightLocalizationPeriodic() {

    double CAMERA_TILT = Math.toRadians(-40.2);
    double CAMERA_ROLL = Math.toRadians(-1.38);
    double PLANE_HEIGHT = 0.74; // Height of vision targets in meters
    Vector3D CAMERA_POSITION = new Vector3D(0.15, 0, 1.26); // Position of camera in meters


    NetworkTable limelightTable = NetworkTableInstance.getDefault().getTable("limelight-a");

    // TODO: Filter limelight contours using size, angle, etc.
    double tx0 = limelightTable.getEntry("tx0").getDouble(100);
    double ty0 = limelightTable.getEntry("ty0").getDouble(100);

    double tx1 = limelightTable.getEntry("tx1").getDouble(100);
    double ty1 = limelightTable.getEntry("ty1").getDouble(100);

    if (tx0 + tx1 + ty0 + ty1 > 5) {
      System.out.println("Unable to get limelight values!");
      return;
    }

    double upperLimit = 0.86;
    double lowerLimit = -0.65;
    double leftAndRightLimit = 0.90;

    boolean debug = false;

    if (tx0 == 0 || tx1 == 0 || ty0 == 0 || ty1 == 0) {
      if (debug) { System.out.println("Ignoring limelight - highly unlikely values"); }
      disableLimelightValues();
      return;
    }
    if (ty0 > upperLimit || ty1 > upperLimit) {
      if (debug) {System.out.println("Ignoring limelight - upper limit");}
      disableLimelightValues();
      return;
    }
    if (ty0 < lowerLimit || ty1 < lowerLimit) {
      if (debug) {System.out.println("Ignoring limelight - lower limit");}
      disableLimelightValues();
      return;
    }
    if (Math.abs(tx0) > leftAndRightLimit || Math.abs(tx1) > leftAndRightLimit) {
      if (debug) {System.out.println("Ignoring limelight - left/right limit");}
      disableLimelightValues();
      return;
    }
    if (debug) {System.out.println("Good limelight values!");}
    if (OI.alignCommand == null || !OI.alignCommand.isRunning()) {
      leds.set(ColorPattern.LIME);
      if (!isFound) {
        isFound = true;
        new RumbleForTime(OI.driver, 1, 0.2).start();
      }
    }

    Vector2D leftAngles = new Vector2D(-tx0, ty0);
    Vector2D rightAngles = new Vector2D(-tx1, ty1);

    Rotation cameraTilt = new Rotation(Vector3D.PLUS_J, CAMERA_TILT, RotationConvention.FRAME_TRANSFORM);
    Rotation cameraRoll = new Rotation(Vector3D.PLUS_I, CAMERA_ROLL, RotationConvention.FRAME_TRANSFORM);

    Rotation cameraRotation = cameraTilt.applyTo(cameraRoll);
    Transform3D base_link_to_target = CameraLocalization.poseFromTwoCamPoints(leftAngles, rightAngles, PLANE_HEIGHT, CAMERA_POSITION, cameraRotation, OI.LIMELIGHT_HORIZONTAL_FOV, OI.LIMELIGHT_VERTICAL_FOV);

    Transform3D odom_to_target = odom_to_base_link.add(base_link_to_target);

    // double[] angles = odom_to_target.getOrientation().getAngles(RotationOrder.XYZ, RotationConvention.FRAME_TRANSFORM);
    //
    // double off = -0.65; // TODO: do this with transforms
    // // double off = 0;
    // double x_off = odom_to_target.getPosition().getX() + off * Math.cos(angles[2]);
    // double y_off = odom_to_target.getPosition().getY() + off * Math.sin(angles[2]);

    // Transform3D limePoseWithOffset = odom_to_target;
    Transform3D limePoseWithOffset = odom_to_target.add(new Transform3D(new Vector3D(-0.65, 0, 0), Rotation.IDENTITY));

    // Transform3D limePoseWithOffset = new Transform3D(new Vector3D(x_off, y_off, 0), new Rotation(RotationOrder.XYZ, RotationConvention.FRAME_TRANSFORM, 0, 0, angles[2]));

    SmartDashboard.putNumber("limelight-pose/position/x", limePoseWithOffset.getPosition().getX());
    SmartDashboard.putNumber("limelight-pose/position/y", limePoseWithOffset.getPosition().getY());
    SmartDashboard.putNumber("limelight-pose/orientation/z", limePoseWithOffset.getOrientation().getAngles(RotationOrder.XYZ, RotationConvention.FRAME_TRANSFORM)[2]);
    SmartDashboard.putBoolean("limelight-pose/correct", true);
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

    Vector3D topPoint = CameraLocalization.getIntersection(
        CameraLocalization.lineFromScreenAngles(
            CameraLocalization.anglesFromScreenSpace(topAngle, hoz_fov, vert_fov), cameraPosition, cameraRotation), planeHeight);
    Vector3D bottomPoint = CameraLocalization.getIntersection(
        CameraLocalization.lineFromScreenAngles(
            CameraLocalization.anglesFromScreenSpace(bottomAngle, hoz_fov, vert_fov), cameraPosition, cameraRotation), planeHeight);

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


  private void disableLimelightValues() {
    SmartDashboard.putBoolean("limelight-pose/correct", false);
    if (OI.alignCommand == null || !OI.alignCommand.isRunning()) {
      leds.set(ColorPattern.RED);
    }
    if (isFound) {
      isFound = false;
    }
  }
}

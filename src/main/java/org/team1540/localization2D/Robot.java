package org.team1540.localization2D;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.IterativeRobot;
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
import org.team1540.localization2D.commands.drivetrain.PercentDrive;
import org.team1540.localization2D.commands.drivetrain.UDPVelocityTwistDrive;
import org.team1540.localization2D.datastructures.Transform;
import org.team1540.localization2D.networking.UDPServer;
import org.team1540.localization2D.rumble.RumbleForTime;
import org.team1540.localization2D.subsystems.DriveTrain;
import org.team1540.localization2D.utils.CameraLocalization;
import org.team1540.localization2D.utils.LocalizationAccum2D;
import org.team1540.rooster.power.PowerManager;
import org.team1540.rooster.util.SimpleCommand;
import org.team1540.rooster.wrappers.RevBlinken;
import org.team1540.rooster.wrappers.RevBlinken.ColorPattern;

public class Robot extends IterativeRobot {
  public static final DriveTrain drivetrain = new DriveTrain();
  public static AHRS navx = new AHRS(Port.kMXP);
  public static UDPServer serv;
  public static RevBlinken leds = new RevBlinken(9);

  static {
    try {
      serv = new UDPServer();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Transform goal_pose = Transform.ZERO;
  private Transform map_to_odom = Transform.ZERO;
  private Transform odom_to_base_link = Transform.ZERO;


  @Override
  public void robotInit() {
    // disable unused things
    LiveWindow.disableAllTelemetry();
    PowerManager.getInstance().interrupt();

    localizationInit();

    Command runTEB = new SimpleCommand("Start segment", () -> {
      new UDPVelocityTwistDrive(2, 0, 0, false).start();
    });
    SmartDashboard.putData(runTEB);
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
    new PercentDrive().start();
    //    new VelocityDrive().start();
  }

  @Override
  public void robotPeriodic() {
    Scheduler.getInstance().run();
    localizationPeriodic();
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

  private static LocalizationAccum2D accum2D = new LocalizationAccum2D();

  private void localizationInit() {
    Robot.navx.zeroYaw();
    accum2D.reset();
    Robot.drivetrain.zeroEncoders();
  }

  private void localizationPeriodic() {
    double leftDistance = drivetrain.getLeftPosition() / Tuning.drivetrainTicksPerMeter;
    double rightDistance = drivetrain.getRightPosition() / Tuning.drivetrainTicksPerMeter;
    double gyroAngle = Math.toRadians(-Robot.navx.getAngle());

    accum2D.update(leftDistance, rightDistance, gyroAngle);

    SmartDashboard.putNumber("pose-position-x", accum2D.getXpos());
    SmartDashboard.putNumber("pose-position-y", accum2D.getYpos());
    SmartDashboard.putNumber("pose-orientation-z", gyroAngle);

    double leftVelocity = drivetrain.getLeftVelocity() * 10 / Tuning.drivetrainTicksPerMeter;
    double rightVelocity = drivetrain.getRightVelocity() * 10 / Tuning.drivetrainTicksPerMeter;

    double xvel = (leftVelocity + rightVelocity) / 2;
    double thetavel = (leftVelocity - rightVelocity) / (Tuning.drivetrainRadius) / 2;

    this.odom_to_base_link = new Transform(
        new Vector3D(accum2D.getXpos(), accum2D.getYpos(), 0),
        new Rotation(RotationOrder.XYZ, RotationConvention.FRAME_TRANSFORM, 0, 0, gyroAngle));

    Transform map_to_base_link = addPoses(this.map_to_odom, this.odom_to_base_link);

    SmartDashboard.putNumber("robot-pose/position/x", map_to_base_link.position.getX());
    SmartDashboard.putNumber("robot-pose/position/y", map_to_base_link.position.getY());
    SmartDashboard.putNumber("robot-pose/orientation/z", map_to_base_link.orientation.getAngles(RotationOrder.XYZ, RotationConvention.FRAME_TRANSFORM)[2]);
    if (serv != null) {
      try {
        serv.sendPoseAndTwist(
            map_to_base_link.position.getX(),
            map_to_base_link.position.getY(),
            map_to_base_link.orientation.getAngles(RotationOrder.XYZ, RotationConvention.FRAME_TRANSFORM)[2],
            xvel,
            thetavel);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }


    // SmartDashboard.putNumber("Left Distance", leftDistance);
    // SmartDashboard.putNumber("Right Distance", rightDistance);


  }

  private boolean isFound = false;

  private void limelightLocalizationPeriodic() {

    double CAMERA_TILT = Math.toRadians(-40.2);
    double CAMERA_ROLL = Math.toRadians(-1.38);
    double PLANE_HEIGHT = 0.74; // Height of vision targets in meters
    // Vector3D CAMERA_POSITION = new Vector3D(0, 0, 1.26); // Position of camera in meters
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
    Transform base_link_to_target = CameraLocalization.poseFromTwoCamPoints(leftAngles, rightAngles, PLANE_HEIGHT, CAMERA_POSITION, cameraRotation, CameraLocalization.LIMELIGHT_HORIZONTAL_FOV, CameraLocalization.LIMELIGHT_VERTICAL_FOV);

    Transform odom_to_target = addPoses(this.odom_to_base_link, base_link_to_target);

    double[] angles = odom_to_target.orientation.getAngles(RotationOrder.XYZ, RotationConvention.FRAME_TRANSFORM);

    double off = -0.6; // TODO: do this with transforms
    // double off = 0;
    double x_off = odom_to_target.position.getX() + off * Math.cos(angles[2]);
    double y_off = odom_to_target.position.getY() + off * Math.sin(angles[2]);

    Transform limePoseWithOffset = new Transform(new Vector3D(x_off, y_off, 0), new Rotation(RotationOrder.XYZ, RotationConvention.FRAME_TRANSFORM, 0, 0, angles[2]));


    SmartDashboard.putNumber("limelight-pose/position/x", limePoseWithOffset.position.getX());
    SmartDashboard.putNumber("limelight-pose/position/y", limePoseWithOffset.position.getY());
    SmartDashboard.putNumber("limelight-pose/orientation/z", limePoseWithOffset.orientation.getAngles(RotationOrder.XYZ, RotationConvention.FRAME_TRANSFORM)[2]);
    SmartDashboard.putBoolean("limelight-pose/correct", true);

    SmartDashboard.putNumber("limelight-pose/position/x_og", base_link_to_target.position.getX());
    SmartDashboard.putNumber("limelight-pose/position/y_og", base_link_to_target.position.getY());
    SmartDashboard.putNumber("limelight-pose/orientation/z_og", base_link_to_target.orientation.getAngles(RotationOrder.XYZ, RotationConvention.FRAME_TRANSFORM)[2]);

    SmartDashboard.putNumber("map-odom/position/x", map_to_odom.position.getX());
    SmartDashboard.putNumber("map-odom/position/y", map_to_odom.position.getY());
    SmartDashboard.putNumber("map-odom/orientation/z", map_to_odom.orientation.getAngles(RotationOrder.XYZ, RotationConvention.FRAME_TRANSFORM)[2]);
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
    // Vector3D CAMERA_POSITION = new Vector3D(0, 0, 1.26); // Position of camera in meters
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

    Transform base_link_to_hatch_flat = new Transform(bottomPoint, new Rotation(Vector3D.PLUS_I, topPoint.subtract(bottomPoint)));

    Transform base_link_to_hatch = addPoses(base_link_to_hatch_flat, new Transform(hatchCenterPoint, Rotation.IDENTITY));
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

  private Transform addPoses(Transform from, Transform to) {
    return new Transform(from.orientation.applyInverseTo(to.position).add(from.position), from.orientation.applyTo(to.orientation));
  }

  private Transform subtractPoses(Transform from, Transform to) {
    return new Transform(from.position.subtract(from.orientation.applyInverseTo(to.position)), from.orientation.applyInverseTo(to.orientation));
  }

  public static double getPosX() {
    return accum2D.getXpos();
  }

  public static double getPosY() {
    return accum2D.getYpos();
  }
}

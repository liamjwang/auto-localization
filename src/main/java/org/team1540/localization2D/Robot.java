package org.team1540.localization2D;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.io.IOException;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.team1540.localization2D.autogroups.TestSequence;
import org.team1540.localization2D.commands.drivetrain.PercentDrive;
import org.team1540.localization2D.commands.drivetrain.UDPVelocityTwistDrive;
import org.team1540.localization2D.subsystems.DriveTrain;
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
    //      runTEB.start();
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

    double pos_x = SmartDashboard.getNumber("limelight-pose/position/x", 0);
    double pos_y = SmartDashboard.getNumber("limelight-pose/position/y", 0);
    double ori_z = SmartDashboard.getNumber("limelight-pose/orientation/z", 0);
    this.goal_pose = new Transform(new Vector3D(pos_x,
        pos_y, 0), new Rotation(RotationOrder.XYZ, RotationConvention.FRAME_TRANSFORM, 0, 0, ori_z));
    new TestSequence(pos_x, pos_y, ori_z).start();
    // new UDPVelocityTwistDrive().start();
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
  public void testInit() {
    Robot.drivetrain.reset();
    Robot.drivetrain.configTalonsForVelocity();
    new UDPVelocityTwistDrive(0, 0, 0, false).start();
  }

  @Override
  public void robotPeriodic() {
    //    SmartDashboard.putData(Scheduler.getInstance());
    NetworkTable limeTable = NetworkTableInstance.getDefault().getTable("limelight-a");
    double tx0 = 27.85 * limeTable.getEntry("tx0").getDouble(0);
    limeTable.getEntry("tx00").setDouble(tx0);

    double tx1 = 27.85 * limeTable.getEntry("tx1").getDouble(0);
    limeTable.getEntry("tx11").setDouble(tx1);

    Scheduler.getInstance().run();
    localizationPeriodic();
    limelightLocalizationPeriodic();
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


    //          SmartDashboard.putNumber("../limelight/tx00", SmartDashboard.getNumber("../limelight/tx0", 0)*26.85);

    //    SmartDashboard.putNumber("twist-linear-x", xvel);
    //    SmartDashboard.putNumber("twist-angular-z", thetavel);

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
    }

    Vector2D leftAngles = new Vector2D(-tx0, ty0);
    Vector2D rightAngles = new Vector2D(-tx1, ty1);

    Rotation cameraTilt = new Rotation(Vector3D.PLUS_J, CAMERA_TILT, RotationConvention.FRAME_TRANSFORM);
    Rotation cameraRoll = new Rotation(Vector3D.PLUS_I, CAMERA_ROLL, RotationConvention.FRAME_TRANSFORM);

    Rotation cameraRotation = cameraTilt.applyTo(cameraRoll);
    Transform base_link_to_target = LimelightLocalization.poseFromTwoCamPoints(leftAngles, rightAngles, PLANE_HEIGHT, CAMERA_POSITION, cameraRotation);

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


  private void disableLimelightValues() {
    SmartDashboard.putBoolean("limelight-pose/correct", false);
    if (OI.alignCommand == null || !OI.alignCommand.isRunning()) {
      leds.set(ColorPattern.RED);
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

package org.team1540.localization2D.runnables;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.team1540.localization2D.datastructures.threed.Transform3D;
import org.team1540.localization2D.robot.OI;
import org.team1540.localization2D.robot.rumble.RumbleForTime;
import org.team1540.localization2D.utils.DualVisionTargetLocalizationUtils;
import org.team1540.rooster.wrappers.RevBlinken.ColorPattern;

/**
 * Runnable class for localization with a Limelight over NetworkTables.
 * The {@link #run()} method is meant to be called periodically using a {@link edu.wpi.first.wpilibj.Notifier}
 */
public class LimelightLocalizationRunnable implements Runnable {

  private boolean isFound = false;


  public LimelightLocalizationRunnable(String limeTableName) {

  }

  @Override
  public void run() {
    double CAMERA_TILT = Math.toRadians(-40.2);
    double CAMERA_ROLL = Math.toRadians(-1.38);
    double PLANE_HEIGHT = 0.74; // Height of vision targets in meters
    Vector3D CAMERA_POSITION = new Vector3D(0.15, 0, 1.26); // Position of camera in meters


    NetworkTable limelightTable = NetworkTableInstance.getDefault().getTable("limelight-a");

    // TODO: Filter limelight contours using size, angle, etc.
    double tx0 = limelightTable.getEntry("tx0").getDouble(0);
    double ty0 = limelightTable.getEntry("ty0").getDouble(0);

    double tx1 = limelightTable.getEntry("tx1").getDouble(0);
    double ty1 = limelightTable.getEntry("ty1").getDouble(0);

    if (tx0 + tx1 + ty0 + ty1 > 5) {
      System.out.println("Unable to get limelight values!");
      return;
    }

    double upperLimit = 0.86;
    double lowerLimit = -0.65;
    double leftAndRightLimit = 0.90;


    boolean debug = false;


    if (tx0 == 0 || tx1 == 0 || ty0 == 0 || ty1 == 0) {
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
    Transform3D base_link_to_target = DualVisionTargetLocalizationUtils.poseFromTwoCamPoints(leftAngles, rightAngles, PLANE_HEIGHT, CAMERA_POSITION, cameraRotation, OI.LIMELIGHT_HORIZONTAL_FOV, OI.LIMELIGHT_VERTICAL_FOV);

    Transform3D odom_to_target = odom_to_base_link.add(base_link_to_target);

    Transform3D limePoseWithOffset = odom_to_target.add(new Transform3D(new Vector3D(-0.65, 0, 0), Rotation.IDENTITY));

    SmartDashboard.putNumber("limelight-pose/position/x", limePoseWithOffset.getPosition().getX());
    SmartDashboard.putNumber("limelight-pose/position/y", limePoseWithOffset.getPosition().getY());
    SmartDashboard.putNumber("limelight-pose/orientation/z", limePoseWithOffset.getOrientation().getAngles(RotationOrder.XYZ, RotationConvention.FRAME_TRANSFORM)[2]);
    SmartDashboard.putBoolean("limelight-pose/correct", true);
  }

  public Transform3D getBaseLinkToVisionTarget() {
    return odomToBaseLink;
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

package org.team1540.localization2D.utils;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.team1540.localization2D.datastructures.threed.Transform3D;
import org.team1540.localization2D.robot.Tuning;

/**
 * Class for localization with a Limelight over NetworkTables.
 */
public class JonathanLocalization {

  private Transform3D baseLinkToVisionTarget;
  private JonathanInterface jonathanInterface;
  private long timeLastAcquired = 0;

  public JonathanLocalization(String limeTableName) {
    jonathanInterface = new JonathanInterface(limeTableName);
  }

  public boolean attemptUpdatePose() {
    double CAMERA_TILT = Math.toRadians(-29);
    double CAMERA_ROLL = Math.toRadians(0);
    double PLANE_HEIGHT = 0.74; // Height of vision targets in meters
    Vector3D CAMERA_POSITION = new Vector3D(0.15, 0.15, 1.14); // Position of camera in meters

    // TODO: Filter limelight contours using size, angle, etc.

    double upperLimit = 1;
    double lowerLimit = -1;
    double leftAndRightLimit = 0.35;

    Vector2D point0 = jonathanInterface.getRawPoint(0);
    Vector2D point1 = jonathanInterface.getRawPoint(1);

    if (point0.equals(Vector2D.ZERO) || point1.equals(Vector2D.ZERO)
        || !VisionUtils.isWithinBounds(point0, upperLimit, lowerLimit, leftAndRightLimit, -leftAndRightLimit)
        || !VisionUtils.isWithinBounds(point1, upperLimit, lowerLimit, leftAndRightLimit, -leftAndRightLimit)) {
      return false;
    }

    Rotation cameraTilt = new Rotation(Vector3D.PLUS_J, CAMERA_TILT, RotationConvention.FRAME_TRANSFORM);
    Rotation cameraRoll = new Rotation(Vector3D.PLUS_I, CAMERA_ROLL, RotationConvention.FRAME_TRANSFORM);

    Rotation cameraRotation = cameraTilt.applyTo(cameraRoll);
    baseLinkToVisionTarget = DualVisionTargetLocalizationUtils.poseFromTwoCamPoints(point0, point1, PLANE_HEIGHT, CAMERA_POSITION, cameraRotation, Tuning.JONATHAN_HORIZONTAL_FOV, Tuning.JONATHAN_VERTICAL_FOV);

    timeLastAcquired = System.currentTimeMillis();
    return true;
  }

  public boolean targetWasAcquired() {
    return baseLinkToVisionTarget != null;
  }

  public Transform3D getBaseLinkToVisionTarget() {
    return baseLinkToVisionTarget;
  }

  public long millisSinceLastAcquired() {
    return System.currentTimeMillis() - timeLastAcquired;
  }
}

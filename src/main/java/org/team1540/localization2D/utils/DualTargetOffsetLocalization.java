package org.team1540.localization2D.utils;

import java.util.function.Supplier;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.team1540.localization2D.datastructures.threed.Transform3D;
import org.team1540.localization2D.robot.OI;

/**
 * Class for localization with a Limelight over NetworkTables.
 */
public class DualTargetOffsetLocalization {

  private final Vector2D cameraFOV;
  private final Transform3D baseLinkToCamera;
  private final Double planeHeight;
  private final Supplier<Vector2D> pointGetterA;
  private final Supplier<Vector2D> pointGetterB;
  private Transform3D baseLinkToVisionTarget;
  private long timeLastAcquired = 0;

  /**
   *
   * @param cameraFOV Camera FOV in radians, where X is the horizontal FOV and Y is the vertical FOV
   * @param baseLinkToCamera Transform from center of the robot to the camera
   * @param planeHeight Height of vision targets in meters
   * @param pointGetterA Returns
   * @param pointGetterB
   */
  public DualTargetOffsetLocalization(Vector2D cameraFOV, Transform3D baseLinkToCamera, Double planeHeight, Supplier<Vector2D> pointGetterA, Supplier<Vector2D> pointGetterB) {
    this.cameraFOV = cameraFOV;
    this.baseLinkToCamera = baseLinkToCamera;
    this.planeHeight = planeHeight;
    this.pointGetterA = pointGetterA;
    this.pointGetterB = pointGetterB;
  }

  public boolean attemptUpdatePose() {
    Vector2D pointA = pointGetterA.get();
    Vector2D pointB = pointGetterB.get();

    if (pointA == null || pointB == null) {
      return false;
    }

    baseLinkToVisionTarget = DualVisionTargetLocalizationUtils.poseFromTwoCamPoints(pointA, pointB, planeHeight, baseLinkToCamera.getPosition(), baseLinkToCamera.getOrientation(), cameraFOV.getX(), cameraFOV.getY());

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

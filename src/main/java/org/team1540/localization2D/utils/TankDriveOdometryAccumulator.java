package org.team1540.localization2D.utils;

import org.team1540.localization2D.datastructures.threed.Transform3D;

public class TankDriveOdometryAccumulator {

  private Transform3D odomToBaseLink = Transform3D.ZERO;

  private double distancePrevLeft;
  private double distancePrevRight;
  private double angleRadsPrev;

  /**
   * @param distanceLeft Absolute distance in meters of left wheels
   * @param distanceRight Absolute distance in meters of right wheels
   * @param continuousAngle Continuous absolute angle in radians (should NOT jump from 2PI to 0)
   */
  public void update(double distanceLeft, double distanceRight, double continuousAngle) { // TODO: This angle should not need to be continuous
    double deltaDistanceLeft = distanceLeft - distancePrevLeft;
    double deltaDistanceRight = distanceRight - distancePrevRight;
    double deltaRads = continuousAngle - angleRadsPrev;

    distancePrevLeft = distanceLeft;
    distancePrevRight = distanceRight;
    angleRadsPrev = continuousAngle;

    Transform3D deltaDistance = TankDriveOdometry.calcDeltaTransformFromTankDriveDistances(deltaDistanceLeft, deltaDistanceRight, deltaRads);

    odomToBaseLink = odomToBaseLink.add(deltaDistance);
  }

  public Transform3D getTransform() {
    return odomToBaseLink;
  }
}

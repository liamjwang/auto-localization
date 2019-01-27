package org.team1540.localization2D.utils;

import org.team1540.localization2D.datastructures.twod.Transform2D;

public class TankDriveOdometryAccumulator {

  private double x;
  private double y;
  private double theta;

  private double distancePrevLeft;
  private double distancePrevRight;
  private double angleRadsPrev;

  /**
   * @param distanceLeft Absolute distance in meters of left wheels
   * @param distanceRight Absolute distance in meters of right wheels
   * @param continuousAngle Continuous absolute angle in radians (should NOT jump from 2PI to 0)
   */
  public void update(double distanceLeft, double distanceRight, double continuousAngle) {
    double deltaDistanceLeft = distanceLeft - distancePrevLeft;
    double deltaDistanceRight = distanceRight - distancePrevRight;
    double deltaRads = continuousAngle - angleRadsPrev;

    distancePrevLeft = distanceLeft;
    distancePrevRight = distanceRight;
    angleRadsPrev = continuousAngle;

    Transform2D deltaDistance = TankDriveOdometry.calcDeltaPoseFromTankDriveDistances(deltaDistanceLeft, deltaDistanceRight, deltaRads);

    // TODO: Move this tranformation to a transformations class
    x += deltaDistance.getY() * Math.cos(continuousAngle + Math.PI / 2)
        + deltaDistance.getX() * Math.cos(continuousAngle);
    y += deltaDistance.getY() * Math.sin(continuousAngle + Math.PI / 2)
        + deltaDistance.getX() * Math.sin(continuousAngle);
    theta = continuousAngle;
  }

  public Transform2D getTransform2D() {
    return new Transform2D(x, y, theta);
  }
}

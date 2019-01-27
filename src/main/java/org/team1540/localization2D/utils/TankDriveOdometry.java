package org.team1540.localization2D.utils;

import org.team1540.localization2D.datastructures.threed.Transform3D;

public class TankDriveOdometry {
  public static Transform3D calcDeltaPoseFromTankDriveDistances(double deltaDistanceLeft, double deltaDistanceRight, double deltaAngle) {
    double deltaX;
    double deltaY;

    if (deltaAngle == 0) { // If the robot has not turned,
      deltaX = (deltaDistanceLeft + deltaDistanceRight)
          / 2; // The pose x has changed by the average of the wheel distances
      deltaY = 0; // And there was no change in Y
    } else {
      double radiusFromLeftArc = TrigUtils.radiusFromArcAndAngle(deltaDistanceLeft, deltaAngle);
      double radiusFromRightArc = TrigUtils.radiusFromArcAndAngle(deltaDistanceRight, deltaAngle);

      double avgRadius = (radiusFromLeftArc + radiusFromRightArc) / 2;

      deltaX = calcDeltaY(avgRadius, deltaAngle);
      deltaY = calcDeltaX(avgRadius, deltaAngle);
    }

    return new Transform3D(deltaX, deltaY, deltaAngle);
  }

  public static double calcDeltaX(double radius, double deltaRads) {
    return radius * (1.0 - Math.cos(deltaRads));
  }

  public static double calcDeltaY(double radius, double deltaRads) {
    return radius * Math.sin(deltaRads);
  }
}

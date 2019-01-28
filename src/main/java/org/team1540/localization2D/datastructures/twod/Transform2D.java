package org.team1540.localization2D.datastructures.twod;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * 2D pose data structure class
 */
public class Transform2D {
  public static final Transform2D ZERO = new Transform2D(0, 0, 0);

  private final double x;
  private final double y;
  private final double theta;

  /**
   * @param x Distance in meters in the X direction
   * @param y Distance in meters in the Y direction
   * @param theta Angle in radians between -PI and PI
   */
  public Transform2D(double x, double y, double theta) {
    this.x = x;
    this.y = y;
    this.theta = theta;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getTheta() {
    return theta;
  }

  public Vector2D getPositionVector() {
    return new Vector2D(x, y);
  }
}

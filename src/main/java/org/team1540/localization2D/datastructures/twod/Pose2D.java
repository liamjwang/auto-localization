package org.team1540.localization2D.datastructures.twod;

/**
 *  2D pose data structure class
 */
public class Pose2D {
  public static final Pose2D ZERO = new Pose2D(0, 0, 0);

  private final double x;
  private final double y;
  private final double theta;

  /**
   * @param x Distance in meters in the X direction
   * @param y Distance in meters in the Y direction
   * @param theta Angle in radians between -PI and PI
   */
  public Pose2D(double x, double y, double theta) {
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
}

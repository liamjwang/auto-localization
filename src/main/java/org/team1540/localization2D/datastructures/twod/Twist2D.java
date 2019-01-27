package org.team1540.localization2D.datastructures.twod;

/**
 * 2D twist data structure class
 */
public class Twist2D {
  public static final Twist2D ZERO = new Twist2D(0, 0, 0);

  public double x = 0;
  public double y = 0;
  public double omega = 0;

  /**
   * @param x Linear velocity in X direction in meters per second
   * @param y Linear velocity in Y direction in meters per second
   * @param omega Angular velocity counter-clockwise in radians per second
   */
  public Twist2D(double x, double y, double omega) {
    this.x = x;
    this.y = y;
    this.omega = omega;
  }
}

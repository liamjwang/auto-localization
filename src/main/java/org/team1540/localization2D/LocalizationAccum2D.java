package org.team1540.localization2D;

public class LocalizationAccum2D {

  private double xpos;
  private double ypos;
  private double theta;

  private double distancePrevLeft;
  private double distancePrevRight;
  private double angleRadsPrev;

  public LocalizationAccum2D() {
    reset();
  }

  public void reset() {
    xpos = 0;
    ypos = 0;
    theta = 0;

    distancePrevLeft = 0;
    distancePrevRight = 0;
    angleRadsPrev = 0;
  }

  /**
   * @param distanceLeft
   * Absolute distance of wheels
   * @param distanceRight
   * Absolute distance of wheels
   * @param angleRads
   * Absolute angle in radians
   */
  public void update(double distanceLeft, double distanceRight, double angleRads) {
    double deltaDistanceLeft = distanceLeft - distancePrevLeft;
    double deltaDistanceRight = distanceRight - distancePrevRight;
    double deltaRads = angleRads - angleRadsPrev;

    distancePrevLeft = deltaDistanceLeft;
    distancePrevRight = deltaDistanceRight;
    angleRadsPrev = deltaRads;

    // Calculate radius of turn
    double avgRadius = (calcRadius(deltaDistanceLeft, deltaRads) + calcRadius(deltaDistanceRight, deltaRads)) /2;

    // Increment the accumulators
    xpos += calcDeltaX(avgRadius, deltaRads);
    ypos += calcDeltaY(avgRadius, deltaRads);
  }

  private double calcRadius(double arcLength, double angleRads) {
    return arcLength/angleRads;
  }

  private double calcDeltaX(double radius, double deltaRads) {
    return radius*(1-Math.cos(deltaRads));
  }

  private double calcDeltaY(double radius, double deltaRads) {
    return radius*Math.sin(deltaRads);
  }

  public double getXpos() {
    return xpos;
  }

  public double getYpos() {
    return ypos;
  }

  public double getTheta() {
    return theta;
  }
}

package org.team1540.localization2D;

public class LocalizationAccum2D {

  private double xpos;
  private double ypos;
  private double theta;

  private double distancePrev;
  private double angleRadsPrev;

  public LocalizationAccum2D() {
    reset();
  }

  public void reset() {
    xpos = 0;
    ypos = 0;
    theta = 0;

    distancePrev = 0;
    angleRadsPrev = 0;
  }

  /**
   * @param distance
   * Absolute distance of wheels
   * @param angleRads
   * Absolute angle in radians
   */
  public void update(double distance, double angleRads) {
    double deltaDistance = distance - distancePrev;
    double deltaRads = angleRads - angleRadsPrev;

    xpos += calcDeltaX(deltaDistance, deltaRads);
    ypos += calcDeltaY(deltaDistance, deltaRads);
  }

  private double calcRadius(double arcLength, double angleRads) {
    return arcLength/angleRads;
  }

  private double calcDeltaX(double deltaDistance, double deltaRads) {
    return calcRadius(deltaDistance, deltaRads)*(1-Math.cos(deltaRads));
  }

  private double calcDeltaY(double deltaDistance, double deltaRads) {
    return calcRadius(deltaDistance, deltaRads)*Math.sin(deltaRads);
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

package org.team1540.localization2D;

public class LocalizationAccum2D {

  private double xpos;
  private double ypos;

  private double distancePrevLeft;
  private double distancePrevRight;
  private double angleRadsPrev;

  public LocalizationAccum2D() {
    reset();
  }

  public void reset() {
    xpos = 0;
    ypos = 0;

    distancePrevLeft = 0;
    distancePrevRight = 0;
    angleRadsPrev = 0;
  }

  /**
   * @param distanceLeft Absolute distance of wheels
   * @param distanceRight Absolute distance of wheels
   * @param angleRads Continuous absolute angle in radians (should NOT jump from 2PI to 0)
   */
  public void update(double distanceLeft, double distanceRight, double angleRads) {
    double deltaDistanceLeft = distanceLeft - distancePrevLeft;
    double deltaDistanceRight = distanceRight - distancePrevRight;
    double deltaRads = angleRads - angleRadsPrev;

    distancePrevLeft = distanceLeft;
    distancePrevRight = distanceRight;
    angleRadsPrev = angleRads;

    // TODO: fall back on encoder pos
    double deltaForward = (deltaDistanceLeft+deltaDistanceRight)/2; // Linear default
    double deltaLeft = 0;

    if (deltaRads != 0) {
      //System.out.println("--_--__--");
      // Calculate radius of turn
      double avgRadius =
          (calcRadius(deltaDistanceLeft, deltaRads) + calcRadius(deltaDistanceRight, deltaRads))
              / 2;

      // Increment the accumulators
      deltaForward = calcDeltaY(avgRadius, deltaRads);
      deltaLeft = calcDeltaX(avgRadius, deltaRads);
    }

    xpos += deltaLeft * Math.cos(angleRads + Math.PI / 2) + deltaForward * Math.cos(angleRads);
    ypos += deltaLeft * Math.sin(angleRads + Math.PI / 2) + deltaForward * Math.sin(angleRads);
    // ypos += deltaForward;
    // ypos += (deltaDistanceLeft+deltaDistanceRight)/2;
  }

  // DOES NOT HANDLE DIV BY ZERO
  private double calcRadius(double arcLength, double angleRads) {
    return arcLength / angleRads;
  }

  private double calcDeltaX(double radius, double deltaRads) {
    return radius * (1.0 - Math.cos(deltaRads));
  }

  private double calcDeltaY(double radius, double deltaRads) {
    return radius * Math.sin(deltaRads);
  }

  public double getXpos() {
    return xpos;
  }

  public double getYpos() {
    return ypos;
  }
}

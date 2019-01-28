package org.team1540.localization2D.datastructures;

import org.team1540.localization2D.datastructures.threed.Transform3D;
import org.team1540.localization2D.datastructures.twod.Twist2D;

public class Odometry {
  public static final Odometry ZERO = new Odometry(Transform3D.IDENTITY, Twist2D.ZERO);

  private final Transform3D pose;
  private final Twist2D twist;

  public Odometry(Transform3D pose, Twist2D twist) {
    this.pose = pose;
    this.twist = twist;
  }

  public Transform3D getPose() {
    return pose;
  }

  public Twist2D getTwist() {
    return twist;
  }
}
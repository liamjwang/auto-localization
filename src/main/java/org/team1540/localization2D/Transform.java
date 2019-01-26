package org.team1540.localization2D;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Transform {
  public static final Transform ZERO = new Transform(Vector3D.ZERO, Rotation.IDENTITY);

  public Vector3D position;
  public Rotation orientation;

  public Transform(Vector3D position, Rotation orientation) {
    this.position = position;
    this.orientation = orientation;
  }
}

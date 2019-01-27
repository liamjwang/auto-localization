package org.team1540.localization2D.datastructures;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Transform3D {
  public static final Transform3D ZERO = new Transform3D(Vector3D.ZERO, Rotation.IDENTITY);

  public Vector3D position;
  public Rotation orientation;

  public Transform3D(Vector3D position, Rotation orientation) {
    this.position = position;
    this.orientation = orientation;
  }
}

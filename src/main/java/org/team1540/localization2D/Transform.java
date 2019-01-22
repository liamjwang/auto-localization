package org.team1540.localization2D;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Transform {

  public Vector3D position;
  public Rotation orientation;

  Transform(Vector3D position, Rotation orientation) {
    this.position = position;
    this.orientation = orientation;
  }
}

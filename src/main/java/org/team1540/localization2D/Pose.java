package org.team1540.localization2D;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Pose {

  public Vector3D position;
  public Vector3D orientation;

  Pose(Vector3D position, Vector3D orientation) {
    this.position = position;
    this.orientation = orientation;
  }
}

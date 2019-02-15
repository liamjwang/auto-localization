package org.team1540.localization2D.utils;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.team1540.localization2D.datastructures.threed.Transform3D;

public class LimelightInterface {
  private final NetworkTable limelightTable;

  public LimelightInterface(String name) {
    limelightTable = NetworkTableInstance.getDefault().getTable(name);
  }

  public Vector2D getRawPointOrNull(int id) {
    double upperLimit = 0.86;
    double lowerLimit = -0.65;
    double rightLimit = 0.90;
    double leftLimit = -0.90;

    Vector2D vector2D = new Vector2D(
        limelightTable.getEntry("tx" + id).getDouble(0),
        limelightTable.getEntry("ty" + id).getDouble(0)
    );
    if (vector2D.equals(Vector2D.ZERO)
        || !VisionUtils.isWithinBounds(vector2D, upperLimit, lowerLimit, rightLimit, leftLimit)) {
      return null;
    }
    return vector2D;
  }

  public Transform3D getVisionTargetToLimelightOrNull() {
    Double[] rawTransformation = limelightTable.getEntry("camtran").getDoubleArray(new Double[]{});
    if (rawTransformation[2] == 0) {
      return null;
    }
    // TODO: Something about this is probably wrong
    return new Transform3D(
        UnitsUtils.inchesToMeters(rawTransformation[2]),
        UnitsUtils.inchesToMeters(-rawTransformation[0]),
        UnitsUtils.inchesToMeters(-rawTransformation[1]),
        -Math.toRadians(rawTransformation[5]),
        Math.toRadians(rawTransformation[3]),
        Math.toRadians(rawTransformation[4]));
  }
}

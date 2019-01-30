package org.team1540.localization2D.utils;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class LimelightInterface {
  private final NetworkTable limelightTable;

  public LimelightInterface(String name) {
    limelightTable = NetworkTableInstance.getDefault().getTable(name);
  }

  public Vector2D getRawPoint(int id) {
    return new Vector2D(
        limelightTable.getEntry("tx" + id).getDouble(0),
        limelightTable.getEntry("ty" + id).getDouble(0)
    );
  }
}

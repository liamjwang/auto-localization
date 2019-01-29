package org.team1540.localization2D.utils;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class LimelightInterface {
  private final NetworkTable limetable;

  public LimelightInterface(String name) {
    limetable = NetworkTableInstance.getDefault().getTable(name);
  }

  public Vector2D getRawPoint(int id) {
    return new Vector2D(
        limetable.getEntry("tx" + id).getDouble(0),
        limetable.getEntry("ty" + id).getDouble(0)
    );
  }
}

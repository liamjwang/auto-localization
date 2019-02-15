package org.team1540.localization2D.utils;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class JonathanInterface {
  private final NetworkTable jonathanTable;

  public JonathanInterface(String name) {
    jonathanTable = NetworkTableInstance.getDefault().getTable(name);
  }

  public Vector2D getRawPoint(int id) {
    Double[] centers = jonathanTable.getEntry("centers").getDoubleArray(new Double[]{});
    if (centers.length != 4) {
      return Vector2D.ZERO;
    }
    return new Vector2D(
        centers[id*2],
        -centers[id*2+1]
    );
  }
}

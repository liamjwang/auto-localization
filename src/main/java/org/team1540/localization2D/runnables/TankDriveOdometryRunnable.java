package org.team1540.localization2D.runnables;

import java.util.function.DoubleSupplier;
import org.team1540.localization2D.datastructures.threed.Transform3D;
import org.team1540.localization2D.utils.TankDriveOdometryAccumulator;

/**
 * Runnable wrapper class for the TankDriveOdometryAccumulator
 */
public class TankDriveOdometryRunnable implements Runnable {

  private TankDriveOdometryAccumulator odometryAccumulator;

  private DoubleSupplier leftPosSupplier;
  private DoubleSupplier rightPosSupplier;
  private DoubleSupplier angleSupplier;

  private Transform3D odomToBaseLink;

  /**
   * @param leftPosSupplier Supplier for left tank drive position in meters
   * @param rightPosSupplier Supplier for right tank drive position in meters
   * @param angleSupplier Supplier for continuous angle measurement in radians // TODO: This should not require angles to be continuous
   */
  public TankDriveOdometryRunnable(
      DoubleSupplier leftPosSupplier,
      DoubleSupplier rightPosSupplier,
      DoubleSupplier angleSupplier) {

    this.leftPosSupplier = leftPosSupplier;
    this.rightPosSupplier = rightPosSupplier;
    this.angleSupplier = angleSupplier;
    reset();
  }

  @Override
  public void run() {
    odometryAccumulator.update(
        leftPosSupplier.getAsDouble(),
        rightPosSupplier.getAsDouble(),
        angleSupplier.getAsDouble());

    odomToBaseLink = odometryAccumulator.getTransform();
  }

  public Transform3D getOdomToBaseLink() {
    return odomToBaseLink;
  }

  public void reset() { // TODO: This should not be necessary when used with a better transformations/localization manager
    odometryAccumulator = new TankDriveOdometryAccumulator();
  }
}

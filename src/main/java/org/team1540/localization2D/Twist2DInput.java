package org.team1540.localization2D;

import java.util.OptionalDouble;
import org.team1540.rooster.drive.pipeline.DriveData;
import org.team1540.rooster.drive.pipeline.Input;
import org.team1540.rooster.drive.pipeline.TankDriveData;

public class Twist2DInput implements Input<TankDriveData> {
  private double cmdVelX = 0;
  private double cmdVelOmega = 0;

  public void setCmdVelX(double cmdVelX) {this.cmdVelX = cmdVelX;}
  public void setCmdVelTheta(double cmdVelOmega) {this.cmdVelOmega = cmdVelOmega;}

  @Override
  public TankDriveData get() {
    double leftSetpoint = (cmdVelX - cmdVelOmega * Tuning.drivetrainRadius);
    double rightSetpoint = (cmdVelX + cmdVelOmega * Tuning.drivetrainRadius);
    return new TankDriveData(new DriveData(OptionalDouble.of(leftSetpoint)), new DriveData(OptionalDouble.of(rightSetpoint)), OptionalDouble.empty(), OptionalDouble.empty());
  }
}

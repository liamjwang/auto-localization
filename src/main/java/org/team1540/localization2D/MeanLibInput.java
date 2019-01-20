package org.team1540.localization2D;

import edu.wpi.first.wpilibj.Timer;
import java.util.OptionalDouble;
import org.team1540.rooster.drive.pipeline.DriveData;
import org.team1540.rooster.drive.pipeline.TankDriveData;
import org.team1540.rooster.functional.Input;
import org.team2471.frc.lib.motion_profiling.Path2D;

public class MeanLibInput implements Input<TankDriveData> {


  private double lastDistanceL = 0;
  private double lastDistanceR = 0;
  private double lastTime = 0;
  private Path2D path2D;
  private Timer timer = new Timer();

  public MeanLibInput(Path2D path2D) {
    this.path2D = path2D;
  }

  @Override
  public TankDriveData get() {

    if (timer.get() <= 0) {
      timer.start();
    }

    double currentTime = timer.get();
    double currentDistanceL = path2D.getLeftDistance(currentTime);
    double currentDistanceR = path2D.getRightDistance(currentTime);

    TankDriveData data = new TankDriveData(
        new DriveData(
            OptionalDouble.of(currentDistanceL),
            OptionalDouble.of((currentDistanceL - lastDistanceL) / (lastTime - currentTime)),
            OptionalDouble.empty(),
            OptionalDouble.empty()
        ),
        new DriveData(
            OptionalDouble.of(currentDistanceR),
            OptionalDouble.of((currentDistanceR - lastDistanceR) / (lastTime - currentTime)),
            OptionalDouble.empty(),
            OptionalDouble.empty()
        ),
        OptionalDouble.of(Math.atan(
            path2D.getPosition(currentTime).getY() / path2D.getPosition(currentTime).getX())),
        OptionalDouble.empty()
    );

    lastTime = currentTime;
    lastDistanceL = currentDistanceL;
    lastDistanceR = currentDistanceR;

    return data;
  }

  public boolean isFinished() {
    return timer.get() >= path2D.getDuration();
  }
}

package org.team1540.robot2018.commands.wrist;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class MoveWrist extends Command {
  private long spikeDuration;
  private long lastExecTime;
  private double target;

  public MoveWrist(double target) {
    super("Move wrist to " + target);
    this.target = target;
    requires(Robot.wrist);
  }

  @Override
  protected void initialize() {
    spikeDuration = 0;
    lastExecTime = System.currentTimeMillis();
  }

  @Override
  protected void execute() {
    if (Robot.wrist.getCurrent() > Tuning.wristCurrentLimit) {
      spikeDuration += System.currentTimeMillis() - lastExecTime;
    } else {
      spikeDuration = 0;
    }
    lastExecTime = System.currentTimeMillis();

    if (spikeDuration > Tuning.wristPeakDuration) {
      target = Robot.wrist.getPosition();
    }
    Robot.wrist.setMotionMagicPosition(target);
  }

  @Override
  protected boolean isFinished() {
    return Robot.wrist.getError() < Tuning.wristTolerance
        && Math.abs(Robot.wrist.getTrajectoryPosition() - target) < Tuning.wristTolerance;
  }
}

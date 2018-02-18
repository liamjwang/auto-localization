package org.team1540.robot2018.commands.wrist;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class HoldWristPosition extends Command {
  double setpoint;
  private long lastExecTime;
  private long spikeDuration;

  public HoldWristPosition() {
    requires(Robot.wrist);
  }

  @Override
  protected void initialize() {
    setpoint = Robot.wrist.getPosition();
    spikeDuration = 0;
    lastExecTime = System.currentTimeMillis();
  }

  @Override
  protected void execute() {
    if (Math.abs(Robot.wrist.getPosition() - setpoint) > Tuning.MAX_WRIST_DEVIATION) {
      setpoint = Robot.wrist.getPosition();
    }

    if (Robot.wrist.getCurrent() > Tuning.wristCurrentLimit) {
      spikeDuration += System.currentTimeMillis() - lastExecTime;
    } else {
      spikeDuration = 0;
    }
    lastExecTime = System.currentTimeMillis();
    Robot.wrist.setMotionMagicPosition(setpoint);
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}

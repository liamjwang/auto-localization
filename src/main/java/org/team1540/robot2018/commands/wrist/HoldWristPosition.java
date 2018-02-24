package org.team1540.robot2018.commands.wrist;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class HoldWristPosition extends Command {
  private double setpoint;
  private Timer spikeTimer;
  private boolean timerIsRunning;

  public HoldWristPosition() {
    requires(Robot.wrist);
  }

  @Override
  protected void initialize() {
    setpoint = Robot.wrist.getPosition();
    spikeTimer.stop();
    spikeTimer.reset();
  }

  @Override
  protected void execute() {
    if (Robot.wrist.getCurrent() > Tuning.wristCurrentLimit) {
      if (spikeTimer.get() <= 0) {
        spikeTimer.start();
      }
    } else {
      spikeTimer.stop();
      spikeTimer.reset();
    }

    if (Math.abs(Robot.wrist.getPosition() - setpoint) > Tuning.maxWristDeviation
        || spikeTimer.hasPeriodPassed(Tuning.wristPeakDuration)) {
      setpoint = Robot.wrist.getPosition();
    }

    Robot.wrist.setMotionMagicPosition(setpoint);
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}

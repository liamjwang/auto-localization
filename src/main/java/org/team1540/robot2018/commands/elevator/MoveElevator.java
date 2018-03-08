package org.team1540.robot2018.commands.elevator;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

/**
 * Move elevator command that ignores the elevator ({@link MoveElevatorToPosition} makes sure that
 * the elevator is clear)
 */
public class MoveElevator extends Command {
  private double target;
  private Timer currentTimer = new Timer();

  public MoveElevator(double target) {
    this.target = target;
    requires(Robot.elevator);
  }

  @Override
  protected void initialize() {
    currentTimer.stop();
    currentTimer.reset();
  }

  @Override
  protected void execute() {
    if (Robot.elevator.getCurrent() > Tuning.elevatorCurrentThreshold) {
      if (currentTimer.get() <= 0) {
        currentTimer.start();
      }
      if (currentTimer.hasPeriodPassed(Tuning.elevatorSpikeTime)) {
        target = Robot.elevator.getPosition();
      }
    } else {
      currentTimer.stop();
      currentTimer.reset();
    }

    Robot.elevator.setMotionMagicPosition(target);
  }

  @Override
  protected boolean isFinished() {
    return Robot.elevator.getError() < Tuning.elevatorTolerance
        && Math.abs(Robot.elevator.getTrajPosition() - target) < Tuning.elevatorTolerance;
  }
}

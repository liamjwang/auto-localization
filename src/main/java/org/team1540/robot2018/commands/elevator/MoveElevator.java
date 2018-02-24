package org.team1540.robot2018.commands.elevator;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

/**
 * Move elevator command that ignores the elevator ({@link MoveElevatorToPosition} makes sure that
 * the elevator is clear)
 */
public class MoveElevator extends Command {
  private final double target;

  public MoveElevator(double target) {
    this.target = target;
    requires(Robot.elevator);
  }

  @Override
  protected void execute() {
    Robot.elevator.setMotionMagicPosition(target);
  }

  @Override
  protected boolean isFinished() {
    return Robot.elevator.getError() < Tuning.elevatorTolerance
        && Math.abs(Robot.elevator.getTrajPosition() - target) < Tuning.elevatorTolerance;
  }
}

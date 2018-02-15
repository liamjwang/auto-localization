package org.team1540.robot2018.commands.elevator;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.Robot;

public class MoveElevatorToPosition extends Command {
  private final double target;

  public MoveElevatorToPosition(double target) {
    this.target = target;
    requires(Robot.elevator);
  }

  @Override
  protected void execute() {
    Robot.elevator.setMotionMagicPosition(target);
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}

package org.team1540.robot2018.commands.elevator;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.Robot;

public class MoveElevatorToPosition extends Command {
  private final double target;
  private final double speed;
  private final double stopTolerance;

  private double currentPosition;

  public MoveElevatorToPosition(double target, double speed, double stopTolerance) {
    this.target = target;
    this.speed = speed;
    this.stopTolerance = stopTolerance;
    requires(Robot.elevator);
    currentPosition = Robot.elevator.getPosition();
  }

  @Override
  protected void initialize() {
    currentPosition = Robot.elevator.getPosition();
  }

  @Override
  protected void execute() {
    currentPosition += Math.copySign(speed, target - Robot.elevator.getPosition());
    Robot.elevator.setPosition(currentPosition);
  }

  @Override
  protected boolean isFinished() {
    return Math.abs(Robot.elevator.getPosition() - target) < stopTolerance;
  }
}

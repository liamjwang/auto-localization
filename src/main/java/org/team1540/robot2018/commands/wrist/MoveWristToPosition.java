package org.team1540.robot2018.commands.wrist;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class MoveWristToPosition extends Command {
  private final double target;
  private final double speed;
  private final double stopTolerance;

  private double currentPosition;

  public MoveWristToPosition(double target) {
    this(target, Tuning.wristMult, Tuning.wristTolerance);
  }

  public MoveWristToPosition(double target, double speed, double stopTolerance) {
    this.target = target;
    this.speed = speed;
    this.stopTolerance = stopTolerance;
    requires(Robot.wrist);
    currentPosition = Robot.wrist.getPosition();
  }

  @Override
  protected void initialize() {
    currentPosition = Robot.wrist.getPosition();
  }

  @Override
  protected void execute() {
    currentPosition += Math.copySign(speed, target - Robot.wrist.getPosition());
    Robot.wrist.setPosition(currentPosition);
  }

  @Override
  protected boolean isFinished() {
    return Math.abs(Robot.wrist.getPosition() - target) < stopTolerance;
  }
}

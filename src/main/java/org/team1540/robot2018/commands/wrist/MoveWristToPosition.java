package org.team1540.robot2018.commands.wrist;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.Robot;

public class MoveWristToPosition extends Command {
  private final double target;

  public MoveWristToPosition(double target) {
    super("Move wrist to " + target);
    this.target = target;
    requires(Robot.wrist);
  }

  @Override
  protected void execute() {
    Robot.wrist.setMotionMagicPosition(target);
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}

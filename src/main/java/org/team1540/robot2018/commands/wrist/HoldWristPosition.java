package org.team1540.robot2018.commands.wrist;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class HoldWristPosition extends Command {
  double setpoint;

  public HoldWristPosition() {
    requires(Robot.wrist);
  }

  @Override
  protected void initialize() {
    setpoint = Robot.wrist.getPosition();
  }

  @Override
  protected void execute() {
    if (Math.abs(Robot.wrist.getPosition() - setpoint) > Tuning.MAX_WRIST_DEVIATION) {
      setpoint = Robot.wrist.getPosition();
    }
    Robot.wrist.setPosition(setpoint);
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}

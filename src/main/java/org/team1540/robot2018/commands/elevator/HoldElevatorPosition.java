package org.team1540.robot2018.commands.elevator;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class HoldElevatorPosition extends Command {
  double setpoint;

  public HoldElevatorPosition() {
    requires(Robot.elevator);
  }

  @Override
  protected void initialize() {
    setpoint = Robot.elevator.getPosition();
  }

  @Override
  protected void execute() {
    if (Math.abs(Robot.elevator.getPosition() - setpoint) > Tuning.maxElevatorDeviation) {
      setpoint = Robot.elevator.getPosition();
    }
    Robot.elevator.setMotionMagicPosition(setpoint);
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}

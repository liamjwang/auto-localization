package org.team1540.robot2018.commands.elevator;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class CalibrateElevator extends Command {
  public CalibrateElevator() {
    requires(Robot.elevator);
  }

  @Override
  protected void initialize() {
    Robot.elevator.set(-1); // TODO is this too fast?
  }

  @Override
  protected void end() {
    Robot.elevator.set(0);
    Robot.elevator.resetEncoder();
  }

  @Override
  protected void interrupted() {
    // don't want to zero on interruption
    Robot.elevator.set(0);
  }

  @Override
  protected boolean isFinished() {
    return Robot.elevator.getCurrent() > Tuning.elevatorCurrentThreshold;
  }
}

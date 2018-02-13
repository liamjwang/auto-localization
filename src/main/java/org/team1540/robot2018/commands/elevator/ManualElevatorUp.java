package org.team1540.robot2018.commands.elevator;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class ManualElevatorUp extends Command {
  public ManualElevatorUp() {
    requires(Robot.elevator);
  }

  @Override
  protected void initialize() {
    Robot.elevator.set(Tuning.manualElevatorUpSpeed);
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

  @Override
  protected void end() {
    Robot.elevator.stop();
  }
}

package org.team1540.robot2018.commands.elevator;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.Robot;

public class ManualElevatorUp extends Command {
  public ManualElevatorUp() {
    requires(Robot.elevator);
  }

  @Override
  protected void initialize() {
    Robot.elevator.set(0.5);
  }

  @Override
  protected void execute() {
  }

  @Override
  protected boolean isFinished() {
    return false; //Return true to stop the command
  }

  @Override
  protected void end() {
  }

  @Override
  protected void interrupted() {
    Robot.elevator.stop();
  }
}
package org.team1540.robot2018.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.Robot;

public class ManualElevatorDown extends Command {
  public ManualElevatorDown() {
    requires(Robot.elevator);
  }

  @Override
  protected void initialize() {
    Robot.elevator.manualElevatorDown();
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
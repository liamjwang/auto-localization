package org.team1540.robot2018.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.OI;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class JoystickElevator extends Command {
  private double position = Robot.elevator.getPosition();

  public JoystickElevator() {
    requires(Robot.elevator);
  }

  @Override
  protected void initialize() {
    position = Robot.elevator.getPosition();
  }

  @Override
  protected void execute() {
    position -= Tuning.elevatorMult * OI.getCopilotLeftY();
    double actPos = Robot.elevator.setPosition(position);
    if (actPos != position) {
      position += (actPos > position ? 1 : -1) * Tuning.wristBounceBack;
    }
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
  }
}
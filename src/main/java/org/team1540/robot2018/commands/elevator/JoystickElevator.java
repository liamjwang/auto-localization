package org.team1540.robot2018.commands.elevator;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.OI;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class JoystickElevator extends Command {

  public JoystickElevator() {
    requires(Robot.elevator);
  }

  @Override
  protected void execute() {
    if (Robot.elevator.getVelocity() < 0) {
      Robot.elevator.set(Tuning.elevatorDownMult * -OI.getElevatorAxis());
    } else {
      Robot.elevator.set(-OI.getElevatorAxis());
    }
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}

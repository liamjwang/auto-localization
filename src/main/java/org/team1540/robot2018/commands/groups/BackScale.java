package org.team1540.robot2018.commands.groups;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.team1540.robot2018.Tuning;
import org.team1540.robot2018.commands.elevator.MoveElevatorToPosition;

public class BackScale extends CommandGroup {
  public BackScale() {
    addSequential(new MoveElevatorToPosition(Tuning.elevatorMaxPosition));
    // TODO: Also move wrist to 45 degrees back
  }
}

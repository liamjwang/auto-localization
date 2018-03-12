package org.team1540.robot2018.commands.groups;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.team1540.robot2018.Tuning;
import org.team1540.robot2018.commands.elevator.MoveElevatorSafe;

public class BackScale extends CommandGroup {
  public BackScale() {
    addSequential(new MoveElevatorSafe(true, Tuning.elevatorMaxPosition));
  }
}

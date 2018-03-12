package org.team1540.robot2018.commands.groups;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.team1540.robot2018.Tuning;
import org.team1540.robot2018.commands.elevator.MoveElevatorSafe;

public class Exchange extends CommandGroup {
  public Exchange() {
    addSequential(new MoveElevatorSafe(true, Tuning.elevatorExchangePosition));
  }
}

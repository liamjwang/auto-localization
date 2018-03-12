package org.team1540.robot2018.commands.groups;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.team1540.robot2018.Tuning;
import org.team1540.robot2018.commands.elevator.MoveElevator;

public class Exchange extends CommandGroup {
  public Exchange() {
    addSequential(new MoveElevator(Tuning.elevatorExchangePosition));
  }
}

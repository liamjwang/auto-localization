package org.team1540.robot2018.commands.groups;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.team1540.robot2018.Tuning;
import org.team1540.robot2018.commands.elevator.MoveElevator;

public class BackSwitch extends CommandGroup {
  public BackSwitch() {
    addSequential(new MoveElevator(true, Tuning.elevatorGroundPosition));
  }
}

package org.team1540.robot2018.commands.groups;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.team1540.robot2018.Tuning;
import org.team1540.robot2018.commands.elevator.MoveElevator;
import org.team1540.robot2018.commands.wrist.MoveWrist;

public class FrontScale extends CommandGroup {
  public FrontScale() {
    addSequential(new MoveWrist(Tuning.wrist45FwdPosition));
    addSequential(new MoveElevator(true, Tuning.elevatorMaxPosition));
  }
}

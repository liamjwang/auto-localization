package org.team1540.robot2018.commands.groups;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.team1540.robot2018.Tuning;
import org.team1540.robot2018.commands.elevator.MoveElevator;
import org.team1540.robot2018.commands.wrist.MoveWristToPosition;

public class GroundPosition extends CommandGroup {
  public GroundPosition() {
    addSequential(new MoveWristToPosition(Tuning.wristOutPosition));
    addSequential(new MoveElevator(Tuning.elevatorGroundPosition));
  }
}

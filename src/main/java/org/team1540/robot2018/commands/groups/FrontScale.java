package org.team1540.robot2018.commands.groups;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.team1540.robot2018.Tuning;
import org.team1540.robot2018.commands.elevator.MoveElevatorToPosition;
import org.team1540.robot2018.commands.wrist.MoveWristToPosition;

public class FrontScale extends CommandGroup {
  public FrontScale() {
    // TODO: Also move wrist to 45-degree angle
    addSequential(new MoveWristToPosition(Tuning.wrist45FwdPosition));
    addSequential(new MoveElevatorToPosition(Tuning.elevatorScalePosition));
  }
}

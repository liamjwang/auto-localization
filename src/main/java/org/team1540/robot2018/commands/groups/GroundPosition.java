package org.team1540.robot2018.commands.groups;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.team1540.robot2018.Tuning;
import org.team1540.robot2018.commands.elevator.CalibrateElevator;
import org.team1540.robot2018.commands.elevator.MoveElevatorToPosition;
import org.team1540.robot2018.commands.wrist.CalibrateWrist;

public class GroundPosition extends CommandGroup {
  public GroundPosition() {
    // TODO: Also move wrist all the way out
    addSequential(new CalibrateWrist());
    addSequential(new MoveElevatorToPosition(Tuning.elevatorGroundPosition));
    addSequential(new CalibrateElevator());
  }
}

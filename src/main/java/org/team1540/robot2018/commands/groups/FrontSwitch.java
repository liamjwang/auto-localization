package org.team1540.robot2018.commands.groups;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.team1540.robot2018.Tuning;
import org.team1540.robot2018.commands.elevator.MoveElevatorToPosition;
import org.team1540.robot2018.commands.wrist.MoveWristToPosition;

public class FrontSwitch extends CommandGroup {
  public FrontSwitch() {
    addSequential(new MoveWristToPosition(Tuning.wristOutPosition));
    addSequential(new MoveElevatorToPosition(Tuning.elevatorFrontSwitchPosition));
  }
}

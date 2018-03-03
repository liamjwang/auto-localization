package org.team1540.robot2018.commands.groups;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.team1540.robot2018.Tuning;
import org.team1540.robot2018.commands.elevator.HoldElevatorPosition;
import org.team1540.robot2018.commands.elevator.MoveElevatorToPosition;
import org.team1540.robot2018.commands.wrist.HoldWristPosition;

public class HoldElevatorWrist extends CommandGroup {
  public HoldElevatorWrist() {
    addParallel(new HoldElevatorPosition());
    addParallel(new HoldWristPosition());
  }
}

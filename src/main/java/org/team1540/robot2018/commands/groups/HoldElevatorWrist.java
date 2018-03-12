package org.team1540.robot2018.commands.groups;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.team1540.robot2018.commands.elevator.HoldElevator;
import org.team1540.robot2018.commands.wrist.HoldWrist;

public class HoldElevatorWrist extends CommandGroup {
  public HoldElevatorWrist() {
    addParallel(new HoldElevator());
    addParallel(new HoldWrist());
  }
}

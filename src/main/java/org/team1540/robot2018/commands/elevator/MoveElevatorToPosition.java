package org.team1540.robot2018.commands.elevator;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class MoveElevatorToPosition extends CommandGroup {

  public MoveElevatorToPosition(double target) {
    // TODO: Add logic to prevent wrist colliding with supports
    addSequential(new MoveElevator(target));
  }
}

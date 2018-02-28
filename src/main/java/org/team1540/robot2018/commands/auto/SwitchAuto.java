package org.team1540.robot2018.commands.auto;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.team1540.robot2018.Tuning;
import org.team1540.robot2018.commands.elevator.MoveElevatorToPosition;
import org.team1540.robot2018.commands.intake.EjectCube;

public class SwitchAuto extends CommandGroup {
  public SwitchAuto() {
    addSequential(new MoveElevatorToPosition(Tuning.elevatorFrontSwitchPosition));
    addSequential(new AutonomousProfiling());
    addSequential(new EjectCube());
  }
}
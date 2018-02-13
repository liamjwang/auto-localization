package org.team1540.robot2018.commands.groups;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.team1540.robot2018.Tuning;
import org.team1540.robot2018.commands.elevator.MoveElevatorToPosition;
import org.team1540.robot2018.commands.intake.AutoIntake;
import org.team1540.robot2018.commands.wrist.MoveWristToPosition;

/**
 * Lower wrist > lower intake > intake a cube > move wrist up
 */
public class IntakeSequence extends CommandGroup {
  public IntakeSequence() {
    addSequential(new MoveWristToPosition(Tuning.wristOutPosition));
    addSequential(new MoveElevatorToPosition(Tuning.elevatorGroundPosition));
    addSequential(new AutoIntake());
    addSequential(new MoveWristToPosition(Tuning.wristTransitPosition));
  }
}

package org.team1540.robot2018.commands.groups;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;
import org.team1540.robot2018.commands.elevator.MoveElevatorToPosition;
import org.team1540.robot2018.commands.wrist.MoveWristToPosition;

public class BackScale extends CommandGroup {
  public BackScale() {
    addSequential(new ConditionalCommand(new MoveWristToPosition(Tuning.wristTransitPosition)) {
      @Override
      protected boolean condition() {
        return Robot.wrist.getPosition() < Tuning.wristTransitPosition;
      }
    });
    addSequential(new MoveElevatorToPosition(Tuning.elevatorScalePosition));
    addSequential(new MoveWristToPosition(Tuning.wristOutPosition));
  }
}

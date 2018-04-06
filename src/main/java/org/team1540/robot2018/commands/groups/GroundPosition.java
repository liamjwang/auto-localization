package org.team1540.robot2018.commands.groups;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.InstantCommand;
import org.team1540.base.util.SimpleConditionalCommand;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;
import org.team1540.robot2018.commands.elevator.CalibrateElevator;
import org.team1540.robot2018.commands.elevator.MoveElevator;
import org.team1540.robot2018.commands.wrist.CalibrateWristMotionMagic;

public class GroundPosition extends CommandGroup {
  public GroundPosition() {
    addSequential(new CalibrateWristMotionMagic());
    addSequential(new SimpleConditionalCommand(
        () -> Robot.elevator.getPosition() > Tuning.elevatorGroundPosition,
        new MoveElevator(true, Tuning.elevatorGroundPosition)));
    addSequential(new CalibrateElevator());
    addSequential(new InstantCommand());
  }
}

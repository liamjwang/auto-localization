package org.team1540.robot2018.commands.auto;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.team1540.robot2018.Tuning;
import org.team1540.robot2018.commands.intake.EjectCube;

public class StraightAuto extends CommandGroup {
  public StraightAuto() {
    addSequential(new DriveBackward(Tuning.driveForwardTime));
    addSequential(new EjectCube());
  }
}

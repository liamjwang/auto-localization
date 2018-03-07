package org.team1540.robot2018.commands.auto.sequences;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.team1540.robot2018.Tuning;
import org.team1540.robot2018.commands.auto.DriveBackward;
import org.team1540.robot2018.commands.wrist.CalibrateWrist;

public class StupidAuto extends CommandGroup {
  public StupidAuto() {
    addSequential(new DriveBackward(Tuning.stupidDriveTime));
    addParallel(new CalibrateWrist());
  }
}

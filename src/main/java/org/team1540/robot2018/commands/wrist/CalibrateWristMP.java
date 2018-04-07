package org.team1540.robot2018.commands.wrist;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.team1540.robot2018.Tuning;

public class CalibrateWristMP extends CommandGroup {
  public CalibrateWristMP(boolean out) {
    addSequential(new MoveWrist(Tuning.wristOutPosition));
    addSequential(new CalibrateWrist(out));
  }
}

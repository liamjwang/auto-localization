package org.team1540.robot2018.commands.wrist;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.team1540.robot2018.Tuning;

public class CalibrateWristMotionMagic extends CommandGroup {
  public CalibrateWristMotionMagic() {
    addSequential(new MoveWrist(Tuning.wristOutPosition));
    addSequential(new CalibrateWrist());
  }
}

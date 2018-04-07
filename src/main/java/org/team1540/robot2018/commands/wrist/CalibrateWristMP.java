package org.team1540.robot2018.commands.wrist;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.team1540.robot2018.Tuning;
import org.team1540.robot2018.commands.wrist.CalibrateWrist.CalibratePosition;

public class CalibrateWristMP extends CommandGroup {
  public CalibrateWristMP(CalibratePosition out) {
    addSequential(new MoveWrist(Tuning.wristOutPosition));
    addSequential(new CalibrateWrist(out));
  }
}

package org.team1540.robot2018.commands.auto.sequences;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.team1540.robot2018.Tuning;
import org.team1540.robot2018.commands.auto.RunProfile;
import org.team1540.robot2018.commands.intake.EjectAuto;
import org.team1540.robot2018.commands.wrist.CalibrateWrist;
import org.team1540.robot2018.commands.wrist.MoveWristToPosition;

public class SingleCubeAuto extends CommandGroup {
  public SingleCubeAuto(String name) {
    addParallel(new MoveWristToPosition(Tuning.wrist45BackPosition));
    addSequential(new RunProfile(name, false));
    addSequential(new EjectAuto());
    addSequential(new CalibrateWrist());
  }
}

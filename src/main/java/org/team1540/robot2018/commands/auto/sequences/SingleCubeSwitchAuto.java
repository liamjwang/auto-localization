package org.team1540.robot2018.commands.auto.sequences;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.team1540.robot2018.Tuning;
import org.team1540.robot2018.commands.auto.RunProfile;
import org.team1540.robot2018.commands.intake.Eject;
import org.team1540.robot2018.commands.wrist.CalibrateWrist;
import org.team1540.robot2018.commands.wrist.MoveWrist;

public class SingleCubeSwitchAuto extends CommandGroup {
  public SingleCubeSwitchAuto(String profileName) {
    addParallel(new MoveWrist(Tuning.wrist45BackPosition));
    addSequential(new RunProfile(profileName));
    addSequential(new Eject(1));
    addSequential(new CalibrateWrist());
  }
}

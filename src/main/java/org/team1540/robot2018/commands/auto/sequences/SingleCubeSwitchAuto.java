package org.team1540.robot2018.commands.auto.sequences;

import static org.team1540.robot2018.commands.wrist.CalibrateWrist.CalibratePosition.OUT;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.team1540.robot2018.Tuning;
import org.team1540.robot2018.commands.intake.Eject;
import org.team1540.robot2018.commands.wrist.CalibrateWristMP;
import org.team1540.robot2018.commands.wrist.MoveWrist;
import org.team1540.robot2018.motion.FollowProfile;

public class SingleCubeSwitchAuto extends CommandGroup {
  public SingleCubeSwitchAuto(String profileName) {
    addParallel(new MoveWrist(Tuning.wrist45BackPosition));
    addSequential(new FollowProfile(profileName));
    addSequential(new Eject(1));
    addSequential(new CalibrateWristMP(OUT));
  }
}

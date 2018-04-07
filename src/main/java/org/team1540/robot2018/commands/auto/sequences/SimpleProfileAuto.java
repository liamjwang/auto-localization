package org.team1540.robot2018.commands.auto.sequences;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.team1540.robot2018.commands.wrist.CalibrateWristMP;
import org.team1540.robot2018.motion.FollowProfile;

public class SimpleProfileAuto extends CommandGroup {
  public SimpleProfileAuto(String name) {
    addSequential(new FollowProfile(name));
    addSequential(new CalibrateWristMP(true));
  }
}

package org.team1540.robot2018.commands.auto.sequences;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.team1540.robot2018.commands.auto.RunProfile;
import org.team1540.robot2018.commands.wrist.CalibrateWrist;

public class SimpleProfileAuto extends CommandGroup {
  public SimpleProfileAuto(String name) {
    addSequential(new RunProfile(name));
    addSequential(new CalibrateWrist());
  }
}

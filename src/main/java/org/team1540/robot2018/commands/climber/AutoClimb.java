package org.team1540.robot2018.commands.climber;

import edu.wpi.first.wpilibj.command.CommandGroup;


public class AutoClimb extends CommandGroup {
  public AutoClimb() {
    addSequential(new RunClimber(2, 0.5));
  }
}
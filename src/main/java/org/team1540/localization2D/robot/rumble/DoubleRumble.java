package org.team1540.localization2D.robot.rumble;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.TimedCommand;

public class DoubleRumble extends CommandGroup {
  public DoubleRumble(Joystick joystick) {
    addSequential(new RumbleForTime(joystick, 1, 0.2));
    addSequential(new TimedCommand(0.2));
    addSequential(new RumbleForTime(joystick, 1, 0.2));
  }
}

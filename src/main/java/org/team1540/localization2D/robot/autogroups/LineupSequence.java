package org.team1540.localization2D.robot.autogroups;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.TimedCommand;
import org.team1540.localization2D.robot.commands.drivetrain.LimelightPoint;
import org.team1540.localization2D.vision.commands.UDPAutoLineup;

public class LineupSequence extends CommandGroup {
  public LineupSequence() {
    addSequential(new UDPAutoLineup());
    addSequential(new TimedCommand(0.05));
    addSequential(new LimelightPoint());
  }
}

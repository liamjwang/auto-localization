package org.team1540.localization2D.robot.autogroups;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.TimedCommand;
import org.team1540.localization2D.datastructures.twod.Transform2D;
import org.team1540.localization2D.vision.commands.UDPVelocityTwistDrive;

public class TestSequence extends CommandGroup {
  public TestSequence() {
    addSequential(new UDPVelocityTwistDrive(new Transform2D(1, 0, 0), false));
        addSequential(new TimedCommand(1));
    addSequential(new UDPVelocityTwistDrive(Transform2D.ZERO, true));
    }
}

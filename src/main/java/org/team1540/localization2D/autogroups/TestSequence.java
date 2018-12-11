package org.team1540.localization2D.autogroups;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.team1540.localization2D.commands.drivetrain.UDPVelocityTwistDrive;

public class TestSequence extends CommandGroup {
    public TestSequence() {
        addSequential(new UDPVelocityTwistDrive(1, 0, 0, false));
        addSequential(new UDPVelocityTwistDrive(2, 0, 0, false));
    }
}

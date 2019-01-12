package org.team1540.localization2D.autogroups;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.TimedCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.localization2D.commands.drivetrain.UDPVelocityTwistDrive;

public class TestSequence extends CommandGroup {
    public TestSequence() {
        // addSequential(new PointToTarget());
        // addSequential(new UDPVelocityTwistDrive(
        //         SmartDashboard.getNumber("vision_position_x", 0),
        //         SmartDashboard.getNumber("vision_position_y", 0),
        //         SmartDashboard.getNumber("vision_orientation_z", 0), false));
        addSequential(new UDPVelocityTwistDrive(
                SmartDashboard.getNumber("limelight-pose/position/x", 0),
                SmartDashboard.getNumber("limelight-pose/position/y", 0),
                SmartDashboard.getNumber("limelight-pose/orientation/z", 0), false));
//        addSequential(new UDPVelocityTwistDrive(2, -0.5, 0.8, false));
        addSequential(new TimedCommand(1));
//        addSequential(new UDPVelocityTwistDrive(0.3, 0, 0, true));
//        addSequential(new UDPVelocityTwistDrive(2, 0.5, Math.PI/2, true));
//        addSequential(new UDPVelocityTwistDrive(0.3, 0, 0, true));
//        addSequential(new UDPVelocityTwistDrive(2, 0.5, -Math.PI/2, true));
//        addSequential(new UDPVelocityTwistDrive(0.3, 0, 0, true));
        addSequential(new UDPVelocityTwistDrive(0, 0, 0, true));
    }
}

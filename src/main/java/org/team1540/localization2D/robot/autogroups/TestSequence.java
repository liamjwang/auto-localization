package org.team1540.localization2D.robot.autogroups;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.TimedCommand;
import org.team1540.localization2D.robot.commands.drivetrain.UDPVelocityTwistDrive;

public class TestSequence extends CommandGroup {
    public TestSequence(double pos_x, double pos_y, double ori_z) {
        // addSequential(new PointToTarget());
        // addSequential(new UDPVelocityTwistDrive(
        //         SmartDashboard.getNumber("vision_position_x", 0),
        //         SmartDashboard.getNumber("vision_position_y", 0),
        //         SmartDashboard.getNumber("vision_orientation_z", 0), false));
        addSequential(new UDPVelocityTwistDrive(
                pos_x, pos_y, ori_z, false));
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
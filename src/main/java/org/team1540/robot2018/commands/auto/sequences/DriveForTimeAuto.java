package org.team1540.robot2018.commands.auto.sequences;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.command.CommandGroup;
import org.team1540.robot2018.commands.auto.DriveTimed;
import org.team1540.robot2018.commands.wrist.CalibrateWristMP;

public class DriveForTimeAuto extends CommandGroup {
  public DriveForTimeAuto(ControlMode mode, double time, double value) {
    addSequential(new DriveTimed(mode, time, value));
    addSequential(new CalibrateWristMP(true));
  }
}

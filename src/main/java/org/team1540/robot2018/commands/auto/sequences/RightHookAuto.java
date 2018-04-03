package org.team1540.robot2018.commands.auto.sequences;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.command.CommandGroup;
import org.team1540.robot2018.Tuning;
import org.team1540.robot2018.commands.auto.DriveTimed;
import org.team1540.robot2018.commands.intake.Eject;
import org.team1540.robot2018.commands.wrist.CalibrateWrist;
import org.team1540.robot2018.commands.wrist.MoveWrist;
import org.team1540.robot2018.motion.FollowProfile;

public class RightHookAuto extends CommandGroup {
  public RightHookAuto() {
    addSequential(new FollowProfile("right_hook_approach"));
    addParallel(new MoveWrist(Tuning.wrist45BackPosition));
    addSequential(new DriveTimed(ControlMode.PercentOutput, 1.6, -0.6, 0.1));
    addSequential(new Eject(1));
    addSequential(new CalibrateWrist());
  }
}

package org.team1540.robot2018.commands.auto.sequences;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.TimedCommand;
import org.team1540.robot2018.Tuning;
import org.team1540.robot2018.commands.auto.DriveTimed;
import org.team1540.robot2018.commands.elevator.MoveElevatorSafe;
import org.team1540.robot2018.commands.groups.GroundPosition;
import org.team1540.robot2018.commands.intake.Eject;
import org.team1540.robot2018.commands.wrist.MoveWrist;
import org.team1540.robot2018.motion.FollowProfile;

public class RightScaleAuto extends CommandGroup {
  public RightScaleAuto() {
    // TODO: Test whether we can run some of the auto with the lift halfway up to reduce time
    // this delays a little bit before moving the wrist so we don't hit the wall
    addParallel(new CommandGroup() {
      {
        addSequential(new TimedCommand(2));
        addSequential(new MoveWrist(Tuning.wristTransitPosition));
      }
    });
    addSequential(new FollowProfile("right_scale_approach"));
    addSequential(new DriveTimed(ControlMode.Velocity, 0.8, -450, 150));
    addSequential(new MoveElevatorSafe(false, Tuning.elevatorMaxPosition));
    addSequential(new MoveWrist(Tuning.wristBackPosition));
    addSequential(new Eject(0.6));
    addSequential(new GroundPosition());
  }
}

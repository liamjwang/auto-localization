package org.team1540.robot2018.commands.groups;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.team1540.robot2018.commands.intake.AutoIntake;

public class IntakeSequence extends CommandGroup {
  public IntakeSequence() {
    // addSequential(new GroundPosition());
    addSequential(new AutoIntake());
    // addSequential(new MoveWrist(Tuning.wrist45FwdPosition));
  }
}

package org.team1540.robot2018.commands.groups;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.team1540.robot2018.commands.intake.AutoIntake;

/**
 * Lower wrist > lower intake > intake a cube > move wrist up
 */
public class IntakeSequence extends CommandGroup {
  public IntakeSequence() {
    // addSequential(new GroundPosition());
    addSequential(new AutoIntake());
    // addSequential(new MoveWristToPosition(Tuning.wrist45FwdPosition));
  }
}

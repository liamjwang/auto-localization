package org.team1540.localization2D.commands.drivetrain;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.localization2D.OI;
import org.team1540.localization2D.Robot;

public class PercentDrive extends Command {
  public PercentDrive() {
    requires(Robot.drivetrain);
    Robot.drivetrain.reset();
  }

  @Override
  protected void execute() {
    double triggerValue = OI.getTankdriveForwardsAxis() - OI.getTankdriveBackwardsAxis();
    Robot.drivetrain.setLeftPercent(triggerValue - OI.getTankdriveLeftAxis());
    Robot.drivetrain.setRightPercent(triggerValue - OI.getTankdriveRightAxis());
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}

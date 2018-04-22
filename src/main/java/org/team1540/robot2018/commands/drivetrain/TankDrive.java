package org.team1540.robot2018.commands.drivetrain;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.OI;
import org.team1540.robot2018.Robot;

public class TankDrive extends Command {
  public TankDrive() {
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

package org.team1540.robot2018.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.OI;
import org.team1540.robot2018.Robot;

public class TankDrive extends Command {
  public TankDrive() {
    requires(Robot.drivetrain);
  }

  @Override
  protected void execute() {
    double triggerValue = OI.getTankdriveForwardsAxis() - OI.getTankdriveBackwardsAxis();
    Robot.drivetrain.setLeftPercent(-OI.getTankdriveLeftAxis() + triggerValue);
    Robot.drivetrain.setRightPercent(-OI.getTankdriveRightAxis() + triggerValue);
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}

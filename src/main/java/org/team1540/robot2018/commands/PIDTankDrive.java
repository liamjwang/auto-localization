package org.team1540.robot2018.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.OI;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class PIDTankDrive extends Command {
  public PIDTankDrive() {
    requires(Robot.drivetrain);
  }

  @Override
  protected void execute() {
    double triggerValue = OI.getTankdriveForwardsAxis() - OI.getTankdriveBackwardsAxis();
    Robot.drivetrain.setLeftVelocity((-OI.getTankdriveLeftAxis() + triggerValue)* Tuning.drivetrainVelocity);
    Robot.drivetrain.setRightVelocity((-OI.getTankdriveRightAxis() + triggerValue)*Tuning.drivetrainVelocity);
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}

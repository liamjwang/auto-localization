package org.team1540.localization2D.commands.drivetrain;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.localization2D.OI;
import org.team1540.localization2D.Robot;
import org.team1540.localization2D.Tuning;

public class VelocityDrive extends Command {
  public VelocityDrive() {
    requires(Robot.drivetrain);
    Robot.drivetrain.reset();
    Robot.drivetrain.configTalonsForVelocity();
  }

  @Override
  protected void execute() {
    double triggerValue = OI.getTankdriveForwardsAxis() - OI.getTankdriveBackwardsAxis();
    double leftSetpoint = (triggerValue - OI.getTankdriveLeftAxis()) * Tuning.drivetrainMaxVelocity;
    double rightSetpoint = (triggerValue - OI.getTankdriveRightAxis()) * Tuning.drivetrainMaxVelocity;
    Robot.drivetrain.setLeftVelocity(leftSetpoint);
    Robot.drivetrain.setRightVelocity(rightSetpoint);
    SmartDashboard.putNumber("leftVelSetpoint", leftSetpoint);
    SmartDashboard.putNumber("rightVelSetpoint", rightSetpoint);
    SmartDashboard.putNumber("leftVel", Robot.drivetrain.getLeftVelocity());
    SmartDashboard.putNumber("rightVel", Robot.drivetrain.getRightVelocity());
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}

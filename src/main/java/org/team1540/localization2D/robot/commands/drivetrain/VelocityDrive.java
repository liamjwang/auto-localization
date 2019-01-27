package org.team1540.localization2D.robot.commands.drivetrain;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.localization2D.robot.OI;
import org.team1540.localization2D.robot.Robot;
import org.team1540.localization2D.robot.Tuning;

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
    SmartDashboard.putNumber("debug-setpoint-left", leftSetpoint);
    SmartDashboard.putNumber("debug-setpoint-right", rightSetpoint);
    SmartDashboard.putNumber("debug-velocity-left", Robot.drivetrain.getLeftVelocity());
    SmartDashboard.putNumber("debug-velocity-right", Robot.drivetrain.getRightVelocity());
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}

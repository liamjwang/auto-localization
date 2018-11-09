package org.team1540.localization2D.commands.drivetrain;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.localization2D.OI;
import org.team1540.localization2D.Robot;
import org.team1540.localization2D.Tuning;

public class NetTablesVelocityTwistDrive extends Command {
  public NetTablesVelocityTwistDrive() {
    requires(Robot.drivetrain);
    Robot.drivetrain.reset();
    Robot.drivetrain.configTalonsForVelocity();
  }

  @Override
  protected void execute() {
    double cmdVelX = SmartDashboard.getNumber("cmd_vel-linear-x", 0);
    double cmdVelOmega = SmartDashboard.getNumber("cmd_vel-angular-z", 0);
    double leftSetpoint = (cmdVelX-cmdVelOmega*Tuning.drivetrainRadius)*Tuning.drivetrainTicksPerMeter/10;
    double rightSetpoint = (cmdVelX+cmdVelOmega*Tuning.drivetrainRadius)*Tuning.drivetrainTicksPerMeter/10;
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

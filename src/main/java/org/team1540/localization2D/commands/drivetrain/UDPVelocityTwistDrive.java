package org.team1540.localization2D.commands.drivetrain;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.localization2D.Robot;
import org.team1540.localization2D.Tuning;

public class UDPVelocityTwistDrive extends Command {
  public UDPVelocityTwistDrive(int xGoal, int yGoal, int angleGoal, boolean freeGoalVel) {
      SmartDashboard.putNumber("goal_position_x", xGoal);
      SmartDashboard.putNumber("goal_position_y", yGoal);
      SmartDashboard.putNumber("goal_orientation_z", angleGoal);
      SmartDashboard.putBoolean("free_goal_vel", freeGoalVel);
    requires(Robot.drivetrain);
    Robot.drivetrain.reset();
    Robot.drivetrain.configTalonsForVelocity();
  }
    public UDPVelocityTwistDrive() {
        requires(Robot.drivetrain);
        Robot.drivetrain.reset();
        Robot.drivetrain.configTalonsForVelocity();
    }

  @Override
  protected void execute() {
    double cmdVelX = 0;
    double cmdVelOmega = 0;
    if (Robot.serv != null) {
      cmdVelX = Robot.serv.getCmdVelX();
      cmdVelOmega = Robot.serv.getCmdVelTheta();
    }
//      System.out.println("Cmd Vel " + cmdVelX + " Omega " + cmdVelOmega);
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
        double xError = SmartDashboard.getNumber("goal_position_x", 0) - Robot.getPosX();
        double yError = SmartDashboard.getNumber("goal_position_y", 0) - Robot.getPosY();
        double angleError = AngleDifference(SmartDashboard.getNumber("goal_orientation_z", 0)*180/2*Math.PI, Robot.navx.getYaw());
        System.out.println(
                "xError "+xError+
                        " yError "+yError+
                        " angleError "+angleError);
        return Math.abs(xError) < 0.04 &&
                Math.abs(yError) < 0.04 &&
                Math.abs(angleError) < 1;
    }

    public static double AngleDifference( double angle1, double angle2 )
    {
        double diff = ( angle2 - angle1 + 180 ) % 360 - 180;
        return diff < -180 ? diff + 360 : diff;
    }
}

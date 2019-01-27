package org.team1540.localization2D.robot.commands.drivetrain;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.localization2D.robot.Robot;
import org.team1540.localization2D.robot.Tuning;

public class UDPVelocityTwistDrive extends Command {
    double xGoal = 0;
    double yGoal = 0;
    double angleGoal = 0;
    boolean freeGoalVel = false;

    boolean checkEnd = false;

  public UDPVelocityTwistDrive(double xGoal, double yGoal, double angleGoal, boolean freeGoalVel) {
      this.xGoal = xGoal;
      this.yGoal = yGoal;
      this.angleGoal = angleGoal;
      this.freeGoalVel = freeGoalVel;

      this.checkEnd = true;
      requires(Robot.drivetrain);
  }
    public UDPVelocityTwistDrive() {
      this.checkEnd = false;
        requires(Robot.drivetrain);
    }

    @Override
    protected void initialize() {
    if (checkEnd) {
      System.out.println("Set goal to " + xGoal);
      SmartDashboard.putNumber("--------goal_position_x", xGoal);
      SmartDashboard.putNumber("--------goal_position_y", yGoal);
      SmartDashboard.putNumber("--------goal_orientation_z", angleGoal);
      SmartDashboard.putBoolean("--------free_goal_vel", freeGoalVel);
      Robot.serv.setGoal(xGoal, yGoal, angleGoal);
    }
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
      if (!this.checkEnd) {
          return false;
      }
        double xError = xGoal - Robot.getPosX();
        double yError = yGoal - Robot.getPosY();
        double angleError = angleGoal*180/Math.PI - (-Robot.navx.getYaw());
        while (angleError > 180) {
            angleError -= 360;
        }
        while (angleError < -180) {
            angleError += 360;
        }
        System.out.println(
                "xError "+xError+
                        " yError "+yError+
                        " angleError "+angleError);
        boolean finished = Math.abs(xError) < 0.04 &&
                Math.abs(yError) < 0.04 &&
                Math.abs(angleError) < 1;
        if (finished) {
            System.out.println("Close to goal: " + xGoal + " " + yGoal);
            Robot.drivetrain.setLeftVelocity(0);
            Robot.drivetrain.setRightVelocity(0);
        }
        return finished;
    }

    public static double AngleDifference( double angle1, double angle2 )
    {
        double diff = ( angle2 - angle1 + 180 ) % 360 - 180;
        return diff < -180 ? diff + 360 : diff;
    }
}

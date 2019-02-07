package org.team1540.localization2D.robot.commands.drivetrain;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.PIDCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.checkerframework.checker.units.qual.A;
import org.team1540.localization2D.datastructures.twod.Transform2D;
import org.team1540.localization2D.robot.Robot;

public class LimelightPoint extends PIDCommand {

  public LimelightPoint() {
    super(0.01, 0.0001, 0.04);
    // super(SmartDashboard.getNumber("lp_p", 0), SmartDashboard.getNumber("lp_i", 0), SmartDashboard.getNumber("lp_d", 0));// The constructor passes a name for the subsystem and the P, I and D constants that are used when computing the motor output
    // getPIDController().setContinuous(true);
    requires(Robot.drivetrain);
  }

  // public LimelightPoint() {
  //   requires(Robot.drivetrain);
  // }

  @Override
  protected void initialize() {
    Robot.drivetrain.reset();
    Robot.drivetrain.configTalonsForVelocity();
  }


  @Override
  protected void execute() {

  }

  @Override
  protected boolean isFinished() {
    double error = Math.abs(NetworkTableInstance.getDefault().getTable("limelight-a").getEntry("tx").getDouble(99));
    if (error < 0.4) {
      Robot.drivetrain.stop();
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected double returnPIDInput() {
    return NetworkTableInstance.getDefault().getTable("limelight-a").getEntry("tx").getDouble(0);
  }

  @Override
  protected void usePIDOutput(double output) {
    output *= 400;
    System.out.println(output);
    Robot.drivetrain.setRightVelocity(output);
    Robot.drivetrain.setLeftVelocity(-output);
  }
}

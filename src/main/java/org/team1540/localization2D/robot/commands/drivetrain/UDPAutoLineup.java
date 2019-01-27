package org.team1540.localization2D.robot.commands.drivetrain;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.team1540.localization2D.datastructures.twod.Twist2D;
import org.team1540.localization2D.robot.Robot;
import org.team1540.localization2D.robot.TankDriveTwist2DInput;
import org.team1540.localization2D.robot.Tuning;
import org.team1540.rooster.drive.pipeline.CTREOutput;
import org.team1540.rooster.drive.pipeline.FeedForwardProcessor;
import org.team1540.rooster.drive.pipeline.UnitScaler;
import org.team1540.rooster.util.Executable;
import org.team1540.rooster.wrappers.RevBlinken.ColorPattern;

public class UDPAutoLineup extends Command {
  double xGoal = 0;
  double yGoal = 0;
  double angleGoal = 0;
  private Executable pipeline;
  private TankDriveTwist2DInput twist2DInput;

  long finishedTime = 0;
  // boolean freeGoalVel;
  //
  // boolean checkEnd;

  public UDPAutoLineup() {
    requires(Robot.drivetrain);
    twist2DInput = new TankDriveTwist2DInput(Tuning.drivetrainRadius);
    pipeline = twist2DInput
        .then(new FeedForwardProcessor(0.27667, 0.054083,0.08694))
        .then(new UnitScaler(Tuning.drivetrainTicksPerMeter, 10))
        .then(new CTREOutput(Robot.drivetrain.driveLeftMotorA, Robot.drivetrain.driveRightMotorA, true));

  }

  @Override
  protected void initialize() {
    Robot.drivetrain.reset();
    Robot.drivetrain.configTalonsForVelocity();
    lastUpdate = System.currentTimeMillis();
    Robot.leds.set(ColorPattern.CHASE_BLUE);
    // SmartDashboard.putBoolean("teb-reset", true);
    NetworkTable tebConfigTable = NetworkTableInstance.getDefault().getTable("TEBPlanner/Config");
    tebConfigTable.getEntry("MaxVelX").setNumber(2.0);
    tebConfigTable.getEntry("MaxVelXBackwards").setNumber(1.5);
    tebConfigTable.getEntry("AccLimX").setNumber(0.7);
    tebConfigTable.getEntry("MaxVelTheta").setNumber(5.0);
    tebConfigTable.getEntry("AccLimTheta").setNumber(6.0);
    updateGoal();
  }

  private void updateGoal() {
    xGoal = SmartDashboard.getNumber("limelight-pose/position/x", 0);
    yGoal = SmartDashboard.getNumber("limelight-pose/position/y", 0);
    angleGoal = SmartDashboard.getNumber("limelight-pose/orientation/z", 0);

    Robot.serv.setGoal(xGoal, yGoal, angleGoal);
    // SmartDashboard.putNumber("--------goal_position_x", xGoal);
    // SmartDashboard.putNumber("--------goal_position_y", yGoal);
    // SmartDashboard.putNumber("--------goal_orientation_z", angleGoal);
    // SmartDashboard.putBoolean("--------free_goal_vel", false);
  }

  private long lastUpdate = 0;

  @Override
  protected void execute() {
    double distanceError = new Vector2D(xGoal, yGoal).distance(new Vector2D(Robot.getPosX(), Robot.getPosY()));
    if (
        // System.currentTimeMillis() - lastUpdate >= 100
        // &&
    SmartDashboard.getBoolean("limelight-pose/correct", false)
        && distanceError > 0.07
        && finishedTime == 0
    ) {
      System.out.println("Updating pose estimate!");
      lastUpdate = System.currentTimeMillis();
      updateGoal();
    }

      double cmdVelX = 0;
      double cmdVelOmega = 0;
      if (Robot.serv != null) {
        cmdVelX = Robot.serv.getCmdVelX();
        cmdVelOmega = Robot.serv.getCmdVelTheta();
      }

    twist2DInput.setTwist(new Twist2D(cmdVelX, 0, cmdVelOmega));
    pipeline.execute();
    // SmartDashboard.putNumber("debug-setpoint-left", leftSetpoint);
      // SmartDashboard.putNumber("debug-setpoint-right", rightSetpoint);
      SmartDashboard.putNumber("debug-velocity-left", Robot.drivetrain.getLeftVelocity());
      SmartDashboard.putNumber("debug-velocity-right", Robot.drivetrain.getRightVelocity());


      // isFinished Checking
    double xError = xGoal - Robot.getPosX();
    double yError = yGoal - Robot.getPosY();
    double angleError = angleGoal * 180 / Math.PI - (-Robot.navx.getYaw());
    while (angleError > 180) {
      angleError -= 360;
    }
    while (angleError < -180) {
      angleError += 360;
    }
    // System.out.println(
    //     "xError " + xError +
    //         " yError " + yError +
    //         " angleError " + angleError);
    boolean finished = Math.abs(xError) < 0.038 &&
        Math.abs(yError) < 0.038 &&
        Math.abs(angleError) < 3;
    if (finished && finishedTime == 0) {
      this.finishedTime = System.currentTimeMillis();
    }
  }

  @Override
  protected boolean isFinished() {
    if (finishedTime != 0 && System.currentTimeMillis() > finishedTime + 50) {
      System.out.println("Close to goal: " + xGoal + " " + yGoal);
      Robot.drivetrain.setLeftVelocity(0);
      Robot.drivetrain.setRightVelocity(0);
      return true;
    }
    return false;
  }

  public static double AngleDifference(double angle1, double angle2) {
    double diff = (angle2 - angle1 + 180) % 360 - 180;
    return diff < -180 ? diff + 360 : diff;
  }
}
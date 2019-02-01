package org.team1540.localization2D.robot.commands.drivetrain;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.team1540.localization2D.datastructures.twod.Transform2D;
import org.team1540.localization2D.datastructures.twod.Twist2D;
import org.team1540.localization2D.robot.Robot;
import org.team1540.localization2D.robot.TankDriveTwist2DInput;
import org.team1540.localization2D.robot.Tuning;
import org.team1540.localization2D.utils.TrigUtils;
import org.team1540.rooster.drive.pipeline.CTREOutput;
import org.team1540.rooster.drive.pipeline.FeedForwardProcessor;
import org.team1540.rooster.drive.pipeline.UnitScaler;
import org.team1540.rooster.functional.Executable;
import org.team1540.rooster.wrappers.RevBlinken.ColorPattern;

public class UDPVelocityTwistDrive extends Command {
  Transform2D goal;

  private Executable pipeline;
  private TankDriveTwist2DInput twist2DInput;

  boolean freeGoalVel;

  boolean checkEnd;

  public UDPVelocityTwistDrive(Transform2D goal, boolean freeGoalVel) {
    this.goal = goal;
    this.freeGoalVel = freeGoalVel;

    this.checkEnd = true;
    requires(Robot.drivetrain);
  }

  public UDPVelocityTwistDrive() {
    this.checkEnd = false;
    this.freeGoalVel = false;
    requires(Robot.drivetrain);
  }

  @Override
  protected void initialize() {
    Robot.drivetrain.reset();
    Robot.drivetrain.configTalonsForVelocity();

    twist2DInput = new TankDriveTwist2DInput(Tuning.drivetrainRadius);
    pipeline = twist2DInput
        .then(new FeedForwardProcessor(0.27667, 0.054083, 0.08694))
        .then(new UnitScaler(Tuning.drivetrainTicksPerMeter, 10))
        .then(new CTREOutput(Robot.drivetrain.driveLeftMotorA, Robot.drivetrain.driveRightMotorA, true));

    if (checkEnd) {
      Robot.leds.set(ColorPattern.CHASE_BLUE);

      NetworkTable tebConfigTable = NetworkTableInstance.getDefault().getTable("TEBPlanner/Config");
      tebConfigTable.getEntry("TebReset").setBoolean(true);
      tebConfigTable.getEntry("MaxVelX").setNumber(2.0);
      tebConfigTable.getEntry("MaxVelXBackwards").setNumber(1.5);
      tebConfigTable.getEntry("AccLimX").setNumber(0.7);
      tebConfigTable.getEntry("MaxVelTheta").setNumber(5.0);
      tebConfigTable.getEntry("AccLimTheta").setNumber(6.0);
    }
    updateGoal();
  }

  private void updateGoal() {
    double xGoal = SmartDashboard.getNumber("test-goal/position/x", 2);
    double yGoal = SmartDashboard.getNumber("test-goal/position/y", 0);
    double angleGoal = SmartDashboard.getNumber("test-goal/orientation/z", 0);
    System.out.println("Updated goal!");

    Robot.wheelOdometry.reset();
    Robot.udpSender.setGoal(new Transform2D(xGoal, yGoal, angleGoal));
    Robot.udpSender.setViaPoint(new Vector2D(1, -1));
  }

  @Override
  protected void execute() {
    Twist2D cmdVel = Robot.udpReceiver.getCmdVel();
    twist2DInput.setTwist(cmdVel);
    pipeline.execute();
  }

  @Override
  protected boolean isFinished() {
    if (!this.checkEnd) {
      return false;
    }
    // isFinished Checking
    Vector3D odomPosition = Robot.wheelOdometry.getOdomToBaseLink().getPosition(); // TODO: This should use javaTF
    double xError = goal.getX() - odomPosition.getX();
    double yError = goal.getY() - odomPosition.getY();
    double angleError = TrigUtils.SignedAngleDifference(goal.getTheta(), Math.toRadians(-Robot.navx.getYaw())); // TODO: If this is the proper way to calculate signed angle, this should be moved to the TrigUtils class

    boolean finished = Math.abs(xError) < 0.038 && // TODO: Make this a static function
        Math.abs(yError) < 0.038 &&
        Math.abs(angleError) < Math.toRadians(3);
    if (finished) {
      System.out.println("Close to goalAvg: " + goal.getX() + " " + goal.getY());
      Robot.drivetrain.stop();
    }
    return finished;
  }
}

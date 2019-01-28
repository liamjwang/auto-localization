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
import org.team1540.rooster.util.Executable;
import org.team1540.rooster.wrappers.RevBlinken.ColorPattern;

public class UDPAutoLineup extends Command {
  Transform2D goal;

  private Executable pipeline;
  private TankDriveTwist2DInput twist2DInput;

  long finishedTime = 0;
  private long lastUpdate = 0;


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

    NetworkTable tebConfigTable = NetworkTableInstance.getDefault().getTable("TEBPlanner/Config");
    tebConfigTable.getEntry("TebReset").setBoolean(true);
    tebConfigTable.getEntry("MaxVelX").setNumber(2.0);
    tebConfigTable.getEntry("MaxVelXBackwards").setNumber(1.5);
    tebConfigTable.getEntry("AccLimX").setNumber(0.7);
    tebConfigTable.getEntry("MaxVelTheta").setNumber(5.0);
    tebConfigTable.getEntry("AccLimTheta").setNumber(6.0);
    updateGoal();
  }

  private void updateGoal() {
    double xGoal = SmartDashboard.getNumber("limelight-pose/position/x", 0);
    double yGoal = SmartDashboard.getNumber("limelight-pose/position/y", 0);
    double angleGoal = SmartDashboard.getNumber("limelight-pose/orientation/z", 0);

    Robot.udpSender.setGoal(new Transform2D(xGoal, yGoal, angleGoal));
  }

  @Override
  protected void execute() {
    Vector3D odomPosition = Robot.odometry.getOdomToBaseLink().getPosition(); // TODO: This should use javaTF
    double distanceError = goal.getPositionVector().distance(new Vector2D(odomPosition.getX(), odomPosition.getY()));

    if (SmartDashboard.getBoolean("limelight-pose/correct", false) // TODO: Use Pose2D averaging
        && distanceError > 0.07 // TODO: Make this distance tunable
        && finishedTime == 0
    ) {
      System.out.println("Updating pose estimate!");
      lastUpdate = System.currentTimeMillis();
      updateGoal();
    }

    // Send velocity commaand
    Twist2D cmdVel = Robot.udpReceiver.getCmdVel();
    twist2DInput.setTwist(cmdVel);
    pipeline.execute();

    // isFinished Checking
    double xError = goal.getX() - odomPosition.getX();
    double yError = goal.getY() - odomPosition.getY();
    double angleError = TrigUtils.SignedAngleDifference(goal.getTheta(), Math.toRadians(-Robot.navx.getYaw())); // TODO: If this is the proper way to calculate signed angle, this should be moved to the TrigUtils class

    boolean finished = Math.abs(xError) < 0.038 && // TODO: Make this a static function
        Math.abs(yError) < 0.038 &&
        Math.abs(angleError) < Math.toRadians(3);
    if (finished && finishedTime == 0) {
      this.finishedTime = System.currentTimeMillis();
    }
  }

  @Override
  protected boolean isFinished() {
    if (finishedTime != 0 && System.currentTimeMillis() > finishedTime + 50) { // TODO: Make the finished time tunable
      System.out.println("Close to goal: " + goal.getX() + " " + goal.getY());
      Robot.drivetrain.stop();
      return true;
    }
    return false;
  }
}

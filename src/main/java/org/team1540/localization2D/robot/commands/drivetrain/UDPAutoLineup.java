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
import org.team1540.localization2D.utils.Transform2DRollingAverage;
import org.team1540.localization2D.utils.TrigUtils;
import org.team1540.rooster.drive.pipeline.CTREOutput;
import org.team1540.rooster.drive.pipeline.FeedForwardProcessor;
import org.team1540.rooster.drive.pipeline.UnitScaler;
import org.team1540.rooster.util.Executable;
import org.team1540.rooster.wrappers.RevBlinken.ColorPattern;

public class UDPAutoLineup extends Command {
  Transform2DRollingAverage goalAvg;

  private Executable pipeline;
  private TankDriveTwist2DInput twist2DInput;

  long finishedTime = 0;
  private long lastUpdate = 0;


  public UDPAutoLineup() {
    requires(Robot.drivetrain);
    goalAvg = new Transform2DRollingAverage(1);
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
    tebConfigTable.getEntry("AccLimTheta").setNumber(5.0);
    updateGoal();
  }

  private void updateGoal() {
    double xGoal = SmartDashboard.getNumber("limelight-pose/position/x", 0);
    double yGoal = SmartDashboard.getNumber("limelight-pose/position/y", 0);
    double angleGoal = SmartDashboard.getNumber("limelight-pose/orientation/z", 0);

    goalAvg.addTransform(new Transform2D(xGoal, yGoal, angleGoal));
    Robot.udpSender.setGoal(goalAvg.getAverage());
    Transform2D via_point = goalAvg.getAverage().add(new Transform2D(-0.7, 0, 0));
    Robot.udpSender.setViaPoint(via_point.getPositionVector());
  }

  @Override
  protected void execute() {
    Vector3D odomPosition = Robot.wheelOdometry.getOdomToBaseLink().getPosition(); // TODO: This should use javaTF
    Transform2D average = goalAvg.getAverage();
    double angleError = TrigUtils.SignedAngleDifference(average.getTheta(), Math.toRadians(-Robot.navx.getYaw())); // TODO: If this is the proper way to calculate signed angle, this should be moved to the TrigUtils class

    double distanceError = average.getPositionVector().distance(new Vector2D(odomPosition.getX(), odomPosition.getY()));

    if (SmartDashboard.getBoolean("limelight-pose/correct", false) // TODO: Use Pose2D averaging
        && (distanceError > 0.07)
        // || angleError > Math.toRadians(10)) // TODO: Make this distance tunable
        && finishedTime == 0
    ) {
      lastUpdate = System.currentTimeMillis();
      updateGoal();
    }

    // Send velocity commaand
    Twist2D cmdVel = Robot.udpReceiver.getCmdVel();
    twist2DInput.setTwist(cmdVel);
    pipeline.execute();

    // isFinished Checking
    double xError = average.getX() - odomPosition.getX();
    double yError = average.getY() - odomPosition.getY();

    boolean finished = Math.abs(xError) < 0.018 && // TODO: Make this a static function
        Math.abs(yError) < 0.018 &&
        Math.abs(angleError) < Math.toRadians(3);
    if (finished && finishedTime == 0) {
      this.finishedTime = System.currentTimeMillis();
    }
  }

  @Override
  protected boolean isFinished() {
    if (finishedTime != 0 && System.currentTimeMillis() > finishedTime + 0) { // TODO: Make the finished time tunable
      Robot.drivetrain.stop();
      return true;
    }
    return false;
  }
}

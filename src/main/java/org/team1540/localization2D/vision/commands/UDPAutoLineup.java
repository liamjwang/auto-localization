package org.team1540.localization2D.vision.commands;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.team1540.localization2D.datastructures.threed.Transform3D;
import org.team1540.localization2D.datastructures.twod.Twist2D;
import org.team1540.localization2D.robot.Robot;
import org.team1540.localization2D.robot.Tuning;
import org.team1540.localization2D.utils.TankDriveTwist2DInput;
import org.team1540.localization2D.utils.TrigUtils;
import org.team1540.rooster.functional.Executable;
import org.team1540.rooster.wrappers.RevBlinken.ColorPattern;

public class UDPAutoLineup extends Command {
  Transform3D goal;
  private Executable pipeline;
  private TankDriveTwist2DInput twist2DInput;

  public UDPAutoLineup() {
    requires(Robot.drivetrain);
    twist2DInput = new TankDriveTwist2DInput(Tuning.drivetrainRadius);
    // pipeline = twist2DInput
    //     .then(new FeedForwardProcessor(0.27667, 0.054083,0.08694))
    //     .then((Processor<TankDriveData, TankDriveData>) tankDriveData -> new TankDriveData(tankDriveData.left, tankDriveData.right))
        // .then(new UnitScaler(Tuning.drivetrainTicksPerMeter, 10))
        // .then(new CTREOutput(Robot.drivetrain.driveLeftMotorA, Robot.drivetrain.driveRightMotorA, true));
  }

  @Override
  protected void initialize() {
    Robot.drivetrain.reset();
    Robot.drivetrain.configTalonsForVelocity();

    Robot.leds.set(ColorPattern.CHASE_BLUE);

    NetworkTable tebConfigTable = NetworkTableInstance.getDefault().getTable("TEBPlanner/Config");
    tebConfigTable.getEntry("TEBReset").setBoolean(true);
    tebConfigTable.getEntry("MaxVelX").setNumber(2.0);
    tebConfigTable.getEntry("MaxVelXBackwards").setNumber(1.5);
    tebConfigTable.getEntry("AccLimX").setNumber(0.8);
    tebConfigTable.getEntry("MaxVelTheta").setNumber(6.0);
    tebConfigTable.getEntry("AccLimTheta").setNumber(10.0);
    if (Robot.visionLocalization.attemptUpdatePose()) { // TODO: Make this distance tunable
      computeAndUpdateGoal();
    } else {
      // if (Robot.visionLocalization.millisSinceLastAcquired() < 2000) {
        updateGoal(Robot.lastOdomToGoal);
      // } else {
      //   Robot.leds.set(ColorPattern.RED);
      //   cancel();
      // }
    }
  }

  private void computeAndUpdateGoal() {
    updateGoal(computeGoal());
  }

  private Transform3D computeGoal() {
    Transform3D visionTargetToLimelightOrNull = Robot.limelightInterface.getVisionTargetToLimelightOrNull();
    // if (targetFound) {
    Transform3D solvepnp = Transform3D.IDENTITY;
    if (visionTargetToLimelightOrNull != null) {
      solvepnp = visionTargetToLimelightOrNull.negate();
    }
    return Robot.wheelOdometry.getOdomToBaseLink()
        // .add(solvepnp)
        .add(Robot.visionLocalization.getBaseLinkToVisionTarget())
        .add(new Transform3D(new Vector3D(-0.65, 0, 0), Rotation.IDENTITY));
  }

  private void updateGoal(Transform3D newGoal) {
    this.goal = newGoal;
    Robot.udpSender.setGoal(goal.toTransform2D());
    System.out.println("Goal updated");

    Transform3D via_point = goal.add(new Transform3D(-0.7, 0, 0));
    Robot.udpSender.setViaPoint(via_point.toTransform2D().getPositionVector());
  }

  @Override
  protected void execute() {
    if (Robot.visionLocalization.attemptUpdatePose() && (getDistanceError() > 0.05)) { // TODO: Make this distance tunable
      computeAndUpdateGoal();
    }

    // Send velocity command
    Twist2D cmdVel = Robot.udpReceiver.getCmdVel();
    // cmdVel.putToNetworkTable("LineupDebug/Target/");
    SmartDashboard.putNumber("LineupDebug/Target/x", cmdVel.getX());
    SmartDashboard.putNumber("LineupDebug/Target/z", cmdVel.getOmega());
    // twist2DInput.setTwist(cmdVel);
    // pipeline.execute();
    double leftSetpoint = (cmdVel.getX() - cmdVel.getOmega() * Tuning.drivetrainRadius)*Tuning.drivetrainTicksPerMeter/10;
    double rightSetpoint = (cmdVel.getX() + cmdVel.getOmega() * Tuning.drivetrainRadius)*Tuning.drivetrainTicksPerMeter/10;
    Robot.drivetrain.setLeftVelocity(leftSetpoint);
    Robot.drivetrain.setRightVelocity(rightSetpoint);
  }

  @Override
  protected boolean isFinished() {
    if (goal == null) {
      return true;
    }
    if (getDistanceError() < 0.02 && Math.abs(getAngleError()) < Math.toRadians(3)) {
      Robot.drivetrain.stop();
      Robot.leds.set(ColorPattern.LIME);
      return true;
    }
    return false;
  }

  private double getDistanceError() {
    Vector3D odomPosition = Robot.wheelOdometry.getOdomToBaseLink().getPosition(); // TODO: This should use javaTF
    return goal.toTransform2D().getPositionVector().distance(new Vector2D(odomPosition.getX(), odomPosition.getY()));
  }

  private double getAngleError() {
    return TrigUtils.SignedAngleDifference(goal.toTransform2D().getTheta(), Math.toRadians(-Robot.navx.getYaw()));
  }
}

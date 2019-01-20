package org.team1540.localization2D;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.command.TimedCommand;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.io.IOException;
import java.util.OptionalDouble;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.team1540.localization2D.subsystems.DriveTrain;
import org.team1540.rooster.drive.pipeline.DriveData;
import org.team1540.rooster.drive.pipeline.FeedForwardProcessor;
import org.team1540.rooster.drive.pipeline.HeadingPIDProcessor;
import org.team1540.rooster.drive.pipeline.TankDriveData;
import org.team1540.rooster.drive.pipeline.UnitScaler;
import org.team1540.rooster.functional.Executable;
import org.team1540.rooster.functional.Input;
import org.team1540.rooster.power.PowerManager;
import org.team1540.rooster.testers.motor.SimpleControllersTester;
import org.team2471.frc.lib.motion_profiling.Autonomous;
import org.team2471.frc.lib.motion_profiling.Path2D;
import org.team2471.frc.lib.motion_profiling.Path2D.RobotDirection;

public class Robot extends TimedRobot {
  public static final DriveTrain drivetrain = new DriveTrain();
  public static AHRS navx = new AHRS(Port.kMXP);
  public static UDPServer serv;
  private static XboxController xboxController = new XboxController(1);

  // static {
  //   try {
  //     serv = new UDPServer();
  //   } catch (Exception e) {
  //     e.printStackTrace();
  //   }
  // }

  private SendableChooser<String> autoPosition;
  private SendableChooser<Boolean> driveMode;

  private Command autoCommand;


  @Override
  public void robotInit() {
    // disable unused things
    LiveWindow.disableAllTelemetry();
    PowerManager.getInstance().interrupt();

//    SmartDashboard.putData(drivetrain);
//    SmartDashboard.putData(PowerManager.getInstance());

    //     Command reset = new SimpleCommand("Reset", () -> {
    //         System.out.println("Reset odometry!");
    //       localizationInit();
    //     });
    //     reset.setRunWhenDisabled(true);
    //     reset.start();
    //       SmartDashboard.putData(reset);
    //
    //       Command runTEB = new SimpleCommand("Start segment", () -> {
    //         new UDPVelocityTwistDrive(2, 0, 0, false).start();
    //     });
    // //      runTEB.start();
    //     SmartDashboard.putData(runTEB);
  }

  @Override
  public void disabledInit() {
    Robot.drivetrain.reset();
    new TimedCommand(2) {
      @Override
      protected void end() {
        Robot.drivetrain.setBrake(false);
      }
    };

  }

  @Override
  public void autonomousInit() {

    drivetrain.configFactoryDefault();
    drivetrain.reset();
    drivetrain.configTalonsForPosition();
    drivetrain.disableCurrentLimiting();

    Path2D path = new Path2D();
    path.setMirrored(false);
    path.setRobotDirection(RobotDirection.FORWARD);

    //    double theta = SmartDashboard.getNumber("goal_position_z", 0);
    //    path.addPointAndTangent(SmartDashboard.getNumber("goal_position_x", 0),
    //        SmartDashboard.getNumber("goal_position_y", 0), Math.cos(theta), Math.sin(theta));;

    // path.addPointAndTangent(0.0, 0.0, 0.0, 4.0);
    // path.addPointAndTangent(2.0, 5.0, 0.0, 4.0);
    // path.addEasePoint(0.0, 0.0);
    // path.addEasePoint(1.9, 1.0);

    // path.addPointAndTangent(0, 0, 2, 0);
    // path.addPointAndTangent(4, 4, -2, 0);
    //
    path.addPointAndTangent(0, 0, 0, 0);
    path.addPointAndTangent(4, 0, 0, 0);

    path.addEasePoint(0.0, 0.0);
    path.addEasePoint(3.0, 1.0);

    path.setSpeed(1);
    Autonomous autonomous = new Autonomous("Auto");
    autonomous.setMirrored(false);
    autonomous.setRobotLength(2.49);
    autonomous.setRobotWidth(2.00);
    autonomous.setTrackWidth(2.00);
    path.setAutonomous(autonomous);
    System.out.println("Starting");

    new Command() {

      // private MeanLibInput input = new MeanLibInput(path);
      private Input<TankDriveData> input = () -> new TankDriveData(
          new DriveData(
              OptionalDouble.of(0),
              OptionalDouble.of(1),
              OptionalDouble.empty(),
              OptionalDouble.empty()
          ),
          new DriveData(
              OptionalDouble.of(0),
              OptionalDouble.of(1),
              OptionalDouble.empty(),
              OptionalDouble.empty()
          ),
          OptionalDouble.empty(),
          OptionalDouble.empty()
      );
      private Executable pipeline = input
          .then(new FeedForwardProcessor(Tuning.kVApp, Tuning.vIntercept, Tuning.kAApp))
          .then(new HeadingPIDProcessor(Tuning.headingP, () -> Math.toRadians(navx.getYaw())))
          .then(new UnitScaler(Tuning.tpu, .1))
          .then((data) -> {
            SmartDashboard.putNumber("pipeline_lPos", data.left.position.orElse(-1));
            SmartDashboard.putNumber("pipeline_rPos", data.right.position.orElse(-1));
            SmartDashboard.putNumber("pipeline_lVel", data.left.velocity.orElse(-1));
            SmartDashboard.putNumber("pipeline_rVel", data.right.velocity.orElse(-1));
            SmartDashboard.putNumber("pipeline_lAFF", data.left.additionalFeedForward.orElse(-1));
            SmartDashboard.putNumber("pipeline_rAFF", data.right.additionalFeedForward.orElse(-1));
            return data;
          })
          .then(drivetrain.getCTREOutput());

      {
        requires(drivetrain);
      }

      @Override
      protected void execute() {
        pipeline.execute();
      }

      @Override
      protected boolean isFinished() {
        // return input.isFinished();
        return false;
      }
    }.start();
  }

  @Override
  public void teleopInit() {
    Robot.drivetrain.reset();
    Robot.drivetrain.configFactoryDefault();
    Scheduler.getInstance().removeAll();
    new SimpleControllersTester(drivetrain.driveMotorAll).addAllSendables().start();
    // new PercentDrive().start();
//    new VelocityDrive().start();
  }

  @Override
  public void testInit() {
    Robot.drivetrain.reset();
    Robot.drivetrain.configTalonsForVelocity();
    // new UDPVelocityTwistDrive(0, 0, 0, false).start();
  }

  @Override
  public void robotPeriodic() {
    Scheduler.getInstance().run();

    SmartDashboard.putData(Scheduler.getInstance());
    //     NetworkTable limeTable = NetworkTableInstance.getDefault().getTable("limelight");
    //     double tx0 = 27.85* limeTable.getEntry("tx0").getDouble(0);
    //     limeTable.getEntry("tx00").setDouble(tx0);
    //
    //       double tx1 = 27.85* limeTable.getEntry("tx1").getDouble(0);
    //       limeTable.getEntry("tx11").setDouble(tx1);
    //     localizationPeriodic();
    //     limelightLocalizationPeriodic();
  }

  @Override
  public void disabledPeriodic() {
  }

  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopPeriodic() {
  }

  @Override
  public void testPeriodic() {
  }

  private static LocalizationAccum2D accum2D = new LocalizationAccum2D();

  private void localizationInit() {
    Robot.navx.zeroYaw();
    accum2D.reset();
    Robot.drivetrain.zeroEncoders();
  }

  private void localizationPeriodic() {
    double leftDistance = drivetrain.getLeftPosition()/Tuning.drivetrainTicksPerMeter;
    double rightDistance = drivetrain.getRightPosition()/Tuning.drivetrainTicksPerMeter;
    double gyroAngle = Robot.navx.getAngle();

    accum2D.update(leftDistance, rightDistance, Math.toRadians(gyroAngle));

    SmartDashboard.putNumber("pose-position-x", accum2D.getXpos());
    SmartDashboard.putNumber("pose-position-y", accum2D.getYpos());
    SmartDashboard.putNumber("pose-orientation-z", gyroAngle);

    double leftVelocity = drivetrain.getLeftVelocity()*10/Tuning.drivetrainTicksPerMeter;
    double rightVelocity = drivetrain.getRightVelocity()*10/Tuning.drivetrainTicksPerMeter;

    double xvel = (leftVelocity + rightVelocity) / 2;
    double thetavel = (leftVelocity-rightVelocity)/(Tuning.drivetrainRadius)/2;


//          SmartDashboard.putNumber("../limelight/tx00", SmartDashboard.getNumber("../limelight/tx0", 0)*26.85);

//    SmartDashboard.putNumber("twist-linear-x", xvel);
//    SmartDashboard.putNumber("twist-angular-z", thetavel);

    if (serv != null) {
        try {
            serv.sendPoseAndTwist(
                    accum2D.getXpos(),
                    accum2D.getYpos(),
                    gyroAngle,
                    xvel,
                    thetavel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // SmartDashboard.putNumber("Left Distance", leftDistance);
    // SmartDashboard.putNumber("Right Distance", rightDistance);


  }

  private void limelightLocalizationPeriodic() {

    double CAMERA_TILT = Math.toRadians(-40.2);
    double CAMERA_ROLL = Math.toRadians(-1.38);
    double PLANE_HEIGHT = 0.74; // Height of vision targets in meters
    // Vector3D CAMERA_POSITION = new Vector3D(0, 0, 1.26); // Position of camera in meters
    Vector3D CAMERA_POSITION = new Vector3D(0.15, 0, 1.26); // Position of camera in meters


    NetworkTable limelightTable = NetworkTableInstance.getDefault().getTable("limelight");

    // TODO: Filter limelight contours using size, angle, etc.
    double tx0 = limelightTable.getEntry("tx0").getDouble(100);
    double ty0 = limelightTable.getEntry("ty0").getDouble(100);

    double tx1 = limelightTable.getEntry("tx1").getDouble(100);
    double ty1 = limelightTable.getEntry("ty1").getDouble(100);

    if (tx0 + tx1 + ty0 + ty1 > 5) {
      System.out.println("Unable to get limelight values!");
      return;
    }

    Vector2D leftAngles;
    Vector2D rightAngles;

    if (tx0 < tx1) {
       leftAngles = new Vector2D(tx0, ty0);
       rightAngles = new Vector2D(tx1, ty1);
    } else {
       leftAngles = new Vector2D(tx1, ty1);
       rightAngles = new Vector2D(tx0, ty0);
    }

    Rotation cameraTilt = new Rotation(Vector3D.PLUS_J, CAMERA_TILT, RotationConvention.FRAME_TRANSFORM);
    Rotation cameraRoll = new Rotation(Vector3D.PLUS_I, CAMERA_ROLL, RotationConvention.FRAME_TRANSFORM);

    // Rotation cameraRotation = cameraRoll.applyTo(cameraTilt);
    Rotation cameraRotation = cameraTilt.applyTo(cameraRoll);
    Pose pose = LimelightLocalization.poseFromTwoCamPoints(leftAngles, rightAngles, PLANE_HEIGHT, CAMERA_POSITION, cameraRotation);

    double off = -0.5;
    double x_off = pose.position.getX()+off*Math.cos(pose.orientation.getZ());
    double y_off = pose.position.getY()+off*Math.sin(pose.orientation.getZ());
    //
    // double x_off = pose.position.getX();//+off*Math.cos(pose.orientation.getZ());
    // double y_off = pose.position.getY();//+off*Math.sin(pose.orientation.getZ());
    // System.out.printf("x: %08.3f y: %08.3f z: %08.3f\n", pose.position.getX(), pose.position.getY(), pose.orientation.getZ());

    SmartDashboard.putNumber("limelight-pose/position/x", x_off);
    SmartDashboard.putNumber("limelight-pose/position/y", y_off);
    SmartDashboard.putNumber("limelight-pose/orientation/z", pose.orientation.getZ());
  }

    public static double getPosX() { return accum2D.getXpos(); }
    public static double getPosY() { return accum2D.getYpos(); }

}

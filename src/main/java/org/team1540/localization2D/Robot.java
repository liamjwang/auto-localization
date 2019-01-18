package org.team1540.localization2D;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.command.TimedCommand;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.io.IOException;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.team1540.localization2D.autogroups.TestSequence;
import org.team1540.localization2D.commands.drivetrain.PercentDrive;
import org.team1540.localization2D.commands.drivetrain.UDPVelocityTwistDrive;
import org.team1540.localization2D.subsystems.DriveTrain;
import org.team1540.rooster.power.PowerManager;
import org.team1540.rooster.util.SimpleCommand;

public class Robot extends IterativeRobot {
  public static final DriveTrain drivetrain = new DriveTrain();
  public static AHRS navx = new AHRS(Port.kMXP);
  public static UDPServer serv;

  static {
    try {
      serv = new UDPServer();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

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

    Command reset = new SimpleCommand("Reset", () -> {
        System.out.println("Reset odometry!");
      localizationInit();
    });
    reset.setRunWhenDisabled(true);
    reset.start();
      SmartDashboard.putData(reset);

      Command runTEB = new SimpleCommand("Start segment", () -> {
        new UDPVelocityTwistDrive(2, 0, 0, false).start();
    });
//      runTEB.start();
    SmartDashboard.putData(runTEB);
  }

  @Override
  public void disabledInit() {
    Robot.drivetrain.reset();
    Robot.drivetrain.configTalonsForVelocity();
    new TimedCommand(2) {
      @Override
      protected void end() {
        Robot.drivetrain.setBrake(false);
      }
    };

  }

  @Override
  public void autonomousInit() {

    Robot.drivetrain.reset();
    Robot.drivetrain.configTalonsForVelocity();
    new TestSequence().start();
  }

  @Override
  public void teleopInit() {
    Robot.drivetrain.reset();
    Robot.drivetrain.enableCurrentLimiting();
    Robot.drivetrain.configTalonsForVelocity();
    new PercentDrive().start();
//    new VelocityDrive().start();
  }

  @Override
  public void testInit() {
    Robot.drivetrain.reset();
    Robot.drivetrain.configTalonsForVelocity();
    new UDPVelocityTwistDrive(0, 0, 0, false).start();
  }

  @Override
  public void robotPeriodic() {
//    SmartDashboard.putData(Scheduler.getInstance());
    NetworkTable limeTable = NetworkTableInstance.getDefault().getTable("limelight");
    double tx0 = 27.85* limeTable.getEntry("tx0").getDouble(0);
    limeTable.getEntry("tx00").setDouble(tx0);

      double tx1 = 27.85* limeTable.getEntry("tx1").getDouble(0);
      limeTable.getEntry("tx11").setDouble(tx1);

    Scheduler.getInstance().run();
    localizationPeriodic();
    limelightLocalizationPeriodic();
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

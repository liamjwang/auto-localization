package org.team1540.robot2018;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.base.Utilities;
import org.team1540.base.adjustables.AdjustableManager;
import org.team1540.base.power.PowerManager;
import org.team1540.base.util.SimpleCommand;
import org.team1540.robot2018.commands.TankDrive;
import org.team1540.robot2018.commands.auto.DriveBackward;
import org.team1540.robot2018.commands.auto.StraightAuto;
import org.team1540.robot2018.commands.elevator.JoystickElevator;
import org.team1540.robot2018.commands.elevator.MoveElevatorToPosition;
import org.team1540.robot2018.commands.groups.FrontScale;
import org.team1540.robot2018.commands.groups.GroundPosition;
import org.team1540.robot2018.commands.groups.IntakeSequence;
import org.team1540.robot2018.commands.intake.EjectCube;
import org.team1540.robot2018.commands.intake.OpenArms;
import org.team1540.robot2018.commands.wrist.JoystickWrist;
import org.team1540.robot2018.subsystems.ClimberWinch;
import org.team1540.robot2018.subsystems.DriveTrain;
import org.team1540.robot2018.subsystems.Elevator;
import org.team1540.robot2018.subsystems.Intake;
import org.team1540.robot2018.subsystems.IntakeArms;
import org.team1540.robot2018.subsystems.Wrist;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;

public class Robot extends IterativeRobot {
  public static final DriveTrain drivetrain = new DriveTrain();
  public static final Intake intake = new Intake();
  public static final IntakeArms intakeArms = new IntakeArms();
  public static final Elevator elevator = new Elevator();
  public static final Wrist wrist = new Wrist();
  public static final ClimberWinch winch = new ClimberWinch();

  private SendableChooser<String> side = new SendableChooser<>();

  private Command autoCommand = new AutonomousProfiling();

  @Override
  public void robotInit() {
    PowerManager.getInstance().interrupt();
    AdjustableManager.getInstance().add(new Tuning());
    AdjustableManager.getInstance().add(drivetrain);
    AdjustableManager.getInstance().add(autoCommand);
    PowerManager.getInstance().setRunning(false);
    side.addDefault("Left", "L");
    side.addObject("Right", "R");
    side.addObject("None", "X");

    SmartDashboard.putData("Robot Position for Auto", side);

    // configure controls

    OI.autoIntakeButton.whenPressed(new IntakeSequence());
    OI.autoEjectButton.whenPressed(new EjectCube());
    OI.stopIntakeButton.whenPressed(new SimpleCommand("Stop intake", intake::stop, intake));

    OI.autoIntakeButton.whileHeld(new OpenArms());

    OI.elevatorExchangeButton.whenPressed(new MoveElevatorToPosition(Tuning.elevatorExchangePosition));

    OI.elevatorSwitchButton.whenPressed(new MoveElevatorToPosition(Tuning.elevatorFrontSwitchPosition));
    OI.elevatorRaiseButton.whenPressed(new MoveElevatorToPosition(Tuning.elevatorScalePosition));
    OI.elevatorFrontScaleButton.whenPressed(new FrontScale());
    OI.elevatorLowerButton.whenPressed(new GroundPosition());

    OI.enableElevatorAxisControlButton.whileHeld(new JoystickElevator());
    OI.enableWristAxisControlButton.whileHeld(new JoystickWrist());


    OI.winchInSlowButton.whileHeld(new SimpleCommand("Winch In Low", () -> winch.set(Tuning.winchInLowVel), winch));

    OI.winchInFastButton.whileHeld(new SimpleCommand("Winch In High", () -> winch.set(Tuning.winchInHighVel), winch));

    // configure SmartDashboard
    Command zeroWrist = new SimpleCommand("[Wrist] Zero Wrist", wrist::resetEncoder);
    zeroWrist.setRunWhenDisabled(true);
    SmartDashboard.putData(zeroWrist);

    Command zeroElevator = new SimpleCommand("[Elevator] Zero Elevator", elevator::resetEncoder);
    zeroElevator.setRunWhenDisabled(true);
    SmartDashboard.putData(zeroElevator);

    SmartDashboard.putData("[Drivetrain] ***** MANUAL DRIVE OVERRIDE *****", new TankDrive());

    new Thread(() -> {
      UsbCamera camera = CameraServer.getInstance().startAutomaticCapture(Tuning.camID);
      camera.setResolution(640, 480);

      CvSink cvSink = CameraServer.getInstance().getVideo();
      CvSource outputStream = CameraServer.getInstance().putVideo("Camera "+Tuning.camID, 640, 480);

      Mat source = new Mat();
      Mat output = new Mat();

      while(!Thread.interrupted()) {
        cvSink.grabFrame(source);
        //				Imgproc.cvtColor(source, output, Imgproc.COLOR_BGR2GRAY);
        Point pt1 = new Point(source.width()/2+Tuning.crosshairsSize, source.height()/2);
        Point pt2 = new Point(source.width()/2-Tuning.crosshairsSize, source.height()/2);
        Point pt3 = new Point(source.width()/2, source.height()/2+Tuning.crosshairsSize);
        Point pt4 = new Point(source.width()/2, source.height()/2-Tuning.crosshairsSize);
        Imgproc.line(source, pt1, pt2, new Scalar(0,255,0), Tuning.crosshairsThicccness);
        Imgproc.line(source, pt3, pt4, new Scalar(0,255,0), Tuning.crosshairsThicccness);
        outputStream.putFrame(source);
      }
    }).start();
  }

  @Override
  public void disabledInit() {
  }

  @Override
  public void autonomousInit() {
    elevator.resetEncoder();
    if (side.getSelected().equals(DriverStation.getInstance().getGameSpecificMessage().substring(0, 1))) {
      System.out.println("Starting cube auto");
      new StraightAuto().start();
    } else {
      new DriveBackward(Tuning.driveForwardTime).start();
    }
    // Scheduler.getInstance().add(autoCommand);
  }

  @Override
  public void teleopInit() {
  }

  @Override
  public void testInit() {
  }

  @Override
  public void robotPeriodic() {
    Scheduler.getInstance().run();
    SmartDashboard.putNumber("lVelocity", Robot.drivetrain.getLeftVelocity());
    SmartDashboard.putNumber("rVelocity", Robot.drivetrain.getRightVelocity());
  }

  @Override
  public void disabledPeriodic() {
  }

  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopPeriodic() {
    Robot.drivetrain.prepareForMotionProfiling();
    Robot.drivetrain.setLeftVelocity(Utilities.processDeadzone((OI.getDriverLeftY() + OI
        .getDriverLeftTrigger() - OI.getDriverRightTrigger()), 0.1) * 1000);
    Robot.drivetrain.setRightVelocity(Utilities.processDeadzone((OI.getDriverRightY() + OI
        .getDriverLeftTrigger() - OI.getDriverRightTrigger()), 0.1) * 1000);
  }
}

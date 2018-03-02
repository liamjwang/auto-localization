package org.team1540.robot2018;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jaci.pathfinder.Waypoint;
import openrio.powerup.MatchData;
import openrio.powerup.MatchData.GameFeature;
import openrio.powerup.MatchData.OwnedSide;
import org.opencv.core.Mat;
import org.team1540.base.adjustables.AdjustableManager;
import org.team1540.base.power.PowerManager;
import org.team1540.base.util.SimpleCommand;
import org.team1540.robot2018.commands.TankDrive;
import org.team1540.robot2018.commands.auto.AutonomousProfiling;
import org.team1540.robot2018.commands.auto.AutonomousProfiling.TrajectorySegment;
import org.team1540.robot2018.commands.auto.DriveBackward;
import org.team1540.robot2018.commands.elevator.JoystickElevator;
import org.team1540.robot2018.commands.elevator.MoveElevatorToPosition;
import org.team1540.robot2018.commands.groups.FrontScale;
import org.team1540.robot2018.commands.groups.GroundPosition;
import org.team1540.robot2018.commands.groups.IntakeSequence;
import org.team1540.robot2018.commands.intake.EjectCube;
import org.team1540.robot2018.commands.wrist.CalibrateWrist;
import org.team1540.robot2018.commands.wrist.JoystickWrist;
import org.team1540.robot2018.subsystems.ClimberWinch;
import org.team1540.robot2018.subsystems.DriveTrain;
import org.team1540.robot2018.subsystems.Elevator;
import org.team1540.robot2018.subsystems.Intake;
import org.team1540.robot2018.subsystems.IntakeArms;
import org.team1540.robot2018.subsystems.Wrist;

public class Robot extends IterativeRobot {
  public static final DriveTrain drivetrain = new DriveTrain();
  public static final Intake intake = new Intake();
  public static final IntakeArms intakeArms = new IntakeArms();
  public static final Elevator elevator = new Elevator();
  public static final Wrist wrist = new Wrist();
  public static final ClimberWinch winch = new ClimberWinch();

  private Command emergencyDriveCommand = new TankDrive();


  private SendableChooser<String> autoPosition;
  private SendableChooser<Boolean> driveMode;

  private Command autoCommand;

  @Override
  public void robotInit() {
    // disable unused things
    LiveWindow.disableAllTelemetry();
    PowerManager.getInstance().interrupt();

    // configure SmartDashboard
    AdjustableManager.getInstance().add(new Tuning());
    autoPosition = new SendableChooser<>();
    autoPosition.addDefault("Middle", "Middle");
    autoPosition.addObject("Left", "Left");
    autoPosition.addObject("Right", "Right");
    autoPosition.addObject("Stupid", "Stupid");

    SmartDashboard.putData("Auto mode", autoPosition);

    driveMode = new SendableChooser<>();
    driveMode.addDefault("PID Drive", false);
    driveMode.addObject("Manual Override", true);
    SmartDashboard.putData("[Drivetrain] ***** DRIVE OVERRIDE *****", driveMode);

    Command zeroWrist = new SimpleCommand("[Wrist] Zero Wrist", wrist::resetEncoder);
    zeroWrist.setRunWhenDisabled(true);
    SmartDashboard.putData(zeroWrist);

    Command zeroElevator = new SimpleCommand("[Elevator] Zero Elevator", elevator::resetEncoder);
    zeroElevator.setRunWhenDisabled(true);
    SmartDashboard.putData(zeroElevator);

    // configure controls
    OI.autoIntakeButton.whenPressed(new IntakeSequence());
    OI.autoEjectButton.whenPressed(new EjectCube());
    OI.stopIntakeButton.whenPressed(new SimpleCommand("Stop intake", intake::stop, intake, intakeArms));

    OI.elevatorExchangeButton.whenPressed(new MoveElevatorToPosition(Tuning.elevatorExchangePosition));

    OI.elevatorSwitchButton.whenPressed(new MoveElevatorToPosition(Tuning.elevatorFrontSwitchPosition));
    OI.elevatorRaiseButton.whenPressed(new MoveElevatorToPosition(Tuning.elevatorScalePosition));
    OI.elevatorFrontScaleButton.whenPressed(new FrontScale());
    OI.elevatorLowerButton.whenPressed(new GroundPosition());

    OI.enableElevatorAxisControlButton.whileHeld(new JoystickElevator());
    OI.enableWristAxisControlButton.whileHeld(new JoystickWrist());


    OI.winchInSlowButton.whileHeld(new SimpleCommand("Winch In Low", () -> winch.set(Tuning.winchInLowVel), winch));

    OI.winchInFastButton.whileHeld(new SimpleCommand("Winch In High", () -> winch.set(Tuning.winchInHighVel), winch));

    // configure camera crosshairs
    new Thread(() -> {
      UsbCamera camera = CameraServer.getInstance().startAutomaticCapture(Tuning.camID);
      camera.setResolution(640, 480);

      CvSink cvSink = CameraServer.getInstance().getVideo();
      CvSource outputStream = CameraServer.getInstance().putVideo(
          "Camera " + Tuning.camID, 640, 480);

      Mat source = new Mat();
      Mat output = new Mat();

      while (!Thread.interrupted()) {
        cvSink.grabFrame(source);
        // Point pt1 = new Point(source.width() / 2 + Tuning.crosshairsSize, source.height() / 2);
        // Point pt2 = new Point(source.width() / 2 - Tuning.crosshairsSize, source.height() / 2);
        // Point pt3 = new Point(source.width() / 2, source.height() / 2 + Tuning.crosshairsSize);
        // Point pt4 = new Point(source.width() / 2, source.height() / 2 - Tuning.crosshairsSize);
        // Imgproc.line(source, pt1, pt2, new Scalar(0, 255, 0), Tuning.crosshairsThicccness);
        // Imgproc.line(source, pt3, pt4, new Scalar(0, 255, 0), Tuning.crosshairsThicccness);
        outputStream.putFrame(source);
      }
    }).start();
  }

  @Override
  public void disabledInit() {
  }

  @Override
  public void autonomousInit() {
    switch (autoPosition.getSelected()) {
      case "Left":
        System.out.println("------------------Left");
        autoCommand = new CommandGroup() {
          {
            if (MatchData.getOwnedSide(GameFeature.SWITCH_NEAR) == OwnedSide.LEFT) {
              System.out.println("------------------Going for the switch");
              addSequential(new AutonomousProfiling(new TrajectorySegment(
                  new Waypoint(0, 0, 0),
                  new Waypoint(120, 50, 0), false)));
              addSequential(new EjectCube());
            } else {
              addSequential(new AutonomousProfiling(new TrajectorySegment(
                  new Waypoint(0, 0, 0),
                  new Waypoint(134, 0, 0), false))); // go straight
            }
            addSequential(new CalibrateWrist());
          }
        };
        break;

      case "Middle":
        System.out.println("------------------Middle");
        autoCommand = new CommandGroup() {
          {
            if (MatchData.getOwnedSide(GameFeature.SWITCH_NEAR) == OwnedSide.LEFT) {
              System.out.println("------------------Going for the LEFT");
              addSequential(new AutonomousProfiling(new TrajectorySegment(
                  new Waypoint(0, 0, 0),
                  new Waypoint(112, -98, 0), false)));
              addSequential(new EjectCube());
            } else if (MatchData.getOwnedSide(GameFeature.SWITCH_NEAR) == OwnedSide.RIGHT) {
              System.out.println("------------------Going for the RIGHT");
              addSequential(new AutonomousProfiling(new TrajectorySegment(
                  new Waypoint(0, 0, 0),
                  new Waypoint(106, 82, 0), false)));
              addSequential(new EjectCube());
            } else {
              DriverStation.reportError("Match data could not get owned switch side, reverting to base auto", false);
              addSequential(new DriveBackward(Tuning.stupidDriveTime));
            }
            addSequential(new CalibrateWrist());
          }
        };
        break;

      case "Right":
        System.out.println("------------------Right");
        autoCommand = new CommandGroup() {
          {
            addSequential(new AutonomousProfiling(new TrajectorySegment(
                new Waypoint(0, 0, 0),
                new Waypoint(134, 0, 0), false)));
            if (MatchData.getOwnedSide(GameFeature.SWITCH_NEAR) == OwnedSide.RIGHT) {
              System.out.println("------------------Going for the RIGHT");
              addSequential(new EjectCube());
            }
            addSequential(new CalibrateWrist());
          }
        };
        break;

      case "Stupid":
        System.out.println("------------------Stupid");
        autoCommand = new CommandGroup() {
          {
            addSequential(new DriveBackward(Tuning.stupidDriveTime));
            addSequential(new CalibrateWrist());
          }
        };
        break;
    }

    autoCommand.start();
  }

  @Override
  public void teleopInit() {
    if (autoCommand != null) {
      autoCommand.cancel();
    }
  }

  @Override
  public void testInit() {
  }

  @Override
  public void robotPeriodic() {
    Scheduler.getInstance().run();
  }

  @Override
  public void disabledPeriodic() {
  }

  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopPeriodic() {
    // for drive override
    if (driveMode.getSelected()) {
      // oh no encoders broke
      emergencyDriveCommand.start();
    } else {
      emergencyDriveCommand.cancel();
    }
  }
}

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
import java.io.File;
import openrio.powerup.MatchData;
import openrio.powerup.MatchData.GameFeature;
import openrio.powerup.MatchData.OwnedSide;
import org.opencv.core.Mat;
import org.team1540.base.adjustables.AdjustableManager;
import org.team1540.base.power.PowerManager;
import org.team1540.base.util.SimpleCommand;
import org.team1540.robot2018.commands.TankDrive;
import org.team1540.robot2018.commands.arms.JoystickArms;
import org.team1540.robot2018.commands.auto.AutonomousProfiling;
import org.team1540.robot2018.commands.auto.AutonomousProfiling.TrajectorySegment;
import org.team1540.robot2018.commands.auto.AutonomousProfilingFast;
import org.team1540.robot2018.commands.auto.AutonomousProfilingFast.TrajectorySegmentFast;
import org.team1540.robot2018.commands.auto.DriveBackward;
import org.team1540.robot2018.commands.auto.TurnLeftBackwardScale;
import org.team1540.robot2018.commands.auto.TurnLeftBackwardSwitch;
import org.team1540.robot2018.commands.elevator.JoystickElevator;
import org.team1540.robot2018.commands.elevator.MoveElevatorToPosition;
import org.team1540.robot2018.commands.elevator.MoveElevatorToPositionNoCurrent;
import org.team1540.robot2018.commands.groups.FrontScale;
import org.team1540.robot2018.commands.groups.GroundPosition;
import org.team1540.robot2018.commands.groups.HoldElevatorWrist;
import org.team1540.robot2018.commands.groups.IntakeSequence;
import org.team1540.robot2018.commands.intake.EjectAuto;
import org.team1540.robot2018.commands.intake.EjectAutoSlow;
import org.team1540.robot2018.commands.intake.EjectCube;
import org.team1540.robot2018.commands.wrist.CalibrateWrist;
import org.team1540.robot2018.commands.wrist.JoystickWrist;
import org.team1540.robot2018.commands.wrist.MoveWristToPosition;
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
  public static CSVProfileManager profiles;

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
    autoPosition.addObject("Middle", "Middle");
    autoPosition.addObject("Left", "Left");
    autoPosition.addObject("Right", "Right");
    autoPosition.addDefault("Right Hook", "Right Hook");
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
    // OI.autoIntakeButton.whileHeld(new SimpleCommand("Intake Arm Open", () -> intakeArms.set
    //     (Tuning.intakeArmSpeed), intakeArms));
    OI.autoIntakeButton.whileHeld(new JoystickArms());

    OI.autoEjectButton.whenPressed(new EjectCube());
    OI.stopIntakeButton.whenPressed(new SimpleCommand("Stop intake", intake::stop, intake,
        intakeArms));

    OI.elevatorExchangeButton.whenPressed(new MoveElevatorToPosition(Tuning
        .elevatorExchangePosition));

    OI.elevatorSwitchButton.whenPressed(new MoveElevatorToPosition(Tuning
        .elevatorFrontSwitchPosition));
    // OI.elevatorRaiseButton.whenPressed(new MoveElevatorToPosition(Tuning.elevatorScalePosition));
    OI.elevatorFrontScaleButton.whenPressed(new FrontScale());
    OI.elevatorLowerButton.whenPressed(new GroundPosition());

    OI.wristFwdButton.whenPressed(new CalibrateWrist());
    OI.wrist45DegButton.whenPressed(new MoveWristToPosition(Tuning.wrist45FwdPosition));
    OI.wristBackButton.whenPressed(new MoveWristToPosition(Tuning.wristBackPosition));

    OI.enableElevatorAxisControlButton.whileHeld(new JoystickElevator());
    OI.enableWristAxisControlButton.whileHeld(new JoystickWrist());

    OI.holdElevatorWristButton.whenPressed(new HoldElevatorWrist());


    OI.winchInSlowButton.whileHeld(new SimpleCommand("Winch In Low", () -> winch.set(Tuning
        .winchInLowVel), winch));

    OI.winchInFastButton.whileHeld(new SimpleCommand("Winch In High", () -> winch.set(Tuning
        .winchInHighVel), winch));

    // OI.climbSequenceButton.whenPressed(new ClimbSequence());

    // configure camera crosshairs
    new Thread(() -> {
      UsbCamera camera = CameraServer.getInstance().startAutomaticCapture(Tuning.camID);
      camera.setResolution(320, 240);

      CvSink cvSink = CameraServer.getInstance().getVideo();
      CvSource outputStream = CameraServer.getInstance().putVideo(
          "Camera " + Tuning.camID, 320, 240);

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


    // initialize profiles
    // unlike other static fields, initialized here because there's a high likelihood of it throwing
    // an exception and exceptions thrown during static initialization are not fun.
    profiles = new CSVProfileManager(new File("/home/lvuser/profiles"));
  }

  @Override
  public void disabledInit() {
  }

  @Override
  public void autonomousInit() {
    elevator.resetEncoder();
    switch (autoPosition.getSelected()) {
      case "Left":
        System.out.println("Left Auto Selected");
        autoCommand = new CommandGroup() {
          {
            if (MatchData.getOwnedSide(GameFeature.SWITCH_NEAR) == OwnedSide.LEFT) {
              System.out.println("Going for Left Switch");
              addSequential(new AutonomousProfiling(new TrajectorySegment(
                  new Waypoint(0, 0, 0),
                  new Waypoint(120, 50, 0), false)));
              addSequential(new MoveWristToPosition(Tuning.wrist45BackPosition));
              addSequential(new EjectAuto());
            } else {
              System.out.println("Just Crossing the Line");
              addSequential(new AutonomousProfiling(new TrajectorySegment(
                  new Waypoint(0, 0, 0),
                  new Waypoint(134, 0, 0), false))); // go straight
            }
            addSequential(new CalibrateWrist());
          }
        };
        break;

      case "Middle":
        System.out.println("Middle Auto Selected");
        autoCommand = new CommandGroup() {
          {
            if (MatchData.getOwnedSide(GameFeature.SWITCH_NEAR) == OwnedSide.LEFT) {
              System.out.println("Going for Left Switch");
              addSequential(new AutonomousProfiling(new TrajectorySegment(
                  new Waypoint(0, 0, 0),
                  new Waypoint(102, -123, 0), false)));
              addSequential(new MoveWristToPosition(Tuning.wrist45BackPosition));
              addSequential(new EjectAuto());
            } else if (MatchData.getOwnedSide(GameFeature.SWITCH_NEAR) == OwnedSide.RIGHT) {
              System.out.println("Going for Right Switch");
              addSequential(new AutonomousProfiling(new TrajectorySegment(
                  new Waypoint(0, 0, 0),
                  new Waypoint(106, 85, 0), false)));
              addSequential(new MoveWristToPosition(Tuning.wrist45BackPosition));
              addSequential(new EjectAuto());
            } else {
              DriverStation.reportError("Match data could not get owned switch side, reverting to"
                  + " base auto", false);
              addSequential(new DriveBackward(Tuning.stupidDriveTime));
            }
            addSequential(new CalibrateWrist());
          }
        };
        break;

      case "Right":
        System.out.println("Right Auto Selected");
        autoCommand = new CommandGroup() {
          {
            addSequential(new AutonomousProfiling(new TrajectorySegment(
                new Waypoint(0, 0, 0),
                new Waypoint(134, 0, 0), false)));
            if (MatchData.getOwnedSide(GameFeature.SWITCH_NEAR) == OwnedSide.RIGHT) {
              System.out.println("Going for Right Switch");
              addSequential(new MoveWristToPosition(Tuning.wrist45BackPosition));
              addSequential(new EjectAuto());
            }
            addSequential(new CalibrateWrist());
          }
        };
        break;

      case "Right Hook":
        System.out.println("Right Hook Selected");
        autoCommand = new CommandGroup() {
          {
            if (MatchData.getOwnedSide(GameFeature.SCALE) == OwnedSide.RIGHT) {
              addSequential(new AutonomousProfilingFast(new TrajectorySegmentFast(
                  new Waypoint(0, 0, 0),
                  new Waypoint(284, 0, 0), false)));
              System.out.println("Going for Right Scale");
              addParallel(new MoveWristToPosition(Tuning.wristTransitPosition));
              addSequential(new TurnLeftBackwardScale(0.8));
              addSequential(new MoveElevatorToPositionNoCurrent(Tuning.elevatorScalePosition));
              addSequential(new MoveWristToPosition(Tuning.wristBackPosition));
              addSequential(new EjectAutoSlow());
            } else if (MatchData.getOwnedSide(GameFeature.SWITCH_NEAR) == OwnedSide.RIGHT) {
              addSequential(new AutonomousProfiling(new TrajectorySegment(
                  new Waypoint(0, 0, 0),
                  new Waypoint(114, 0, 0), false)));
              System.out.println("Going for Right Switch");
              addSequential(new TurnLeftBackwardSwitch(1.6));
              addSequential(new MoveWristToPosition(Tuning.wrist45BackPosition));
              addSequential(new EjectAuto());
            } else {
              addSequential(new AutonomousProfiling(new TrajectorySegment(
                  new Waypoint(0, 0, 0),
                  new Waypoint(114, 0, 0), false)));
            }
            addSequential(new CalibrateWrist());
            addSequential(new GroundPosition());
          }
        };
        break;

      case "Stupid":
        System.out.println("Stupid Auto Selected");
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
    SmartDashboard.putNumber("[Elevator] Position", elevator.getPosition());
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

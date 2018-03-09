package org.team1540.robot2018;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import openrio.powerup.MatchData;
import openrio.powerup.MatchData.GameFeature;
import openrio.powerup.MatchData.OwnedSide;
import org.opencv.core.Mat;
import org.team1540.base.adjustables.AdjustableManager;
import org.team1540.base.power.PowerManager;
import org.team1540.base.util.SimpleCommand;
import org.team1540.robot2018.commands.TankDrive;
import org.team1540.robot2018.commands.auto.DriveBackward;
import org.team1540.robot2018.commands.auto.sequences.NoCubeProfileAuto;
import org.team1540.robot2018.commands.auto.sequences.SingleCubeAuto;
import org.team1540.robot2018.commands.elevator.JoystickElevator;
import org.team1540.robot2018.commands.elevator.MoveElevatorToPosition;
import org.team1540.robot2018.commands.groups.FrontScale;
import org.team1540.robot2018.commands.groups.GroundPosition;
import org.team1540.robot2018.commands.groups.HoldElevatorWrist;
import org.team1540.robot2018.commands.groups.IntakeSequence;
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

  private Command emergencyDriveCommand = new TankDrive();


  private SendableChooser<AutoPosition> autoPosition;
  private SendableChooser<Boolean> driveMode;

  private AutoSequence autoCommand;

  @Override
  public void robotInit() {
    // disable unused things
    LiveWindow.disableAllTelemetry();
    PowerManager.getInstance().interrupt();

    // configure SmartDashboard
    AdjustableManager.getInstance().add(new Tuning());
    autoPosition = new SendableChooser<>();
    for (AutoPosition type : AutoPosition.values()) {
      // Safety just cuz I'm not 100% sure on how the default bit works
      if (type == Tuning.defaultAutoPosition) {
        autoPosition.addDefault(type.name(), type);
      } else {
        autoPosition.addObject(type.name(), type);
      }
    }

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
    OI.autoIntakeButton.whileHeld(new SimpleCommand("Intake Arm Open", () -> intakeArms.set
        (Tuning.intakeArmSpeed), intakeArms));
    OI.autoEjectButton.whenPressed(new EjectCube());
    OI.stopIntakeButton.whenPressed(new SimpleCommand("Stop intake", intake::stop, intake,
        intakeArms));

    OI.elevatorExchangeButton.whenPressed(new MoveElevatorToPosition(Tuning
        .elevatorExchangePosition));

    OI.elevatorSwitchButton.whenPressed(new MoveElevatorToPosition(Tuning
        .elevatorFrontSwitchPosition));
    OI.elevatorRaiseButton.whenPressed(new MoveElevatorToPosition(Tuning.elevatorScalePosition));
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
    OI.climbSequenceButton.whenPressed(new MoveElevatorToPosition(Tuning.elevatorRungPosition));

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
  }

  @Override
  public void disabledInit() {
  }

  @Override
  public void autonomousInit() {
    elevator.resetEncoder();

    // Straight equality should be fine since only one of these is running at a time

    switch (autoPosition.getSelected()) {
      case LEFT:
        System.out.println("Left Auto Selected");
        if (MatchData.getOwnedSide(GameFeature.SWITCH_NEAR) == OwnedSide.LEFT) {
          System.out.println("Going for Left Switch");
          autoCommand = AutoSequence.LEFT_TO_LEFT_SWICH;
        } else {
          System.out.println("Just Crossing the Line");
          autoCommand = AutoSequence.CROSS_LINE;
        }
        break;
      case MIDDLE:
        System.out.println("Middle Auto Selected");
        if (MatchData.getOwnedSide(GameFeature.SWITCH_NEAR) == OwnedSide.LEFT) {
          System.out.println("Going for Left Switch");
          autoCommand = AutoSequence.MIDDLE_TO_LEFT_SWITCH;
        } else if (MatchData.getOwnedSide(GameFeature.SWITCH_NEAR) == OwnedSide.RIGHT) {
          System.out.println("Going for Right Switch");
          autoCommand = AutoSequence.MIDDLE_TO_RIGHT_SWITCH;
        } else {
          DriverStation.reportError("Match data could not get owned switch side, reverting to "
              + "base auto", false);
          autoCommand = AutoSequence.STUPID;
        }
        break;
      case RIGHT:
        System.out.println("Right Auto Selected");
        if (MatchData.getOwnedSide(GameFeature.SWITCH_NEAR) == OwnedSide.RIGHT) {
          System.out.println("Going for Right Switch");
          autoCommand = AutoSequence.RIGHT_TO_RIGHT_SWITCH;
        } else {
          System.out.println("Just Crossing the Line");
          autoCommand = AutoSequence.CROSS_LINE;
        }
        break;

      case STUPID:
        System.out.println("Stupid Auto Selected");
        autoCommand = AutoSequence.STUPID;
        break;
    }

    autoCommand.command.start();
  }

  @Override
  public void teleopInit() {
    if (autoCommand != null) {
      autoCommand.command.cancel();
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

  public enum AutoPosition {
    CROSS_LINE, LEFT, MIDDLE, RIGHT, STUPID
  }

  public enum AutoSequence {

    CROSS_LINE(new NoCubeProfileAuto("go_straight")),
    LEFT_TO_LEFT_SWICH(new SingleCubeAuto("left_to_left_switch")),
    MIDDLE_TO_LEFT_SWITCH(new SingleCubeAuto("middle_to_left_switch")),
    MIDDLE_TO_RIGHT_SWITCH(new SingleCubeAuto("middle_to_right_switch")),
    RIGHT_TO_RIGHT_SWITCH(new SingleCubeAuto("go_straight")),
    STUPID(new DriveBackward(Tuning.stupidDriveTime));

    public final Command command;

    AutoSequence(Command autoCommand) {
      this.command = autoCommand;
    }
  }
}

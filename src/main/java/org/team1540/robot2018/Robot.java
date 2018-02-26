package org.team1540.robot2018;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.base.adjustables.AdjustableManager;
import org.team1540.base.util.SimpleCommand;
import org.team1540.robot2018.commands.auto.StraightAuto;
import org.team1540.robot2018.commands.climber.AlignClimber;
import org.team1540.robot2018.commands.elevator.JoystickElevator;
import org.team1540.robot2018.commands.elevator.MoveElevatorToPosition;
import org.team1540.robot2018.commands.groups.FrontScale;
import org.team1540.robot2018.commands.groups.GroundPosition;
import org.team1540.robot2018.commands.groups.IntakeSequence;
import org.team1540.robot2018.commands.intake.EjectCube;
import org.team1540.robot2018.commands.intake.OpenArms;
import org.team1540.robot2018.commands.wrist.JoystickWrist;
import org.team1540.robot2018.subsystems.ClimberTapeMeasure;
import org.team1540.robot2018.subsystems.ClimberTurret;
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
  public static final ClimberTurret turret = new ClimberTurret();
  public static final ClimberTapeMeasure tape = new ClimberTapeMeasure();
  public static final ClimberWinch winch = new ClimberWinch();

  private SendableChooser<String> side = new SendableChooser<>();

  @Override
  public void robotInit() {
    AdjustableManager.getInstance().add(new Tuning());
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

    OI.tapeOutButton.whileHeld(new SimpleCommand("Tape out", () -> tape.set(Tuning.tapeOutVel), tape));
    OI.tapeInSlowButton.whileHeld(new SimpleCommand("Tape in", () -> tape.set(Tuning.tapeInLowVel), tape));

    OI.elevatorSwitchButton.whenPressed(new MoveElevatorToPosition(Tuning.elevatorFrontSwitchPosition));
    OI.elevatorRaiseButton.whenPressed(new MoveElevatorToPosition(Tuning.elevatorScalePosition));
    OI.elevatorFrontScaleButton.whenPressed(new FrontScale());
    OI.elevatorLowerButton.whenPressed(new GroundPosition());

    OI.enableElevatorAxisControlButton.whileHeld(new JoystickElevator());

    /*
    The left trigger changes the mode of the right joystick. Normally (the left trigger is not
    pressed) the JoystickWrist command is run and the right stick controls the wrist. When the left
    trigger is pressed, the JoystickWrist command does not run, while the AlignClimber command does;
    therefore, the right stick controls the climber turret.

    The enableWristOrTurretAxisControlButton activates when the joystick is outside of its deadzone, and only
    runs the joystick control commands when the joystick is in fact being moved. This allows other
    commands that require the wrist (or turret) to run when the joystick is not being moved.
    */
    // TODO: Climber turret control only activates when the Y axis is out of its deadzone, but the pan uses the X joystick axis
    // TODO: This logic depends on a very specific mapping of joysticks, is there a better way of doing this?
    OI.enableWristOrTurretAxisControlButton.whileHeld(new ConditionalCommand(new AlignClimber(), new JoystickWrist()) {
      @Override
      protected boolean condition() {
        return OI.changeWristToTurretButton.get();
      }
    });

    OI.winchInSlowButton.whileHeld(new SimpleCommand("Winch In Low", () -> {
      tape.set(Tuning.tapeInLowVel);
      winch.set(Tuning.winchInLowVel);
    }, tape, winch));

    OI.winchInFastButton.whileHeld(new SimpleCommand("Winch In High", () -> {
      tape.set(Tuning.tapeInHighVel);
      winch.set(Tuning.winchInHighVel);
    }, tape, winch));

    // configure SmartDashboard
    Command zeroWrist = new SimpleCommand("[Elevator] Zero Wrist", wrist::resetEncoder);
    zeroWrist.setRunWhenDisabled(true);
    SmartDashboard.putData(zeroWrist);

    Command zeroElevator = new SimpleCommand("[Elevator] Zero Elevator", elevator::resetEncoder);
    zeroElevator.setRunWhenDisabled(true);
    SmartDashboard.putData(zeroElevator);

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
    turret.disableServos();
  }

  @Override
  public void autonomousInit() {
    if (side.getSelected().equals(DriverStation.getInstance().getGameSpecificMessage().substring(0, 1))) {
      new StraightAuto().start();
    }
  }

  @Override
  public void teleopInit() {
    turret.enableServos();
    turret.init();
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
  }
}

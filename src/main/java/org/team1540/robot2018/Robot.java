package org.team1540.robot2018;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.io.File;
import java.util.function.BooleanSupplier;
import openrio.powerup.MatchData;
import openrio.powerup.MatchData.GameFeature;
import openrio.powerup.MatchData.OwnedSide;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.team1540.base.adjustables.AdjustableManager;
import org.team1540.base.power.PowerManager;
import org.team1540.base.util.SimpleCommand;
import org.team1540.robot2018.commands.TankDrive;
import org.team1540.robot2018.commands.auto.DriveTimed;
import org.team1540.robot2018.commands.auto.sequences.ProfileDoubleScaleAuto;
import org.team1540.robot2018.commands.auto.sequences.ProfileScaleAuto;
import org.team1540.robot2018.commands.auto.sequences.SimpleProfileAuto;
import org.team1540.robot2018.commands.auto.sequences.SingleCubeSwitchAuto;
import org.team1540.robot2018.commands.auto.sequences.SwitchDoubleCube;
import org.team1540.robot2018.subsystems.Arms;
import org.team1540.robot2018.subsystems.ClimberTape;
import org.team1540.robot2018.subsystems.ClimberWinch;
import org.team1540.robot2018.subsystems.DriveTrain;
import org.team1540.robot2018.subsystems.Elevator;
import org.team1540.robot2018.subsystems.Intake;
import org.team1540.robot2018.subsystems.Wrist;

public class Robot extends IterativeRobot {
  public static final DriveTrain drivetrain = new DriveTrain();
  public static final Intake intake = new Intake();
  public static final Arms arms = new Arms();
  public static final Elevator elevator = new Elevator();
  public static final Wrist wrist = new Wrist();
  public static final ClimberWinch winch = new ClimberWinch();
  public static final ClimberTape tape = new ClimberTape();
  public static CSVProfileManager profiles;
  public static AHRS navx = new AHRS(Port.kMXP);

  private Command emergencyDriveCommand = new TankDrive();

  private SendableChooser<AutoMode> autoPosition;
  private SendableChooser<Boolean> driveMode;

  private Command autoCommand;

  @Override
  public void robotInit() {
    // disable unused things
    LiveWindow.disableAllTelemetry();
    PowerManager.getInstance().interrupt();
    // PowerManager.getInstance().setUpdateDelay(40);

    // TODO: Move auto chooser into command
    AdjustableManager.getInstance().add(new Tuning());
    autoPosition = new SendableChooser<>();
    for (AutoMode autoMode : AutoMode.values()) {
      autoPosition.addObject(autoMode.name, autoMode);
    }

    SmartDashboard.putData("Auto mode", autoPosition);

    driveMode = new SendableChooser<>();
    driveMode.addDefault("PID Drive", false);
    driveMode.addObject("Manual Override", true);
    SmartDashboard.putData("[Drivetrain] ***** DRIVE OVERRIDE *****", driveMode);

    // TODO: Move SmartDashboard commands to separate class
    Command zeroWrist = new SimpleCommand("[Wrist] Zero Wrist", wrist::resetEncoder);
    zeroWrist.setRunWhenDisabled(true);
    SmartDashboard.putData(zeroWrist);

    Command zeroElevator = new SimpleCommand("[Elevator] Zero Elevator", elevator::resetEncoder);
    zeroElevator.setRunWhenDisabled(true);
    SmartDashboard.putData(zeroElevator);

    Command zeroDrivetrain = new SimpleCommand("[Drivetrain] Zero Drivetrain", drivetrain::zeroEncoders);
    zeroDrivetrain.setRunWhenDisabled(true);
    SmartDashboard.putData(zeroDrivetrain);

    UsbCamera overheadCam = CameraServer.getInstance().startAutomaticCapture(RobotMap.OVERHEAD_CAM_ID);
    overheadCam.setResolution(128, 73);
    overheadCam.setFPS(30);

    UsbCamera turretCam = CameraServer.getInstance().startAutomaticCapture(RobotMap.TURRET_CAM_ID);
    turretCam.setResolution(128, 73);
    overheadCam.setFPS(30);


    Command refreshProfiles = new SimpleCommand("[MotionP] Refresh Motion Profiles",
        () -> profiles = new CSVProfileManager(new File("/home/lvuser/profiles")));
    refreshProfiles.setRunWhenDisabled(true);
    SmartDashboard.putData(refreshProfiles);

    // initialize profiles
    // unlike other static fields, initialized here because there's a high likelihood of it throwing
    // an exception and exceptions thrown during static initialization are not fun.
    profiles = new CSVProfileManager(new File("/home/lvuser/profiles"));

    SmartDashboard.putData(drivetrain);
    SmartDashboard.putData(PowerManager.getInstance());
  }

  @Override
  public void disabledInit() {
    Robot.drivetrain.configTalonsForVelocity();
    if (autoCommand != null) {
      autoCommand.cancel();
    }
  }

  @Override
  public void autonomousInit() {
    // TODO: Move auto logic into command
    // PowerManager.getInstance().setRunning(false);
    elevator.resetEncoder();
    wrist.setSensorPosition(0);
    autoCommand = findCommand(autoPosition.getSelected().root);
    if (autoCommand != null) {
      autoCommand.start();
    } else {
      DriverStation.reportError("Could not find auto command", false);
    }
  }

  // Depth first search
  private Command findCommand(DecisionNode root) {
    if (root.condition.getAsBoolean()) {
      if (root.profile != null) {
        if (root.message != null) {
          root.message.displayMessage();
        }
        return root.profile.autoCommand;
      } else {
        if (root.children != null) {
          for (DecisionNode node : root.children) {
            if (node != null) {
              Command command = findCommand(node);
              if (command != null) {
                return command;
              }
            }
          }
        }
      }
    }
    return null;
  }

  @Override
  public void teleopInit() {
    // PowerManager.getInstance().setRunning(true);
    if (autoCommand != null) {
      autoCommand.cancel();
    }
    Robot.drivetrain.configTalonsForVelocity();
  }

  @Override
  public void testInit() {
  }

  @Override
  public void robotPeriodic() {
    SmartDashboard.putNumber("Elevator postion", elevator.getPosition());
    double leftDistance = drivetrain.getLeftPosition();
    double rightDistance = -drivetrain.getRightPosition();

    SmartDashboard.putNumber("Left Distance", leftDistance);
    SmartDashboard.putNumber("Right Distance", rightDistance);

    SmartDashboard.putNumber("Gyro Angle", Robot.navx.getAngle());
    SmartDashboard.putData(Scheduler.getInstance());
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
    // TODO: Add a command chooser to ROOSTER
    // for drive override
    if (driveMode.getSelected()) {
      // oh no encoders broke
      emergencyDriveCommand.start();
    } else {
      emergencyDriveCommand.cancel();
    }
  }

  /**
   * Set of all available autonomous modes. To create a new autonomous mode, create a new enum and
   * identify the root {@link DecisionNode} from which to perform a depth first tree traversal in
   * search of a leaf.
   */
  private enum AutoMode {

    LEFT_DOUBLE_SCALE_THEN_SWITCH(new DecisionNode(new DecisionNode[]{
        new DecisionNode(Posession.SCALE_OWNED_LEFT, AutonomousRoutine.LEFT_DOUBLE_SCALE_THEN_SWITCH),
        new DecisionNode(Posession.SCALE_OWNED_RIGHT, new DecisionNode[]{
            new DecisionNode(Posession.SWITCH_OWNED_LEFT, AutonomousRoutine.LEFT_HOOK),
            new DecisionNode(Posession.SWITCH_OWNED_RIGHT, AutonomousRoutine.GO_STRAIGHT),
            new DecisionNode(Posession.SWITCH_NO_DATA, AutonomousRoutine.GO_STRAIGHT).setMessage(Posession.SWITCH_NO_DATA.message)
        }),
        new DecisionNode(Posession.SCALE_NO_DATA, AutonomousRoutine.GO_STRAIGHT).setMessage(Posession.SCALE_NO_DATA.message)
    })),
    LEFT_SCALE_THEN_SWITCH(new DecisionNode(new DecisionNode[]{
        new DecisionNode(Posession.SCALE_OWNED_LEFT, AutonomousRoutine.LEFT_SCALE),
        new DecisionNode(Posession.SCALE_OWNED_RIGHT, new DecisionNode[]{
            new DecisionNode(Posession.SWITCH_OWNED_LEFT, AutonomousRoutine.LEFT_HOOK),
            new DecisionNode(Posession.SWITCH_OWNED_RIGHT, AutonomousRoutine.GO_STRAIGHT),
            new DecisionNode(Posession.SWITCH_NO_DATA, AutonomousRoutine.GO_STRAIGHT).setMessage(Posession.SWITCH_NO_DATA.message)
        }),
        new DecisionNode(Posession.SCALE_NO_DATA, AutonomousRoutine.GO_STRAIGHT).setMessage(Posession.SCALE_NO_DATA.message)
    })),
    LEFT_SCALE_NO_SWTICH(new DecisionNode(new DecisionNode[]{
        new DecisionNode(Posession.SCALE_OWNED_LEFT, AutonomousRoutine.LEFT_SCALE),
        new DecisionNode(Posession.SCALE_OWNED_RIGHT, AutonomousRoutine.GO_STRAIGHT),
        new DecisionNode(Posession.SCALE_NO_DATA, AutonomousRoutine.GO_STRAIGHT).setMessage(Posession.SCALE_NO_DATA.message)
    })),
    LEFT_DOUBLE_SCALE_NO_SWITCH(new DecisionNode(new DecisionNode[]{
        new DecisionNode(Posession.SCALE_OWNED_LEFT, AutonomousRoutine.LEFT_SCALE_NO_SWITCH),
        new DecisionNode(Posession.SCALE_OWNED_RIGHT, AutonomousRoutine.GO_STRAIGHT),
        new DecisionNode(Posession.SCALE_NO_DATA, AutonomousRoutine.GO_STRAIGHT).setMessage(Posession.SCALE_NO_DATA.message)
    })),
    LEFT_HOOK_SWITCH_THEN_DOUBLE_SCALE(new DecisionNode(new DecisionNode[]{
        new DecisionNode(Posession.SWITCH_OWNED_LEFT, AutonomousRoutine.LEFT_HOOK),
        new DecisionNode(Posession.SCALE_OWNED_LEFT, AutonomousRoutine.LEFT_SCALE_STRAIGHT),
        new DecisionNode(Posession.SWITCH_NO_DATA, AutonomousRoutine.GO_STRAIGHT).setMessage(Posession.SWITCH_NO_DATA.message),
        new DecisionNode(Posession.SCALE_NO_DATA, AutonomousRoutine.GO_STRAIGHT).setMessage(Posession.SCALE_NO_DATA.message)
    })),
    LEFT_HOOK_SWITCH_THEN_SCALE(new DecisionNode(new DecisionNode[]{
        new DecisionNode(Posession.SWITCH_OWNED_LEFT, AutonomousRoutine.LEFT_HOOK),
        new DecisionNode(Posession.SCALE_OWNED_LEFT, AutonomousRoutine.LEFT_SCALE_STRAIGHT),
        new DecisionNode(Posession.SCALE_OWNED_RIGHT, AutonomousRoutine.GO_STRAIGHT),
        new DecisionNode(Posession.SWITCH_NO_DATA, AutonomousRoutine.GO_STRAIGHT).setMessage(Posession.SWITCH_NO_DATA.message),
        new DecisionNode(Posession.SCALE_NO_DATA, AutonomousRoutine.GO_STRAIGHT).setMessage(Posession.SCALE_NO_DATA.message)
    })),
    MIDDLE(new DecisionNode(new DecisionNode[]{
        new DecisionNode(Posession.SWITCH_OWNED_LEFT, AutonomousRoutine.MIDDLE_TO_LEFT_SWITCH),
        new DecisionNode(Posession.SWITCH_OWNED_RIGHT, AutonomousRoutine.MIDDLE_TO_RIGHT_SWITCH),
        new DecisionNode(Posession.SWITCH_NO_DATA, AutonomousRoutine.GO_STRAIGHT).setMessage(Posession.SWITCH_NO_DATA.message)
    })),
    CENTER_DOUBLE_CUBE(new DecisionNode(new DecisionNode[]{
        new DecisionNode(Posession.SWITCH_OWNED_LEFT, AutonomousRoutine.SWITCH_DOUBLE_CUBE_LEFT),
        new DecisionNode(Posession.SWITCH_OWNED_RIGHT, AutonomousRoutine.SWITCH_DOUBLE_CUBE_RIGHT),
        new DecisionNode(Posession.SWITCH_NO_DATA, AutonomousRoutine.GO_STRAIGHT).setMessage(Posession.SWITCH_NO_DATA.message)
    })),
    RIGHT_HOOK_SWITCH(new DecisionNode(new DecisionNode[]{
        new DecisionNode(Posession.SWITCH_OWNED_RIGHT, AutonomousRoutine.RIGHT_HOOK),
        new DecisionNode(Posession.SWITCH_OWNED_LEFT, AutonomousRoutine.GO_STRAIGHT),
        new DecisionNode(Posession.SWITCH_NO_DATA, AutonomousRoutine.GO_STRAIGHT).setMessage(Posession.SWITCH_NO_DATA.message)
    })),
    RIGHT_SCALE_THEN_SWITCH(new DecisionNode(new DecisionNode[]{
        new DecisionNode(Posession.SCALE_OWNED_RIGHT, AutonomousRoutine.RIGHT_SCALE),
        new DecisionNode(Posession.SCALE_OWNED_LEFT, new DecisionNode[]{
            new DecisionNode(Posession.SWITCH_OWNED_RIGHT, AutonomousRoutine.RIGHT_HOOK),
            new DecisionNode(Posession.SWITCH_OWNED_LEFT, AutonomousRoutine.GO_STRAIGHT),
            new DecisionNode(Posession.SWITCH_NO_DATA, AutonomousRoutine.GO_STRAIGHT).setMessage(Posession.SWITCH_NO_DATA.message)
        }),
        new DecisionNode(Posession.SCALE_NO_DATA, AutonomousRoutine.GO_STRAIGHT).setMessage(Posession.SCALE_NO_DATA.message)
    })),
    CROSS_LINE(new DecisionNode(AutonomousRoutine.GO_STRAIGHT)),
    STUPID(new DecisionNode(AutonomousRoutine.DRIVE_TIMED));


    private final String name;
    private final DecisionNode root;

    AutoMode(String name, DecisionNode root) {
      this.name = name;
      this.root = root;
    }

    AutoMode(DecisionNode root) {
      StringBuilder nameMaker = new StringBuilder();
      for (String word : this.name().split("_")) {
        nameMaker.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase()).append(" ");
      }
      name = nameMaker.toString().trim();
      this.root = root;
    }

  }

  private enum Posession {

    SCALE_OWNED_LEFT(() -> (MatchData.getOwnedSide(GameFeature.SCALE) == OwnedSide.LEFT)),
    SCALE_OWNED_RIGHT(() -> (MatchData.getOwnedSide(GameFeature.SCALE) == OwnedSide.RIGHT)),
    SWITCH_OWNED_LEFT(() -> (MatchData.getOwnedSide(GameFeature.SWITCH_NEAR) == OwnedSide.LEFT)),
    SWITCH_OWNED_RIGHT(() -> (MatchData.getOwnedSide(GameFeature.SWITCH_NEAR) == OwnedSide.RIGHT)),
    SCALE_NO_DATA(() -> (MatchData.getOwnedSide(GameFeature.SCALE)
        == OwnedSide.UNKNOWN), "Could not get scale data", true),
    SWITCH_NO_DATA(() -> (MatchData.getOwnedSide(GameFeature.SWITCH_NEAR)
        == OwnedSide.UNKNOWN), "Could not get switch data", true);

    @NotNull
    private final BooleanSupplier condition;
    @Nullable
    private final Message message;

    Posession(@NotNull BooleanSupplier condition) {
      this.condition = condition;
      this.message = null;
    }

    Posession(@NotNull BooleanSupplier condition, @Nullable String message, boolean isError) {
      this.condition = condition;
      this.message = new Message(message, isError);
    }

  }

  private enum AutonomousRoutine {
    GO_STRAIGHT(new SimpleProfileAuto("go_straight")),
    LEFT_HOOK(new SingleCubeSwitchAuto("left_hook")),
    LEFT_SCALE(new ProfileScaleAuto("left_scale")),
    LEFT_SCALE_NO_SWITCH(new ProfileDoubleScaleAuto("left_scale", "left_scale_back_to_switch", "left_switch_back_to_scale")),
    LEFT_DOUBLE_SCALE_THEN_SWITCH(new ProfileDoubleScaleAuto("left_scale_straight", "left_scale_straight_back_to_switch", "left_switch_straight_back_to_scale")),
    LEFT_SCALE_STRAIGHT(new ProfileDoubleScaleAuto("left_scale_straight", "left_scale_straight_back_to_switch", "left_switch_straight_back_to_scale")),
    MIDDLE_TO_LEFT_SWITCH(new SingleCubeSwitchAuto("middle_to_left_switch")),
    MIDDLE_TO_RIGHT_SWITCH(new SingleCubeSwitchAuto("middle_to_right_switch")),
    SWITCH_DOUBLE_CUBE_LEFT(new SwitchDoubleCube("left")),
    SWITCH_DOUBLE_CUBE_RIGHT(new SwitchDoubleCube("right")),
    RIGHT_HOOK(new SingleCubeSwitchAuto("right_hook")),
    RIGHT_SCALE(new ProfileScaleAuto("right_scale")),
    DRIVE_TIMED(new DriveTimed(ControlMode.PercentOutput, Tuning.stupidDriveTime, Tuning.stupidDrivePercent));

    private final Command autoCommand;
    private final String defaultMessage;

    AutonomousRoutine(Command autoCommand) {
      this.autoCommand = autoCommand;
      this.defaultMessage = "Running routine " + this.name();
    }

    AutonomousRoutine(Command autoCommand, String defaultMessage) {
      this.autoCommand = autoCommand;
      this.defaultMessage = defaultMessage;
    }

  }

  /**
   * Class for use in a tree structure for deciding what autonomous routine to execute. Each
   * decision node has a {@link #condition} that determines if it's possible to continue pathing
   * down that node. Each decision node either has children, or if it is a leaf, an
   * AutonomousRoutine.
   */
  private static class DecisionNode {

    @NotNull
    final BooleanSupplier condition;
    @Nullable
    private Message message = null;
    @Nullable
    final Robot.AutonomousRoutine profile;
    @Nullable
    final DecisionNode[] children;

    public DecisionNode(Posession posession, @NotNull Robot.AutonomousRoutine profile) {
      this.condition = posession.condition;
      this.profile = profile;
      this.children = null;
      message = new Message(profile.defaultMessage);
    }

    public DecisionNode(@NotNull Robot.AutonomousRoutine profile) {
      this.condition = () -> (true);
      this.children = null;
      this.profile = profile;
    }

    public DecisionNode(Posession posession, @NotNull DecisionNode[] children) {
      this.condition = posession.condition;
      this.children = children;
      this.profile = null;
    }

    public DecisionNode(@NotNull DecisionNode[] children) {
      this.condition = () -> (true);
      this.children = children;
      this.profile = null;
    }

    public DecisionNode setMessage(Message message) {
      this.message = message;
      return this;
    }
  }

  private static class Message {
    private String message;
    private boolean isError = false;

    public Message(String message) {
      this.message = message;
    }

    public Message(String message, boolean isError) {
      this.message = message;
      this.isError = isError;
    }

    public void displayMessage() {
      if (isError) {
        System.err.println(message);
      } else {
        System.out.println(message);
      }
    }
  }
}

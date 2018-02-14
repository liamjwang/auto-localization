package org.team1540.robot2018;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.base.Utilities;
import org.team1540.base.adjustables.AdjustableManager;
import org.team1540.base.adjustables.Telemetry;
import org.team1540.base.adjustables.Tunable;
import org.team1540.base.util.SimpleCommand;
import org.team1540.base.wrappers.ChickenController;
import org.team1540.base.wrappers.ChickenTalon;

public class ElevatorTuningRobot extends IterativeRobot {
  @Tunable("Ramp")
  public double clr;
  @Tunable("Peak out fwd")
  private double pof;
  @Tunable("Peak out reverse")
  private double por;

  private enum TuningMode {JOYSTICK, PID, PID_JOYSTICK, PID_MTP}

  private SendableChooser<TuningMode> chooser = new SendableChooser<>();
  @Tunable("P")
  public double p;
  @Tunable("I")
  public double i;
  @Tunable("D")
  public double d;
  @Tunable("PID Mode")
  public boolean usingPid;
  @Tunable("PID Target")
  public double setpoint;

  @Tunable("Invert Motor 1")
  public boolean invert1 = false;
  @Tunable("Invert Motor 2")
  public boolean invert2 = false;

  @Tunable("Invert Sensor")
  public boolean invertSensor;

  @Tunable("Joystick Multiplier")
  public double joystickMultiplier = 0;

  private Joystick joystick = new Joystick(0);

  private ChickenTalon motor1 = new ChickenTalon(14);
  private ChickenController motor2 = new ChickenTalon(13);
  @Telemetry("Joystick Position")
  public double joystickPosition = 0;
  @Override
  public void robotInit() {
    // zeroing command
    AdjustableManager.getInstance().add(this);
    joystickPosition = motor1.getSelectedSensorPosition();


    chooser.addDefault("Joystick", TuningMode.JOYSTICK);
    chooser.addObject("PID With Setpoint", TuningMode.PID);
    chooser.addObject("PID With Joystick", TuningMode.PID_JOYSTICK);
    chooser.addObject("PID Move to Position", TuningMode.PID_MTP);

    SmartDashboard.putData(chooser);
    SmartDashboard.putData("Zero Position", new SimpleCommand("Zero position", () -> motor1.setSelectedSensorPosition(0)));
    SmartDashboard.putData("Scheduler", Scheduler.getInstance());
  }

  @Override
  public void disabledInit() {

  }

  @Override
  public void autonomousInit() {

  }

  @Override
  public void teleopInit() {
    joystickPosition = motor1.getSelectedSensorPosition();
  }

  @Override
  public void testInit() {

  }

  @Override
  public void robotPeriodic() {
    Scheduler.getInstance().run();

    motor1.setBrake(true);
    motor1.setInverted(invert1);
    motor1.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
    motor1.configClosedloopRamp(clr);
    motor1.configPeakOutputForward(pof);
    motor1.configPeakOutputReverse(por);
    motor1.config_kP(0, p);
    motor1.config_kI(0, i);
    motor1.config_kD(0, d);
    motor1.setSensorPhase(invertSensor);

    motor2.set(ControlMode.Follower, motor1.getDeviceID());
    motor2.setInverted(invert2);
    motor1.configClosedloopRamp(0);
    motor1.configPeakOutputForward(1);
    motor1.configPeakOutputReverse(-1);
    SmartDashboard.putNumber("Throttle", motor1.getMotorOutputPercent());
    SmartDashboard.putNumber("Position", motor1.getSelectedSensorPosition());
  }

  @Override
  public void disabledPeriodic() {
  }

  @Override
  public void autonomousPeriodic() {

  }

  @Override
  public void teleopPeriodic() {
    switch (chooser.getSelected()) {
      case JOYSTICK:
        motor1.set(ControlMode.PercentOutput, joystick.getRawAxis(1));
        joystickPosition = motor1.getSelectedSensorPosition();
        break;
      case PID:
        motor1.set(ControlMode.Position, setpoint);
        joystickPosition = motor1.getSelectedSensorPosition();
        break;
      case PID_JOYSTICK:
        joystickPosition +=
            Utilities.processAxisDeadzone(joystick.getRawAxis(1), 0.1) * joystickMultiplier;
        motor1.set(ControlMode.Position, joystickPosition);
        break;
      case PID_MTP:
        joystickPosition -= Math.copySign(joystickMultiplier,
            motor1.getSelectedSensorPosition() - setpoint);
        motor1.set(ControlMode.Position, joystickPosition);
    }
  }

  @Override
  public void testPeriodic() {

  }
}

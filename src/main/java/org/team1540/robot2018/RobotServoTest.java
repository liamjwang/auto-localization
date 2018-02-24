package org.team1540.robot2018;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Value;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.base.adjustables.AdjustableManager;
import org.team1540.base.adjustables.Tunable;

public class RobotServoTest extends IterativeRobot {

  private Joystick driver = new Joystick(0);

  @Tunable("enableServo")
  public boolean enableServo = false;
  @Tunable("invertServoA")
  public boolean invertA = false;
  @Tunable("invertServoB")
  public boolean invertB = false;

  // @Tunable("servoDivisor")
  // double servoDivisor = 30;

  Servo pan = new Servo(0);
  Servo tilt = new Servo(1);
  Relay servoRelay = new Relay(3);

  @Override
  public void robotInit() {
    LiveWindow.disableAllTelemetry();

    AdjustableManager.getInstance().add(this);
  }

  @Override
  public void disabledInit() {
    servoRelay.set(Value.kOff);
  }

  @Override
  public void autonomousInit() {
  }

  @Override
  public void teleopInit() {
    servoRelay.set(Value.kOn);
  }

  @Override
  public void testInit() {
  }

  @Override
  public void robotPeriodic() {
    AdjustableManager.getInstance().update();
    if (SmartDashboard.getBoolean("Enable Servo Control", false)) {

      double processedPan =
          OI.isOutsideRange((OI.getDriverLeftX() / SmartDashboard.getNumber("Servo Divisor", 30)) + pan.get());
      double processedTilt =
          OI.isOutsideRange((OI.getDriverLeftY() / SmartDashboard.getNumber("Servo Divisor", 30)) + tilt.get());

      SmartDashboard.putNumber("Processed Pan", processedPan);
      SmartDashboard.putNumber("Processed Tilt", processedTilt);

      pan.set(processedPan);
      tilt.set(processedTilt);

    }
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

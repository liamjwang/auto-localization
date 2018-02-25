package org.team1540.robot2018;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Value;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.base.Utilities;
import org.team1540.base.adjustables.AdjustableManager;
import org.team1540.base.adjustables.Tunable;

public class RobotServoTest extends IterativeRobot {

  private Joystick driver = new Joystick(0);

  @Tunable("[ServoTest] enableServo")
  public boolean enableServo = false;
  @Tunable("[ServoTest] invertServoA")
  public boolean invertA = false;
  @Tunable("[ServoTest] invertServoB")
  public boolean invertB = false;

  @Tunable("[ServoTest] panServoChannel")
  public int panServoChannel = 0;
  @Tunable("[ServoTest] tiltServoChannel")
  public int tiltServoChannel = 1;
  @Tunable("[ServoTest] servoRelayChannel")
  public int servoRelayChannel = 3;

  // @Tunable("[ServoTest] servoDivisor")
  // double servoDivisor = 30;

  Servo pan = new Servo(panServoChannel);
  Servo tilt = new Servo(tiltServoChannel);
  Relay servoRelay = new Relay(servoRelayChannel);

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
    Scheduler.getInstance().run();
    if (SmartDashboard.getBoolean("Enable Servo Control", false)) {
      // TODO: rewrite the servo testing class to have the inputs be tunable
      double processedPan =
          isOutsideRange((OI.getServoPanAxis() / SmartDashboard.getNumber("[ServoTest] Servo Divisor", 260)) + pan.get());
      double processedTilt =
          isOutsideRange((OI.getServoTiltAxis() / SmartDashboard.getNumber("[ServoTest] Servo Divisor", 260)) + tilt.get());

      SmartDashboard.putNumber("[ServoTest] Processed Pan", processedPan);
      SmartDashboard.putNumber("[ServoTest] Processed Tilt", processedTilt);

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

  public static double isOutsideRange(double value) {
    return Utilities.constrain(value, 0, 1);
  }
}

package org.team1540.robot2018;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.base.wrappers.ChickenController;
import org.team1540.base.wrappers.ChickenTalon;
import org.team1540.base.wrappers.ChickenVictor;

public class RobotMotorTest extends IterativeRobot {

  private ChickenController[] motors = new ChickenController[17];
  private SendableChooser<Integer> motorChooser;

  Boolean enableServo = false;
  double servoDivisor = 30;

  Servo pan = new Servo(0);
  Servo tilt = new Servo(1);

  @Override
  public void robotInit() {
    LiveWindow.disableAllTelemetry();
    motorChooser = new SendableChooser<Integer>();

    for (int i = 1; i <= 16; i++) {
      motorChooser.addObject(Integer.toString(i), i);
    }

    SmartDashboard.putData("Motor Chooser", motorChooser);
    SmartDashboard.putData("PDP", new PowerDistributionPanel());
    SmartDashboard.putBoolean("Enable Servo Control", enableServo);

    SmartDashboard.putNumber("Servo Divisor", servoDivisor);

    motors[1] = new ChickenTalon(1);
    motors[2] = new ChickenTalon(2);
    motors[3] = new ChickenTalon(3);
    motors[4] = new ChickenTalon(4);
    motors[5] = new ChickenTalon(5);
    motors[6] = new ChickenTalon(6);
    motors[7] = new ChickenTalon(7);
    motors[8] = new ChickenVictor(8);

    motors[9] = new ChickenTalon(9);

    motors[10] = new ChickenVictor(10);
    motors[11] = new ChickenVictor(11);
    motors[12] = new ChickenVictor(12);

    motors[13] = new ChickenTalon(13);
    motors[14] = new ChickenTalon(14);
    motors[15] = new ChickenVictor(15);
    motors[16] = new ChickenVictor(16);
  }

  @Override
  public void disabledInit() {
  }

  @Override
  public void autonomousInit() {
  }

  @Override
  public void teleopInit() {
  }

  @Override
  public void testInit() {
  }

  @Override
  public void robotPeriodic() {
    SmartDashboard.putNumber("Driver Right Y", OI.getDriverRightY());
    SmartDashboard.putNumber("Driver Left Y", OI.getDriverLeftY());
    SmartDashboard.putNumber("Driver Left X", OI.getDriverLeftX());

    Scheduler.getInstance().run();
    if (motorChooser.getSelected() != null) {
      motors[motorChooser.getSelected()].set(ControlMode.PercentOutput, OI.getDriverRightY());
    }

    if (SmartDashboard.getBoolean("Enable Servo Control", false)) {

      double processedPan =
          OI.isOutsideRange((OI.getDriverLeftX() / SmartDashboard.getNumber("Servo Divisor", 30)) + pan.get());
      double processedTilt =
          OI.isOutsideRange((OI.getDriverLeftY() / SmartDashboard.getNumber("Servo Divisor", 30)) + tilt.get());

      SmartDashboard.putNumber("Processed Pan", processedPan);
      SmartDashboard.putNumber("Processed Tilt", processedTilt);

      pan.set(processedPan);
      tilt.set(processedTilt);

      /*
      OI returns -1 to 1
      I need 0 to 1
       */
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

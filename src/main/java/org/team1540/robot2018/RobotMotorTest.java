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

  private ChickenController[] motorsA = new ChickenController[17];
//  private ChickenController[] motorsB = new ChickenController[17];
  private SendableChooser<Integer> motorChooserA;
//  private SendableChooser<Integer> motorChooserB;

  private boolean enableServo = false;
  private boolean invertA = false;
  private boolean invertB = false;

  double servoDivisor = 30;

  Servo pan = new Servo(0);
  Servo tilt = new Servo(1);

  @Override
  public void robotInit() {
    LiveWindow.disableAllTelemetry();
    motorChooserA = new SendableChooser<Integer>();
//    motorChooserB = new SendableChooser<Integer>();

    for (int i = 0; i <= 16; i++) {
      motorChooserA.addObject(Integer.toString(i), i);
//      motorChooserB.addObject(Integer.toString(i), i);
    }

    SmartDashboard.putData("Motor ChooserA", motorChooserA);
//    SmartDashboard.putData("Motor ChooserB", motorChooserB);
    SmartDashboard.putData("PDP", new PowerDistributionPanel());
    SmartDashboard.putBoolean("Enable Servo Control", enableServo);

    SmartDashboard.putNumber("Servo Divisor", servoDivisor);

    motorsA[1] = new ChickenTalon(1);
    motorsA[2] = new ChickenTalon(2);
    motorsA[3] = new ChickenTalon(3);
    motorsA[4] = new ChickenTalon(4);
    motorsA[5] = new ChickenTalon(5);
    motorsA[6] = new ChickenTalon(6);
    motorsA[7] = new ChickenTalon(7);
    motorsA[8] = new ChickenVictor(8);

    motorsA[9] = new ChickenTalon(9);

    motorsA[10] = new ChickenVictor(10);
    motorsA[11] = new ChickenVictor(11);
    motorsA[12] = new ChickenVictor(12);

    motorsA[13] = new ChickenTalon(13);
    motorsA[14] = new ChickenTalon(14);
    motorsA[15] = new ChickenVictor(15);
    motorsA[16] = new ChickenVictor(16);

//    motorsA[0] = null;

//    motorsB[1] = new ChickenTalon(1);
//    motorsB[2] = new ChickenTalon(2);
//    motorsB[3] = new ChickenTalon(3);
//    motorsB[4] = new ChickenTalon(4);
//    motorsB[5] = new ChickenTalon(5);
//    motorsB[6] = new ChickenTalon(6);
//    motorsB[7] = new ChickenTalon(7);
//    motorsB[8] = new ChickenVictor(8);
//
//    motorsB[9] = new ChickenTalon(9);
//
//    motorsB[10] = new ChickenVictor(10);
//    motorsB[11] = new ChickenVictor(11);
//    motorsB[12] = new ChickenVictor(12);
//
//    motorsB[13] = new ChickenTalon(13);
//    motorsB[14] = new ChickenTalon(14);
//    motorsB[15] = new ChickenVictor(15);
//    motorsB[16] = new ChickenVictor(16);
//
//    motorsB[0] = null;
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
    SmartDashboard.putNumber("Driver Right X", OI.getDriverRightX());
    SmartDashboard.putNumber("Driver Left Y", OI.getDriverLeftY());
    SmartDashboard.putNumber("Driver Left X", OI.getDriverLeftX());

    Scheduler.getInstance().run();
    if (motorChooserA.getSelected() != null) {
      motorsA[motorChooserA.getSelected()].set(ControlMode.PercentOutput, OI.getDriverRightY());
    }
//    if (motorChooserB.getSelected() != null) {
//      motorsB[motorChooserB.getSelected()].set(ControlMode.PercentOutput, -OI.getDriverRightY());
//    }

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

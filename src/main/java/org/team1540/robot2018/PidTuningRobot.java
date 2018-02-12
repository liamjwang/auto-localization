package org.team1540.robot2018;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.base.adjustables.Tunable;
import org.team1540.base.util.SimpleCommand;
import org.team1540.base.wrappers.ChickenController;
import org.team1540.base.wrappers.ChickenTalon;
import org.team1540.base.wrappers.ChickenVictor;

public class PidTuningRobot extends IterativeRobot {
  @Tunable("Motor 1 ID")
  public int motor1Id = 0;
  @Tunable("Motor 2 ID")
  public int motor2Id = 0;
  @Tunable("Motor 2 Is Victor")
  public boolean motor2IsVictor;
  @Tunable("P")
  public double p;
  @Tunable("I")
  public double i;
  @Tunable("D")
  public double d;
  @Tunable("PID Mode")
  public boolean usingPid;
  @Tunable("PID Setpoint")
  public double setpoint;

  @Tunable("Invert Motor 1")
  public boolean invert1 = false;
  @Tunable("Invert Motor 2")
  public boolean invert2 = false;

  private ChickenTalon motor1;
  private ChickenController motor2;

  @Override
  public void robotInit() {
    // zeroing command
    SmartDashboard.putData("Zero Position", new SimpleCommand("Zero position", () -> motor1.setSelectedSensorPosition(0)));
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
    Scheduler.getInstance().run();
    if (motor1 == null || motor1.getDeviceID() != motor1Id) {
      motor1 = new ChickenTalon(motor1Id);
    }

    motor1.setBrake(true);
    motor1.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
    motor1.configClosedloopRamp(0);
    motor1.configPeakOutputForward(1);
    motor1.configPeakOutputReverse(-1);
    motor1.config_kP(0, p);
    motor1.config_kI(0, i);
    motor1.config_kD(0, d);

    if (motor2Id == 0) {
      motor2 = null;
    } else if (motor2 == null || (motor2 instanceof ChickenVictor != motor2IsVictor)) {
      motor2 = motor2IsVictor ? new ChickenVictor(motor2Id) : new ChickenTalon(motor2Id);
      motor2.configClosedloopRamp(0);
      motor2.configPeakOutputForward(1);
      motor2.configPeakOutputReverse(-1);
    }

    if (motor2 != null) {
      motor2.set(ControlMode.Follower, motor1.getDeviceID());
    }

    SmartDashboard.putNumber("Position", motor1.getSelectedSensorPosition());
  }

  @Override
  public void disabledPeriodic() {
    super.disabledPeriodic();
  }

  @Override
  public void autonomousPeriodic() {
    super.autonomousPeriodic();
  }

  @Override
  public void teleopPeriodic() {
    if (usingPid) {
      motor1.set(ControlMode.Position, setpoint);
    } else {
      motor1.set(ControlMode.PercentOutput, 0);
    }
  }

  @Override
  public void testPeriodic() {
    super.testPeriodic();
  }
}

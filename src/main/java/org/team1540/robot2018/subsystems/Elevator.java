package org.team1540.robot2018.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.base.ChickenSubsystem;
import org.team1540.base.util.SimpleCommand;
import org.team1540.base.wrappers.ChickenTalon;
import org.team1540.robot2018.RobotMap;
import org.team1540.robot2018.Tuning;
import org.team1540.robot2018.commands.elevator.HoldElevatorPosition;

public class Elevator extends ChickenSubsystem {

  private ChickenTalon talon1 = new ChickenTalon(RobotMap.elevator_1);
  private ChickenTalon talon2 = new ChickenTalon(RobotMap.elevator_2);

  public Elevator() {
    this.add(talon1, talon2);
    this.setPriority(10);

    talon1.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
    Command command = new SimpleCommand("Zero Elevator", () -> talon1.setSelectedSensorPosition(0));
    command.setRunWhenDisabled(true);
    SmartDashboard.putData(command);
  }

  public int getError() {
    return talon1.getClosedLoopError();
  }

  public double getPosition() {
    return talon1.getSelectedSensorPosition();
  }

  public int getTrajPosition() {
    return talon1.getActiveTrajectoryPosition();
  }

  @Override
  public void initDefaultCommand() {
    setDefaultCommand(new HoldElevatorPosition());
  }

  public void setMotionMagicPosition(double position) {
    talon1.set(ControlMode.MotionMagic, position);
    talon2.set(ControlMode.Follower, talon1.getDeviceID());
  }

  public void set(double value) {
    talon1.set(ControlMode.PercentOutput, value);
    talon2.set(ControlMode.PercentOutput, value);
  }

  public void stop() {
    talon1.set(ControlMode.PercentOutput, 0);
    talon2.set(ControlMode.PercentOutput, 0);
  }

  @Override
  public void periodic() {
    talon1.config_kP(0, Tuning.elevatorP);
    talon1.config_kI(0, Tuning.elevatorI);
    talon1.config_kD(0, Tuning.elevatorD);
    talon1.config_kF(0, talon1.getSelectedSensorVelocity()
        > 0 ? Tuning.elevatorFGoingUp : Tuning.elevatorFGoingDown);
    talon1.configMotionCruiseVelocity(Tuning.elevatorCruiseVel);
    talon1.configMotionAcceleration(Tuning.elevatorMaxAccel);
    talon1.config_IntegralZone(0, Tuning.elevatorIZone);

    talon1.setInverted(true);
    talon2.setInverted(true);
    talon1.setSensorPhase(Tuning.isPandora);
  }
}

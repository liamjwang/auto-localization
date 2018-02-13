package org.team1540.robot2018.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import org.team1540.base.ChickenSubsystem;
import org.team1540.base.wrappers.ChickenTalon;
import org.team1540.robot2018.RobotMap;
import org.team1540.robot2018.Tuning;
import org.team1540.robot2018.commands.elevator.HoldElevatorPosition;

public class Elevator extends ChickenSubsystem {

  private ChickenTalon elevator_1 = new ChickenTalon(RobotMap.elevator_1);
  private ChickenTalon elevator_2 = new ChickenTalon(RobotMap.elevator_2);

  public Elevator() {
    this.add(elevator_1, elevator_2);
    this.setPriority(10);
    elevator_1.setInverted(false);
    elevator_2.setInverted(false);

    elevator_1.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder); // TODO: 2/9/18 Figure out which motor has the encoder
  }

  public void set(double value){
    elevator_1.set(ControlMode.PercentOutput, value);
    elevator_2.set(ControlMode.PercentOutput, -value);
  }

  public void stop(){
    elevator_1.set(ControlMode.PercentOutput, 0);
    elevator_2.set(ControlMode.PercentOutput, 0);
  }

  public void updatePID() {
    elevator_1.config_kP(0, Tuning.elevatorP);
    elevator_1.config_kI(0, Tuning.elevatorI);
    elevator_1.config_kD(0, Tuning.elevatorD);
  }

  public double setPosition(double position){
    position = position < Tuning.elevatorUpLimit ? position : Tuning.elevatorUpLimit - Tuning.elevatorBounceBack;
    position = position >= Tuning.elevatorDownLimit ? position : 0 + Tuning.elevatorBounceBack;
    set(position);
    return position;
  }

  public double getPosition(){
    return elevator_1.getSelectedSensorPosition();
  }

  @Override
  public void initDefaultCommand(){
    setDefaultCommand(new HoldElevatorPosition());
  }
}

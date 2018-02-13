package org.team1540.robot2018.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import org.team1540.base.ChickenSubsystem;
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
    talon1.setInverted(true);
    talon2.setInverted(true);

    talon1.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
  }

  public void set(double value){
    talon1.set(ControlMode.PercentOutput, value);
    talon2.set(ControlMode.PercentOutput, value);
  }

  public void stop(){
    talon1.set(ControlMode.PercentOutput, 0);
    talon2.set(ControlMode.PercentOutput, 0);
  }

  public void updatePID() {
    talon1.config_kP(0, Tuning.elevatorP);
    talon1.config_kI(0, Tuning.elevatorI);
    talon1.config_kD(0, Tuning.elevatorD);
  }

  public double setPosition(double position){
    position = position < Tuning.elevatorUpLimit ? position : Tuning.elevatorUpLimit - Tuning.elevatorBounceBack;
    position = position >= Tuning.elevatorDownLimit ? position : 0 + Tuning.elevatorBounceBack;
    set(position);
    return position;
  }

  public double getPosition(){
    return talon1.getSelectedSensorPosition();
  }

  @Override
  public void initDefaultCommand(){
    setDefaultCommand(new HoldElevatorPosition());
  }
}

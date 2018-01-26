package org.team1540.robot2018.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import org.team1540.base.wrappers.ChickenTalon;
import org.team1540.robot2018.OI;
import org.team1540.robot2018.RobotMap;
import org.team1540.base.ChickenSubsystem;

public class Elevator extends ChickenSubsystem {

  ChickenTalon elevator_1 = new ChickenTalon(RobotMap.elevator_1);
  ChickenTalon elevator_2 = new ChickenTalon(RobotMap.elevator_2);

  public Elevator() {
    this.add(elevator_1, elevator_2);
    this.setPriority(10);
    elevator_1.setInverted(false);
    elevator_2.setInverted(false);

    elevator_2.set(ControlMode.Follower, elevator_1.getDeviceID());
  }

  public void JoystickElevator(){
    elevator_1.set(ControlMode.PercentOutput, OI.getCopilotLeftX());
  }

  public void ManualElevatorUp(){
    elevator_1.set(ControlMode.PercentOutput, -0.5); //TODO: What direction does negative make it go
  }

  public void ManualElevatorDown(){
    elevator_1.set(ControlMode.PercentOutput, 0.5);
  }

  public void stop(){
    elevator_1.set(ControlMode.PercentOutput, 0);
  }
}

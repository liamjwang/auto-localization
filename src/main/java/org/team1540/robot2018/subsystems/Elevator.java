package org.team1540.robot2018.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import org.team1540.base.wrappers.ChickenTalon;
import org.team1540.robot2018.OI;
import org.team1540.robot2018.RobotMap;
import org.team1540.base.ChickenSubsystem;
import org.team1540.robot2018.commands.JoystickElevator;

public class Elevator extends ChickenSubsystem {

  private ChickenTalon elevator_1 = new ChickenTalon(RobotMap.elevator_1);
  private ChickenTalon elevator_2 = new ChickenTalon(RobotMap.elevator_2);

  public Elevator() {
    this.add(elevator_1, elevator_2);
    this.setPriority(10);
    elevator_1.setInverted(false);
    elevator_2.setInverted(false);
  }

  public void set(double value){
    elevator_1.set(ControlMode.PercentOutput, value);
    elevator_2.set(ControlMode.PercentOutput, -value);
  }

  public void stop(){
    elevator_1.set(ControlMode.PercentOutput, 0);
    elevator_2.set(ControlMode.PercentOutput, 0);
  }

  @Override
  public void initDefaultCommand(){
    setDefaultCommand(new JoystickElevator());
  }
}

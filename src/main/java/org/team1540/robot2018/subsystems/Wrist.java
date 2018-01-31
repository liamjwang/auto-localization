package org.team1540.robot2018.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import org.team1540.base.wrappers.ChickenTalon;
import org.team1540.robot2018.RobotMap;
import org.team1540.base.ChickenSubsystem;

public class Wrist extends ChickenSubsystem {
  
  ChickenTalon wristMotor = new ChickenTalon(RobotMap.wristMotor);

  public Wrist() {
    this.add(wristMotor);
    this.setPriority(11);
    wristMotor.setInverted(false);
  }

  public void ManualUp(){
    wristMotor.set(ControlMode.PercentOutput, -0.5); //TODO: check if negative makes it go backwards
  }

  public void ManualDown(){
    wristMotor.set(ControlMode.PercentOutput, 0.5);
  }

  public void stop(){
    wristMotor.set(ControlMode.PercentOutput, 0);
  }
}

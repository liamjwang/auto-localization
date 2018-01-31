package org.team1540.robot2018.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import org.team1540.base.wrappers.ChickenTalon;
import org.team1540.robot2018.RobotMap;
import org.team1540.base.ChickenSubsystem;

public class Intake extends ChickenSubsystem {

  public ChickenTalon intake_1 = new ChickenTalon(RobotMap.intake_1);
  public ChickenTalon intake_2 = new ChickenTalon(RobotMap.intake_2);

  public Intake() {
    this.add(intake_1, intake_2);
    this.setPriority(10);
    intake_1.setInverted(false);
    intake_2.setInverted(false);

    intake_2.set(ControlMode.Follower, intake_1.getDeviceID());
  }

  public void manualEject(){
    intake_1.set(ControlMode.PercentOutput, -0.5); //TODO: check if negative makes it go backwards
  }

  public void manualIntake(){
    intake_1.set(ControlMode.PercentOutput, 0.5);
  }

  public void stop(){
    intake_1.set(ControlMode.PercentOutput, 0);
  }
}

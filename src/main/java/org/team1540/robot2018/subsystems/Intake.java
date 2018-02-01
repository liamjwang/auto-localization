package org.team1540.robot2018.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import org.team1540.base.wrappers.ChickenTalon;
import org.team1540.robot2018.RobotMap;
import org.team1540.base.ChickenSubsystem;
import org.team1540.robot2018.Tuning;

public class Intake extends ChickenSubsystem {

  private ChickenTalon intake_1 = new ChickenTalon(RobotMap.intake_1);
  private ChickenTalon intake_2 = new ChickenTalon(RobotMap.intake_2);

  public Intake() {
    this.add(intake_1, intake_2);
    this.setPriority(10);
    intake_1.setInverted(false);
    intake_2.setInverted(false);

    intake_2.set(ControlMode.Follower, intake_1.getDeviceID());
  }

  public void manualIntake(double speed){
    intake_1.set(ControlMode.PercentOutput, speed);
  }

  public void stop(){
    intake_1.set(ControlMode.PercentOutput, 0);
  }

  public void set(double value){
    intake_1.set(ControlMode.PercentOutput, value);
  }

  public double getCurrent1(){
    return intake_1.getOutputCurrent();
  }
  public double getCurrent2(){
    return intake_2.getOutputCurrent();
  }
}

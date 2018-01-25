package org.team1540.robot2018.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import org.team1540.base.wrappers.ChickenTalon;
import org.team1540.robot2018.RobotMap;
import org.team1540.base.ChickenSubsystem;
import edu.wpi.first.wpilibj.Timer;


public class Intake extends ChickenSubsystem {

  private final Timer timer = new Timer();

  ChickenTalon intake_1 = new ChickenTalon(RobotMap.intake_1);
  ChickenTalon intake_2 = new ChickenTalon(RobotMap.intake_2);

  public Intake() {
    this.add(intake_1, intake_2);
    this.setPriority(10);
    intake_1.setInverted(false);
    intake_2.setInverted(false);

    intake_2.set(ControlMode.Follower, intake_1.getDeviceID());
  }

  public void IntakePowerup(double speed, double spikeCurrent, double spikeLength) {

    if (timer.get() <= 0) { //If the timer hasn't started, start it
      timer.start();
    }else{ //If it has already started, reset it.
      timer.reset();
    }

    if (intake_1.getOutputCurrent() < spikeCurrent) {
      intake_1.set(ControlMode.PercentOutput, speed); //Start intaking at the speed specified

    } else if(timer.get() >= spikeLength){
      intake_1.set(ControlMode.Follower, 0);
    }
  }
}

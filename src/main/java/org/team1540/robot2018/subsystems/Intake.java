package org.team1540.robot2018.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import org.team1540.base.wrappers.ChickenTalon;
import org.team1540.robot2018.RobotMap;
import org.team1540.base.ChickenSubsystem;
import edu.wpi.first.wpilibj.Timer;

public class Intake extends ChickenSubsystem {

  private final Timer IntakeTimer = new Timer();
  private final Timer EjectTimer = new Timer();

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
    IntakeTimer.reset();

    if (intake_1.getOutputCurrent() < spikeCurrent) {
      intake_1.set(ControlMode.PercentOutput, speed);

    } else if(IntakeTimer.get() >= spikeLength){
      intake_1.set(ControlMode.PercentOutput, 0);
    }
  }

  public void EjectPowerup(double speed, double stopCurrent, double stopLength){
    EjectTimer.reset();

    if (intake_1.getOutputCurrent() > stopCurrent) {
      intake_1.set(ControlMode.PercentOutput, speed);

    } else if(EjectTimer.get() >= stopLength){
      intake_1.set(ControlMode.PercentOutput, 0);
    }
  }

  public void ManualEject(){
    intake_1.set(ControlMode.PercentOutput, -0.5); //TODO: check if negative makes it go backwards
  }

  public void ManualIntake(){
    intake_1.set(ControlMode.PercentOutput, 0.5);
  }

  public void stop(){
    intake_1.set(ControlMode.PercentOutput, 0);
  }
}

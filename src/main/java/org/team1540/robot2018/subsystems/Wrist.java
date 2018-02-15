package org.team1540.robot2018.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import org.team1540.base.ChickenSubsystem;
import org.team1540.base.wrappers.ChickenTalon;
import org.team1540.robot2018.RobotMap;
import org.team1540.robot2018.Tuning;
import org.team1540.robot2018.commands.JoystickWrist;

public class Wrist extends ChickenSubsystem {
  
  private ChickenTalon wristMotor = new ChickenTalon(RobotMap.wristMotor);

  public Wrist() {
    this.add(wristMotor);
    this.setPriority(11);
    wristMotor.setInverted(false);

    wristMotor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
  }

  public void set(double value) {
    wristMotor.set(value);
  }

  public void stop(){
    wristMotor.set(ControlMode.PercentOutput, 0);
  }

  public void updatePID() {
    wristMotor.config_kP(0, Tuning.wristP);
    wristMotor.config_kI(0, Tuning.wristI);
    wristMotor.config_kD(0, Tuning.wristD);
  }

  public double setPosition(double position){
    position = position < Tuning.wristUpLimit ? position : Tuning.wristUpLimit - Tuning.wristBounceBack;
    position = position >= Tuning.wristDownLimit ? position : 0 + Tuning.wristBounceBack;
    wristMotor.set(position);
    return position;
  }

  public double getPosition(){
    return wristMotor.getSelectedSensorPosition();
  }

  @Override
  public void initDefaultCommand() {
    setDefaultCommand(new JoystickWrist());
  }
}

package org.team1540.robot2018.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import org.team1540.base.ChickenSubsystem;
import org.team1540.base.wrappers.ChickenTalon;
import org.team1540.robot2018.RobotMap;
import org.team1540.robot2018.Tuning;

public class Wrist extends ChickenSubsystem {
  
  private ChickenTalon wristMotor = new ChickenTalon(RobotMap.wristMotor);

  public Wrist() {
    this.add(wristMotor);
    this.setPriority(11);
    wristMotor.setInverted(false);

    wristMotor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
  }

  @Override
  public void initDefaultCommand() {
    //setDefaultCommand();
  }

  public void set(double value) {
    wristMotor.set(value);
  }

  public void stop(){
    wristMotor.set(ControlMode.PercentOutput, 0);
  }

  public void resetEncoder() {
    wristMotor.setSelectedSensorPosition(0);
  }

  public void setMotionMagicPosition(double position) {
    wristMotor.set(ControlMode.MotionMagic, position);
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

  public void updatePID() {
    wristMotor.config_kP(0, Tuning.wristP);
    wristMotor.config_kI(0, Tuning.wristI);
    wristMotor.config_kD(0, Tuning.wristD);

    wristMotor.config_kF(0, Tuning.wristF);

    wristMotor.config_IntegralZone(0, Tuning.wristIzone);

    wristMotor.configMotionAcceleration(Tuning.motionMaxAccel);
    wristMotor.configMotionCruiseVelocity(Tuning.motionCruiseVelocity);
  }
}

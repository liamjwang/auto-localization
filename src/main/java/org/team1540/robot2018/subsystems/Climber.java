package org.team1540.robot2018.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.base.wrappers.ChickenTalon;
import org.team1540.robot2018.RobotMap;
import org.team1540.base.ChickenSubsystem;
import org.team1540.robot2018.commands.AlignClimber;
import org.team1540.robot2018.Tuning;

public class Climber extends ChickenSubsystem {

  private ChickenTalon tapeMeasureMotor = new ChickenTalon(RobotMap.tapeMeasureMotor);

  private ChickenTalon winchA = new ChickenTalon(RobotMap.winchA);
  private ChickenTalon winchB = new ChickenTalon(RobotMap.winchB);
  private ChickenTalon winchC = new ChickenTalon(RobotMap.winchC);
  private ChickenTalon winchD = new ChickenTalon(RobotMap.winchD);

  public Servo pan = new Servo(RobotMap.panServo);
  public Servo tilt = new Servo(RobotMap.tiltServo);

  public Climber() {
    this.add(tapeMeasureMotor, winchA, winchB, winchC, winchD);
    this.setPriority(12);

    tapeMeasureMotor.setInverted(false);
    winchA.setInverted(false);
    winchB.setInverted(false);
    winchC.setInverted(false);
    winchD.setInverted(false);

    winchB.set(ControlMode.Follower, winchA.getDeviceID());
    winchC.set(ControlMode.Follower, winchA.getDeviceID());
    winchD.set(ControlMode.Follower, winchA.getDeviceID());

    SmartDashboard.putNumber("Pan Value", pan.get());
    SmartDashboard.putNumber("Tilt Value", tilt.get());
  }

  public void setWinch(double speed){
    winchA.set(ControlMode.PercentOutput, speed);
  }

  public void stopWinch(){
    winchA.set(ControlMode.PercentOutput, 0);
  }

  public void setTape(double speed){
    tapeMeasureMotor.set(ControlMode.PercentOutput, speed);
  }

  public void stopTape(){
    tapeMeasureMotor.set(ControlMode.PercentOutput, 0);
  }

  public void align(double x, double y){
    pan.set(x);
    tilt.set(y);
  }

  public void runClimber(double value){
    double tapeMeasureValue = value * Tuning.tapeMeasureMultiplier;
    double winchValue = value * Tuning.winchMultiplier;
    setTape(tapeMeasureValue);
    setWinch(winchValue);
  }

  public void stop(){
    stopTape();
    stopWinch();
  }

  @Override
  public void initDefaultCommand(){
    setDefaultCommand(new AlignClimber());
  }
}

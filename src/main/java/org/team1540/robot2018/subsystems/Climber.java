package org.team1540.robot2018.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.base.ChickenSubsystem;
import org.team1540.base.wrappers.ChickenTalon;
import org.team1540.base.wrappers.ChickenVictor;
import org.team1540.robot2018.RobotMap;
import org.team1540.robot2018.Tuning;
import org.team1540.robot2018.commands.climber.AlignClimber;

public class Climber extends ChickenSubsystem{

  private ChickenVictor tapeMeasureMotor = new ChickenVictor(RobotMap.tapeMeasureMotor);

  private ChickenTalon winchA = new ChickenTalon(RobotMap.winchA);
  private ChickenTalon winchB = new ChickenTalon(RobotMap.winchB);
  private ChickenTalon winchC = new ChickenTalon(RobotMap.winchC);
  private ChickenTalon winchD = new ChickenTalon(RobotMap.winchD);

  private Servo pan = new Servo(RobotMap.panServo);
  private Servo tilt = new Servo(RobotMap.tiltServo);

  public Climber() {
    tapeMeasureMotor.setInverted(false);
    winchA.setInverted(false);
    winchB.setInverted(false);
    winchC.setInverted(true);
    winchD.setInverted(true);


    SmartDashboard.putNumber("Pan Value", pan.get());
    SmartDashboard.putNumber("Tilt Value", tilt.get());
  }

  public void setWinch(double speed){
    winchA.set(ControlMode.PercentOutput, speed);
    winchB.set(ControlMode.PercentOutput, speed);
    winchC.set(ControlMode.PercentOutput, speed);
    winchD.set(ControlMode.PercentOutput, speed);
  }

  public void setTape(double speed){
    tapeMeasureMotor.set(ControlMode.PercentOutput, speed);
  }

  public void align(double x, double y){
    pan.set(x);
    tilt.set(y);
  }

  public double getPan() {
    return pan.get();
  }

  public double getTilt() {
    return tilt.get();
  }

  public void runClimber(double value){
    double tapeMeasureValue = value * Tuning.tapeMeasureMultiplier;
    double winchValue = value * Tuning.winchMultiplier;
    setTape(tapeMeasureValue);
    setWinch(winchValue);
  }

  public void stop(){
    setTape(0);
    setWinch(0);
  }

  public void disableServos() {
      pan.setDisabled();
      tilt.setDisabled();
  }

  @Override
  public void initDefaultCommand(){
    setDefaultCommand(new AlignClimber());
  }
}

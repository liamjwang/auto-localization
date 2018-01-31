package org.team1540.robot2018.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.base.wrappers.ChickenTalon;
import org.team1540.robot2018.RobotMap;
import org.team1540.base.ChickenSubsystem;
import org.team1540.robot2018.commands.AlignClimber;

public class Climber extends ChickenSubsystem {

  ChickenTalon tapeMeasureMotor = new ChickenTalon(RobotMap.tapeMeasureMotor);

  ChickenTalon winchA = new ChickenTalon(RobotMap.winchA);
  ChickenTalon winchB = new ChickenTalon(RobotMap.winchB);
  ChickenTalon winchC = new ChickenTalon(RobotMap.winchC);
  ChickenTalon winchD = new ChickenTalon(RobotMap.winchD);

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

  public void manualWinch(double speed){
    winchA.set(ControlMode.PercentOutput, speed);
  }

  public void stop(){
    winchA.set(ControlMode.PercentOutput, 0);
  }

  @Override
  public void initDefaultCommand(){
    setDefaultCommand(new AlignClimber());
  }
}

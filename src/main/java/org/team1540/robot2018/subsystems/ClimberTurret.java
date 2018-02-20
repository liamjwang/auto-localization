package org.team1540.robot2018.subsystems;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Value;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.team1540.robot2018.RobotMap;

public class ClimberTurret extends Subsystem {
  private Servo pan = new Servo(RobotMap.panServo);
  private Servo tilt = new Servo(RobotMap.tiltServo);

  private Relay servoRelay = new Relay(RobotMap.servoRelay);

  @Override
  protected void initDefaultCommand() {

  }

  public void set(double pan, double tilt) {
    setPan(pan);
    setTilt(tilt);
  }

  public void setPan(double panVal) {
    pan.setPosition(panVal);
  }

  public void setTilt(double tiltVal) {
    tilt.setPosition(tiltVal);
  }

  public double getPan() {
    return pan.getPosition();
  }

  public double getTilt() {
    return tilt.getPosition();
  }

  public void enableServos() {
    servoRelay.set(Value.kOn);
  }

  public void disableServos() {
    servoRelay.set(Value.kOff);
  }
}

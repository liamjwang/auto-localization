package org.team1540.robot2018.subsystems;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Value;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.team1540.robot2018.RobotMap;
import org.team1540.robot2018.Tuning;

public class ClimberTurret extends Subsystem {
  private Servo pan = new Servo(RobotMap.PAN_SERVO);
  private Servo tilt = new Servo(RobotMap.TILT_SERVO);

  private Relay servoRelay = new Relay(RobotMap.SERVO_RELAY);

  @Override
  protected void initDefaultCommand() {

  }

  public void init() {
    setPan(Tuning.turretInitPan);
    setTilt(Tuning.turretInitTilt);
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

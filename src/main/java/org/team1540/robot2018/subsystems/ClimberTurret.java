package org.team1540.robot2018.subsystems;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.team1540.robot2018.RobotMap;

public class ClimberTurret extends Subsystem {
  private Servo pan = new Servo(RobotMap.panServo);
  private Servo tilt = new Servo(RobotMap.tiltServo);

  @Override
  protected void initDefaultCommand() {

  }

  public void set(double pan, double tilt) {
    setPan(pan);
    setTilt(tilt);
  }

  public void setPan(double panVal) {
    pan.set(panVal);
  }

  public void setTilt(double tiltVal) {
    tilt.set(tiltVal);
  }

  public double getPan() {
    return pan.get();
  }

  public double getTilt() {
    return tilt.get();
  }

  public void disableServos() {
    pan.setDisabled();
    tilt.setDisabled();
  }
}

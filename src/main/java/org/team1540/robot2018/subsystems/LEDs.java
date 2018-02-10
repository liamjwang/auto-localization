package org.team1540.robot2018.subsystems;

import edu.wpi.first.wpilibj.Talon;
import org.team1540.base.ChickenSubsystem;

public class LEDs extends ChickenSubsystem {

  private double brightness;

  private Talon ledController = new Talon(2);

  public LEDs() {}

  public void on(){
    ledController.set(brightness);
  }

  public void off(){
    ledController.set(0);
  }

  public void blink(){
    long timenow = System.currentTimeMillis() / 1000l;
    ledController.set(0);
    long seconds = System.currentTimeMillis() / 1000l;
    if(seconds > timenow + 1000) {
      ledController.set(1);
    }

    seconds = System.currentTimeMillis() / 1000l;
    if(seconds > timenow + 2000) {
      ledController.set(0);
    }
  }

  public void setBrightness(double AssignedBrightness){
    brightness = AssignedBrightness;
  }

  public double getBrightness(){
    return brightness;
  }
}

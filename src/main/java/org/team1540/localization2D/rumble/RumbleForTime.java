package org.team1540.localization2D.rumble;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.TimedCommand;

public class RumbleForTime extends TimedCommand {

  private Joystick joystick;
  private final double rumblePercent;

  public RumbleForTime(Joystick joystick, double rumblePercent, double timeout) {
    super(timeout);
    this.joystick = joystick;
    this.rumblePercent = rumblePercent;
  }

  @Override
  protected void initialize() {
    joystick.setRumble(RumbleType.kLeftRumble, rumblePercent);
    joystick.setRumble(RumbleType.kRightRumble, rumblePercent);
  }

  @Override
  protected void end() {
    joystick.setRumble(RumbleType.kLeftRumble, 0);
    joystick.setRumble(RumbleType.kRightRumble, 0);
  }
}

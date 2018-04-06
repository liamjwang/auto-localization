package org.team1540.robot2018.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.OI;
import org.team1540.robot2018.Robot;

public class JoystickTape extends Command {

  public JoystickTape() {
    requires(Robot.tape);
  }

  @Override
  protected void execute() {
    if (OI.getTapeEnabled()) {
      Robot.tape.set(OI.getTapeAxis() + (OI.getWinchInAxis() * 1.5));
    }
    else {
      Robot.tape.set((OI.getWinchInAxis() * 1.5));
    }
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

}

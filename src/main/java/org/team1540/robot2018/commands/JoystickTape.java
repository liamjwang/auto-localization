package org.team1540.robot2018.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.OI;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class JoystickTape extends Command {

  public JoystickTape() {
    requires(Robot.tape);
  }

  @Override
  protected void execute() {
    if (OI.getTapeEnabled()) {
      Robot.tape.set(OI.getTapeAxis());
    }
    else {
      Robot.tape.stop();
    }
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

}

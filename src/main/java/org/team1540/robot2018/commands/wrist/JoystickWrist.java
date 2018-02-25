package org.team1540.robot2018.commands.wrist;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.OI;
import org.team1540.robot2018.Robot;

public class JoystickWrist extends Command {

  public JoystickWrist() {
    requires(Robot.wrist);
  }

  @Override
  protected void execute() {
    Robot.wrist.set(OI.getWristAxis());
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}

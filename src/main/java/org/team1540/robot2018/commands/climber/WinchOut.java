package org.team1540.robot2018.commands.climber;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class WinchOut extends Command {

  public WinchOut() {
    requires(Robot.winch);
  }

  @Override
  protected void initialize() {
    Robot.winch.set(Tuning.winchOutSpeed);
  }

  @Override
  protected boolean isFinished() {
    return false; //Return true to stop the command
  }

  @Override
  protected void end() {
    Robot.winch.set(0);
  }
}

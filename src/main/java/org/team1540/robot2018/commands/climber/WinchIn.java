package org.team1540.robot2018.commands.climber;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class WinchIn extends Command {

  public WinchIn(){
    requires(Robot.winch);
  }

  @Override
  protected void initialize() {
    Robot.winch.set(Tuning.winchInSpeed);
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

  @Override
  protected void end() {
    Robot.winch.set(0);
  }
}

package org.team1540.robot2018.commands.climber;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class WinchOut extends Command {

  public WinchOut(){
    requires(Robot.climber);
  }

  @Override
  protected void initialize() {
    Robot.climber.setWinch(Tuning.winchOutSpeed);
  }

  @Override
  protected void execute() {
  }

  @Override
  protected boolean isFinished() {
    return false; //Return true to stop the command
  }

  @Override
  protected void end() {
    Robot.climber.setWinch(0);
  }

  @Override
  protected void interrupted() {
  }
}

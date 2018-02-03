package org.team1540.robot2018.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class TapeIn extends Command {

  public TapeIn(){
    requires(Robot.climber);
  }

  @Override
  protected void initialize() {
    Robot.climber.setTape(Tuning.tapeInSpeed);
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
    Robot.climber.stopTape();
  }

  @Override
  protected void interrupted() {
  }
}

package org.team1540.robot2018.commands.climber;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class TapeIn extends Command {

  public TapeIn(){
    requires(Robot.tape);
  }

  @Override
  protected void initialize() {
    Robot.tape.set(Tuning.tapeInSpeed);
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

  @Override
  protected void end() {
    Robot.tape.set(0);
  }
}

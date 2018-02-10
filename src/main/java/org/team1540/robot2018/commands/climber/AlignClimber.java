package org.team1540.robot2018.commands.climber;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.OI;
import org.team1540.robot2018.Robot;

public class AlignClimber extends Command {
  public AlignClimber() {
    requires(Robot.climber);
  }

  @Override
  protected void initialize() {
  }

  @Override
  protected void execute() {
    Robot.climber.align(OI.getCopilotLeftX(), OI.getCopilotLeftY());
  }

  @Override
  protected boolean isFinished() {
    return false; //Return true to stop the command
  }

  @Override
  protected void end() {
  }

  @Override
  protected void interrupted() {
  }
}
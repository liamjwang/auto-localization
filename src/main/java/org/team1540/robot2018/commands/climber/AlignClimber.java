package org.team1540.robot2018.commands.climber;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.OI;
import org.team1540.robot2018.Robot;

public class AlignClimber extends Command {
  public AlignClimber() {
    requires(Robot.climber);
  }

  @Override
  protected void execute() {

    double processedPan =
            OI.isOutsideRange((OI.getCopilotRightX() / 30) + Robot.climber.getPan());
    double processedTilt =
            OI.isOutsideRange((OI.getCopilotRightY() / 30) + Robot.climber.getTilt());

    Robot.climber.align(processedPan, processedTilt);

  }

  @Override
  protected boolean isFinished() {
    return false; //Return true to stop the command
  }
}

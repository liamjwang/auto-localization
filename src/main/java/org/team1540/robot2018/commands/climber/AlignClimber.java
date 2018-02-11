package org.team1540.robot2018.commands.climber;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.base.Utilities;
import org.team1540.robot2018.OI;
import org.team1540.robot2018.Robot;

public class AlignClimber extends Command {
  public AlignClimber() {
    requires(Robot.turret);
  }

  @Override
  protected void execute() {

    double processedPan =
        Utilities.constrain((OI.getCopilotRightX() / 30) + Robot.turret.getPan(), 0, 1);
    double processedTilt =
        Utilities.constrain((OI.getCopilotRightY() / 30) + Robot.turret.getTilt(), 0, 1);

    Robot.turret.set(processedPan, processedTilt);

  }

  @Override
  protected boolean isFinished() {
    return false; //Return true to stop the command
  }
}

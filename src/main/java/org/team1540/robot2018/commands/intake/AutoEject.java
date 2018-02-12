package org.team1540.robot2018.commands.intake;

import edu.wpi.first.wpilibj.command.TimedCommand;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class AutoEject extends TimedCommand {

  public AutoEject(){
    super(Tuning.EjectTime);
    requires(Robot.intake);
  }

  @Override
  protected void execute() {
    Robot.intake.set(Tuning.EjectSpeedA, Tuning.EjectSpeedB);
  }

  @Override
  protected boolean isFinished() {
    return false; //Return true to stop the command
  }

  @Override
  protected void end() {
    Robot.intake.stop();
  }
}

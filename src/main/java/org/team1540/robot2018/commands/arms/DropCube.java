package org.team1540.robot2018.commands.arms;

import edu.wpi.first.wpilibj.command.TimedCommand;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class DropCube extends TimedCommand {


  public DropCube(double timeout) {
    super(timeout);
    requires(Robot.arms);
  }

  @Override
  protected void execute() {
    Robot.arms.set(Tuning.armDropSpeed,
        Tuning.armDropSpeed);
  }
}

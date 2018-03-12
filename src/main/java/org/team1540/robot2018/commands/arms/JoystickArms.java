package org.team1540.robot2018.commands.arms;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.OI;
import org.team1540.robot2018.Robot;

public class JoystickArms extends Command {

  public JoystickArms() {
    requires(Robot.intakeArms);
  }

  @Override
  protected void execute() {
    Robot.intakeArms.setLeft(-OI.getArmLeftAxis() * 0.4);
    Robot.intakeArms.setRight(OI.getArmRightAxis() * 0.4);
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}

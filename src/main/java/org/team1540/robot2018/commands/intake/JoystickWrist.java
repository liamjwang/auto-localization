package org.team1540.robot2018.commands.intake;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.OI;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.RobotUtil;

public class JoystickWrist extends Command {
  private double position = Robot.wrist.getPosition();

  public JoystickWrist() {
    requires(Robot.wrist);
  }

  @Override
  protected void initialize() {
    position = Robot.wrist.getPosition();
  }

  @Override
  protected void execute() {
//    position -= Tuning.wristMult * OI.getCopilotLeftY();
//    double actPos = Robot.wrist.setPosition(position);
//    if (actPos != position) {
//      position += (actPos > position ? 1 : -1) * Tuning.wristBounceBack;
//    }
      Robot.wrist.set(RobotUtil.deadzone(OI.getCopilotLeftY()));
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}

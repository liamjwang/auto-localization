package org.team1540.robot2018.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.robot2018.OI;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class JoystickWrist extends Command {

  private double position = Robot.wrist.getPosition();

  public JoystickWrist() {
    requires(Robot.wrist);
  }

  @Override
  protected void initialize() {
    Robot.wrist.updatePID(); // TODO: 2/13/18 Do I actually need this?
    position = Robot.wrist.getPosition();
  }

  @Override
  protected void execute() {
    position -= Tuning.wristMult * OI.getCopilotLeftTrigger() - OI.getCopilotRightTrigger();
    SmartDashboard.putNumber("Wrist Position Set by Command", position);
    double actPos = Robot.wrist.setPosition(position);
    if (actPos != position) {
      position += (actPos > position ? 1 : -1) * Tuning.wristBounceBack;
    }
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}

package org.team1540.robot2018.commands.wrist;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

/**
 * Moves the wrist forward until it hits a current limit to determine where "down" is.
 */
public class CalibrateWrist extends Command {

  @Override
  protected void initialize() {
    System.out.println("Calibrating wrist...");
    Robot.wrist.set(1);
  }

  @Override
  protected void end() {
    System.out.println(
        "Wrist calibrated. Position before calibration: " + Robot.wrist.getPosition());
    Robot.wrist.setSensorPosition((int) Tuning.wristOutPosition);
  }

  @Override
  protected boolean isFinished() {
    return Robot.wrist.getCurrent() > Tuning.wristStallCurrent;
  }
}

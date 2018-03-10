package org.team1540.robot2018.commands.auto;

import edu.wpi.first.wpilibj.command.TimedCommand;
import org.team1540.robot2018.Robot;

public class TurnLeftBackwardScale extends TimedCommand {
  public TurnLeftBackwardScale(double time) {
    super(time);
    requires(Robot.drivetrain);
  }

  @Override
  protected void initialize() {
    Robot.drivetrain.setLeftVelocity(-0.6 * 750);
    Robot.drivetrain.setRightVelocity(0.2 * 750);
  }

  @Override
  protected void end() {
    Robot.drivetrain.setLeftVelocity(0);
    Robot.drivetrain.setRightVelocity(0);
  }
}

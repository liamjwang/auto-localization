package org.team1540.robot2018.commands.auto;

import edu.wpi.first.wpilibj.command.TimedCommand;
import org.team1540.robot2018.Robot;

public class DriveForward extends TimedCommand {
  public DriveForward(double time) {
    super(time);
    requires(Robot.drivetrain);
  }

  @Override
  protected void initialize() {
    Robot.drivetrain.setLeft(0.4);
    Robot.drivetrain.setRight(0.4);
  }

  @Override
  protected void end() {
    Robot.drivetrain.setLeft(0);
    Robot.drivetrain.setRight(0);
  }
}

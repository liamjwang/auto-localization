package org.team1540.robot2018.commands.climber;

import edu.wpi.first.wpilibj.command.TimedCommand;
import org.team1540.robot2018.Robot;

public class RunClimber extends TimedCommand {
  private double speed;

  public RunClimber(double seconds, double speed) {
    super(seconds);
    this.speed = speed;
    requires(Robot.climber);
  }

  @Override
  protected void initialize() {
  }

  @Override
  protected void execute() {
    Robot.climber.runClimber(speed);
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

  @Override
  protected void end() {
    Robot.climber.stop();
  }

  @Override
  protected void interrupted() {
  }
}

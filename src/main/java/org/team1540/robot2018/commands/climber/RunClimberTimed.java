package org.team1540.robot2018.commands.climber;

import edu.wpi.first.wpilibj.command.TimedCommand;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class RunClimberTimed extends TimedCommand {
  private double speed;

  public RunClimberTimed(double seconds, double speed) {
    super(seconds);
    this.speed = speed;
    requires(Robot.winch);
    requires(Robot.tape);
  }

  @Override
  protected void execute() {
    Robot.winch.set(speed * Tuning.winchMultiplier);
    Robot.tape.set(speed * Tuning.tapeMeasureMultiplier);
  }

  @Override
  protected void end() {
    Robot.tape.set(0);
    Robot.winch.set(0);
  }
}

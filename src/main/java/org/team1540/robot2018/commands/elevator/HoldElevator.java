package org.team1540.robot2018.commands.elevator;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class HoldElevator extends Command {
  private double setpoint;

  public HoldElevator() {
    requires(Robot.elevator);
  }

  @Override
  protected void initialize() {
    setpoint = Robot.elevator.getPosition();
  }

  @Override
  protected void execute() {
    /*
    This command is supposed to HOLD the elevator position: while it should make corrections to a
    certain extent it should never cause the elevator to move a significant amount.
    If the robot is a certain distance away from its original setpoint it just accepts the new
    status quo.
    */
    if (Math.abs(Robot.elevator.getPosition() - setpoint) > Tuning.elevatorMaxDeviation) {
      setpoint = Robot.elevator.getPosition();
    }
    Robot.elevator.setMotionMagicPosition(setpoint);
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}

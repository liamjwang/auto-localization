package org.team1540.robot2018.commands.auto;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.command.TimedCommand;
import org.team1540.robot2018.Robot;

// TODO: Move DriveTimed command to ROOSTER
public class DriveTimed extends TimedCommand {
  private ControlMode mode;
  private double leftValue;
  private double rightValue;

  public DriveTimed(ControlMode mode, double time, double value) {
    super(time);
    this.mode = mode;
    this.leftValue = value;
    this.rightValue = value;
    requires(Robot.drivetrain);
  }

  public DriveTimed(ControlMode mode, double time, double leftValue, double rightValue) {
    super(time);
    this.mode = mode;
    this.leftValue = leftValue;
    this.rightValue = rightValue;
    requires(Robot.drivetrain);
  }

  @Override
  protected void execute() {
    Robot.drivetrain.setLeft(mode, leftValue);
    Robot.drivetrain.setRight(mode, rightValue);
  }

  @Override
  protected void end() {
    Robot.drivetrain.setLeft(mode, 0);
    Robot.drivetrain.setRight(mode, 0);
  }
}

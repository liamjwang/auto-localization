package org.team1540.robot2018.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import org.team1540.robot2018.RobotMap;
import org.team1540.robot2018.OI;
import org.team1540.robot2018.Robot;
import org.team1540.base.ChickenSubsystem;

public class DriveTrain extends ChickenSubsystem {

  TalonSRX left = new TalonSRX(RobotMap.left);
  TalonSRX left2 = new TalonSRX(RobotMap.left2);
  TalonSRX left3 = new TalonSRX(RobotMap.left3);

  TalonSRX right = new TalonSRX(RobotMap.right);
  TalonSRX right2 = new TalonSRX(RobotMap.right2);
  TalonSRX right3 = new TalonSRX(RobotMap.right3);


  public DriveTrain() {
    left.setInverted(false);
    left2.setInverted(false);
    left3.setInverted(false);

    right.setInverted(false);
    right2.setInverted(false);
    right3.setInverted(false);

    left2.set(ControlMode.Follower, left.getDeviceID());
    left3.set(ControlMode.Follower, left.getDeviceID());

    right2.set(ControlMode.Follower, right.getDeviceID());
    right3.set(ControlMode.Follower, right.getDeviceID());
  }

  public void drive() {
    double triggerValue = OI.getDriverLeftTrigger() + -OI.getDriverRightTrigger();

    Robot.drivetrain.left.set(ControlMode.PercentOutput, OI.getDriverLeftX() + triggerValue);
    Robot.drivetrain.right.set(ControlMode.PercentOutput, OI.getDriverRightX() + triggerValue);
  }
}

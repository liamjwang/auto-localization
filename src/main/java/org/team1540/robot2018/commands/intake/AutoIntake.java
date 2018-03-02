package org.team1540.robot2018.commands.intake;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.robot2018.OI;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;


public class AutoIntake extends Command {

  public AutoIntake() {
    super(Tuning.intakeMaxTime);
    requires(Robot.intake);
    requires(Robot.intakeArms);
  }

  @Override
  protected void initialize() {
    setTimeout(Tuning.intakeMaxTime);
    Robot.intake.set(Tuning.intakeSpeedA, Tuning.intakeSpeedB);
  }

  @Override
  protected boolean isFinished() {
    // return ((Robot.intake.getCurrent() >= Tuning.intakeSpikeCurrent)
    //     && this.timeSinceInitialized() > Tuning.intakeMinTime)
    //     || isTimedOut();
    return false;
  }

  @Override
  protected void execute() {
    if (OI.autoIntakeButton.get()) {
      Robot.intakeArms.set(Tuning.intakeArmSpeed);
    } else {
      Robot.intakeArms.set(Tuning.intakeArmHoldSpeed);
    }
  }

  @Override
  protected void end() {
    // if (!isTimedOut()) {
    Robot.intake.set(-Tuning.intakeHoldSpeed, -Tuning.intakeHoldSpeed);
      Robot.intakeArms.set(Tuning.intakeArmHoldSpeed);
    // } else {
    //   Robot.intake.stop();
    //   Robot.intakeArms.set(0);
    // }
  }

  @Override
  protected void interrupted() {
    // Robot.intake.stop();
    // Robot.intakeArms.set(0);
    Robot.intake.set(-Tuning.intakeHoldSpeed, -Tuning.intakeHoldSpeed);
    Robot.intakeArms.set(Tuning.intakeArmHoldSpeed);
  }
}

package org.team1540.robot2018.commands.climber;

import edu.wpi.first.wpilibj.command.Command;
import org.team1540.base.Utilities;
import org.team1540.robot2018.OI;
import org.team1540.robot2018.Robot;
import org.team1540.robot2018.Tuning;

public class AlignClimber extends Command {
  public AlignClimber() {
    requires(Robot.turret);
  }

  @Override
  protected void execute() {

    double processedPan =
        Utilities.constrain((OI.getServoPanAxis() * Tuning.turretJoystickConstant) + Robot.turret.getPan(),
            Tuning.turretPanMin, Tuning.turretPanMax);
    double processedTilt =
        Utilities.constrain((OI.getServoTiltAxis() * Tuning.turretJoystickConstant) + Robot.turret.getTilt(),
            Tuning.turretTiltMin, Tuning.turretTiltMax);

    Robot.turret.set(processedPan, processedTilt);
  }

  @Override
  protected boolean isFinished() {
    return false; //Return true to stop the command
  }
}

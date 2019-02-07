package org.team1540.localization2D.robot.commands.drivetrain;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Random;
import org.team1540.localization2D.robot.OI;
import org.team1540.localization2D.robot.Robot;

public class NetworkTablesTest extends Command {

  private String[] keys;
  private Random rand = new Random();

  public NetworkTablesTest() {
    keys = new String[500];
    for (int ii = 0; ii < 500; ii++) {
      keys[ii] = "test/testKey"+ii;
    }
  }

  @Override
  protected void execute() {
    for (String key : keys) {
      SmartDashboard.putNumber(key, rand.nextDouble());
    }
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}

// package org.team1540.robot2018.commands.groups;
//
// import edu.wpi.first.wpilibj.command.CommandGroup;
// import org.team1540.robot2018.Tuning;
// import org.team1540.robot2018.commands.auto.DriveForward;
// import org.team1540.robot2018.commands.elevator.MoveElevatorToPosition;
// import org.team1540.robot2018.commands.wrist.MoveWristToPosition;
//
// public class ClimbSequence extends CommandGroup {
//   public ClimbSequence() {
//     addSequential(new MoveElevatorToPosition(Tuning.elevatorRungPosition));
//     addSequential(new DriveForward(Tuning.climbingDriveFwdSecs));
//     addSequential(new MoveWristToPosition(Tuning.wristTransitPosition));
//     addSequential(new MoveElevatorToPosition(Tuning.elevatorGroundPosition));
//     addSequential(new MoveWristToPosition(Tuning.wristBackPosition));
//   }
// }

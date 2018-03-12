package org.team1540.robot2018;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//TODO: Migrate this to ROOSTER as it's already generic

/**
 * Class to load motion profiles from CSVs on robot initialization so that they aren't loaded at the
 * start of autonomous.
 */
public class CSVProfileManager {
  public Map<String, DriveProfile> trajectories;

  /**
   * Create a new {@code CSVProfileManager} that pulls profiles from the given directory.
   *
   * @param profileDirectory The directory that profiles should be pulled from.
   *
   * @throws RuntimeException If an error occurs while reading profile data.
   * @throws IllegalArgumentException If {@code profileDirectory} is not a directory, or if it
   *     does not contain 1 or more properly named profile CSV pairs (named {@code name_left.csv}
   *     and {@code name_right.csv}, where {@code name} is the name of the profile).
   */
  public CSVProfileManager(File profileDirectory) {
    if (!profileDirectory.isDirectory()) {
      throw new IllegalArgumentException("Parameter profileDirectory must be a directory");
    }

    File[] leftFiles = profileDirectory.listFiles((file) -> file.getName().endsWith("_left.csv"));
    File[] rightFiles = profileDirectory.listFiles((file) -> file.getName().endsWith("_right.csv"));

    if (leftFiles == null || rightFiles == null) {
      // according to listFiles() docs, it will only return null if the file isn't a directory
      // (which we've already checked) or if an IO error occurs. Thus, if leftFiles or rightFiles is
      // null we know an IO error happened. Not throwing an IOException because checked exceptions
      // are bad, in constructors even more so.
      throw new RuntimeException("IO exception occured while reading files");
    }

    Set<String> profileNames = new HashSet<>();

    for (File file : leftFiles) {
      profileNames.add(file.getName().substring(0, file.getName().length() - "_left.csv".length()));
    }

    for (File file : rightFiles) {
      profileNames.add(file.getName().substring(0,
          file.getName().length() - "_right.csv".length()));
    }

    // initialize the map once we know the number of profiles so it doesn't expand.
    // Why? p e r f o r m a n c e

    trajectories = new HashMap<>(profileNames.size());

    for (String name : profileNames) {
      File leftFile = Arrays.stream(leftFiles)
          .filter(file -> file.getName().equals(name + "_left.csv"))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException(
              "File " + name + "_left.csv" + " does not exist"));
      File rightFile = Arrays.stream(rightFiles)
          .filter(file -> file.getName().equals(name + "_right.csv"))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException(
              "File " + name + "_right.csv" + " does not exist"));

      Trajectory left = Pathfinder.readFromCSV(leftFile);
      Trajectory right = Pathfinder.readFromCSV(rightFile);

      trajectories.put(name, new DriveProfile(left, right));
    }
  }

  public DriveProfile getTrajectory(String name) {
    return trajectories.get(name);
  }

  /**
   * Data class for holding left and right trajectories in one object.
   */
  public static class DriveProfile {
    private final Trajectory left;
    private final Trajectory right;

    public DriveProfile(Trajectory left, Trajectory right) {
      this.left = left;
      this.right = right;
    }

    public Trajectory getLeft() {
      return left;
    }

    public Trajectory getRight() {
      return right;
    }
  }
}

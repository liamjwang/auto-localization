package org.team1540.localization2D.utils;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import com.google.common.collect.EvictingQueue;
import java.util.Queue;
import org.team1540.localization2D.datastructures.Pose2D;

public class PoseSE2RollingAverage {

  private Queue<Pose2D> poseQueue;

  public PoseSE2RollingAverage(int numElements) {
    this.poseQueue = EvictingQueue.create(numElements);
  }

  public void addPose(Pose2D pose) {
    poseQueue.add(pose);
  }

  public Pose2D getAverage() {
    int listSize = poseQueue.size();
    if (listSize == 0) {
      return null;
    }
    double sumX = 0;
    double sumY = 0;
    double sumSin = 0;
    double sumCos = 0;
    for (Pose2D pose : poseQueue) {
      sumX += pose.x;
      sumY += pose.y;
      sumSin += sin(pose.theta);
      sumCos += cos(pose.theta);
    }
    return new Pose2D(
        sumX / listSize, sumY / listSize, atan2(sumSin / listSize, sumCos / listSize));
  }
}

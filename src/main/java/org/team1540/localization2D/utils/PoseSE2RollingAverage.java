package org.team1540.localization2D.utils;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import com.google.common.collect.EvictingQueue;
import java.util.Queue;
import org.team1540.localization2D.datastructures.PoseSE2;

public class PoseSE2RollingAverage {

  private Queue<PoseSE2> poseQueue;

  public PoseSE2RollingAverage(int numElements) {
    this.poseQueue = EvictingQueue.create(numElements);
  }

  public void addPose(PoseSE2 pose) {
    poseQueue.add(pose);
  }

  public PoseSE2 getAverage() {
    int listSize = poseQueue.size();
    if (listSize == 0) {
      return null;
    }
    double sumX = 0;
    double sumY = 0;
    double sumSin = 0;
    double sumCos = 0;
    for (PoseSE2 pose : poseQueue) {
      sumX += pose.x;
      sumY += pose.y;
      sumSin += sin(pose.theta);
      sumCos += cos(pose.theta);
    }
    return new PoseSE2(
        sumX / listSize, sumY / listSize, atan2(sumSin / listSize, sumCos / listSize));
  }
}

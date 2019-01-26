package org.team1540.localization2D;

import org.apache.commons.math3.geometry.euclidean.threed.Line;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class LimelightLocalization {

  private static double TOLERANCE = 0.0001;

  private static double LIMELIGHT_HORIZONTAL_FOV = Math.toRadians(59.6);
  private static double LIMELIGHT_VERTICAL_FOV = Math.toRadians(45.7);

  public static Vector2D anglesFromScreenSpace(Vector2D normalizedScreenPoint, double hoz_fov, double vert_fov) {
    //http://docs.limelightvision.io/en/latest/theory.html#from-pixels-to-angles
    double vpw = 2.0 * Math.tan(hoz_fov / 2);
    double vph = 2.0 * Math.tan(vert_fov / 2);

    double screenSpaceX = vpw / 2.0 * -normalizedScreenPoint.getX(); // X is negated
    double screenSpaceY = vph / 2.0 * normalizedScreenPoint.getY();

    return new Vector2D(
        Math.PI / 2 - Math.atan2(1, screenSpaceX),
        Math.PI / 2 - Math.atan2(1, screenSpaceY)
    );
  }

  public static Line lineFromScreenAngles(Vector2D screenAngles, Vector3D cameraPosition, Rotation cameraRotation) {
    double yaw = screenAngles.getX();
    double pitch = screenAngles.getY();

    Vector3D pixelVector = Vector3D.PLUS_I;

    Rotation pitchRot = new Rotation(Vector3D.PLUS_J, pitch, RotationConvention.FRAME_TRANSFORM);
    Rotation yawRot = new Rotation(Vector3D.PLUS_K, yaw, RotationConvention.FRAME_TRANSFORM);

    pixelVector = pitchRot.applyTo(pixelVector);
    pixelVector = yawRot.applyTo(pixelVector);

    Vector3D pixelVectorRotated = cameraRotation.applyTo(pixelVector);

    return new Line(cameraPosition, cameraPosition.add(pixelVectorRotated), TOLERANCE);
  }

  public static Vector3D getIntersection(Line line, double height) {
    return new Plane(Vector3D.PLUS_K.scalarMultiply(height), Vector3D.PLUS_K, TOLERANCE)
        .intersection(line);
  }

  private static Vector3D midpoint(Vector3D a, Vector3D b) {
    return new Vector3D(
        (a.getX() + b.getX()) / 2,
        (a.getY() + b.getY()) / 2,
        (a.getZ() + b.getZ()) / 2
    );
  }

  private static double angleFromVisionTargets(Vector2D left, Vector2D right) {
    Vector2D difference = left.subtract(right);
    double atan = Math.atan(difference.getY() / difference.getX())+Math.PI/2;
    if (atan > Math.PI/2) {
      atan = atan-Math.PI;
    }
    return atan;
  }

  private static Vector2D xyFromVector3D(Vector3D vec) {
    return new Vector2D(vec.getX(), vec.getY());
  }

  public static Transform poseFromTwoCamPoints(Vector2D leftAngles, Vector2D rightAngles, double planeHeight, Vector3D cameraPosition, Rotation cameraRotation) {

    Vector3D leftPoint = getIntersection(lineFromScreenAngles(anglesFromScreenSpace(leftAngles, LIMELIGHT_HORIZONTAL_FOV, LIMELIGHT_VERTICAL_FOV), cameraPosition, cameraRotation), planeHeight);
    Vector3D rightPoint = getIntersection(lineFromScreenAngles(anglesFromScreenSpace(rightAngles, LIMELIGHT_HORIZONTAL_FOV, LIMELIGHT_VERTICAL_FOV), cameraPosition, cameraRotation), planeHeight);

    return new Transform(
        midpoint(leftPoint, rightPoint),
        new Rotation(RotationOrder.XYZ, RotationConvention.FRAME_TRANSFORM, 0, 0, angleFromVisionTargets(
            xyFromVector3D(leftPoint),
            xyFromVector3D(rightPoint))));
  }
}

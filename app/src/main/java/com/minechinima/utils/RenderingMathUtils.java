package com.minechinima.utils;

import org.joml.*;

import java.lang.Math;
import java.util.Optional;

public class RenderingMathUtils {
    /**
     * Project the screen coordinates onto a given plane in world space. The plane is composed of the normal and planeOrigin.
     * @param cameraPos the position of the camera from which the ray will be projected of.
     * @param screenX screen x coordinate, needs to be in a range between -1 and 1
     * @param screenY screen y coordinate, needs to be in a range between -1 and 1
     * @param planeNormal
     * @param  planeOrigin
     * @return empty Optional if no intersection was found
     */
    public static Optional<Vector3d> projectScreenOntoPlane(Vector3d cameraPos, Matrix4d projection, Matrix3d viewRotation,
                                                            double screenX, double screenY,
                                                            Vector3d planeNormal, Vector3d planeOrigin) {
        Matrix4d cameraToWorld = new Matrix4d(projection);
        cameraToWorld.mul(new Matrix4d(viewRotation).invert()).invert();
        planeNormal = new Vector3d(planeNormal).normalize();

        Vector4d mouse = new Vector4d(screenX, screenY, 0, 1);
        cameraToWorld.transform(mouse);
        Vector3d ray = new Vector3d(mouse.x, mouse.y, mouse.z).normalize();

        double factor = Intersectiond.intersectRayPlane(cameraPos, ray, planeOrigin, planeNormal, 0.000000001);

        /*
         * -1 means no intersection, try with an inverted plane normal,
         * since the orientation of the plane does not matter for projection here
         */
        if (factor == -1) {
            factor = Intersectiond.intersectRayPlane(cameraPos, ray, planeOrigin, new Vector3d(planeNormal).mul(-1), 0.000000001);
        }

        if (factor != -1) {
            Vector3d intersectionPoint = new Vector3d(cameraPos).add(ray.mul(factor));
            return Optional.of(intersectionPoint);
        }
        return Optional.empty();
    }

    /**
     * @param direction
     * @return the yaw for this direction vector in degrees.
     */
    public static float getYaw(Vector3f direction) {
        return (float) getYaw(new Vector3d(direction));
    }

    /**
     * @param direction
     * @return the pitch for this direction vector in degrees.
     */
    public static float getPitch(Vector3f direction) {
        return (float) getPitch(new Vector3d(direction));
    }

    /**
     * @param direction
     * @return the yaw for this direction vector in degrees.
     */
    public static double getYaw(Vector3d direction) {
        double yaw = Math.atan2(-direction.x, direction.z);
        yaw = Math.toDegrees(yaw);
        if (yaw < -180) {
            yaw += 360;
        } else if (yaw > 180) {
            yaw -= 360;
        }
        return -yaw;
    }

    /**
     * @param direction
     * @return the pitch for this direction vector in degrees.
     */
    public static double getPitch(Vector3d direction) {
        double pitch = Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z));
        return -Math.toDegrees(pitch);
    }

    public enum Facing
    {
        ROTATE_XYZ("rotate_xyz"),
        ROTATE_Y("rotate_y"),
        LOOKAT_XYZ("lookat_xyz", true, false),
        LOOKAT_Y("lookat_y", true, false),
        LOOKAT_DIRECTION("lookat_direction", true, true),
        DIRECTION_X("direction_x", false, true),
        DIRECTION_Y("direction_y", false, true),
        DIRECTION_Z("direction_z", false, true),
        EMITTER_XY("emitter_transform_xy"),
        EMITTER_XZ("emitter_transform_xz"),
        EMITTER_YZ("emitter_transform_yz");

        public final String id;
        public final boolean isLookAt;
        public final boolean isDirection;

        public static Facing fromString(String string)
        {
            for (Facing facing : values())
            {
                if (facing.id.equals(string))
                {
                    return facing;
                }
            }

            return null;
        }

        Facing(String id, boolean isLookAt, boolean isDirection)
        {
            this.id = id;
            this.isLookAt = isLookAt;
            this.isDirection = isDirection;
        }

        Facing(String id)
        {
            this(id, false, false);
        }
    }
}

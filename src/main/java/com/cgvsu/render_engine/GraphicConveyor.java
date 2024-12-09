package com.cgvsu.render_engine;

import com.cgvsu.math.Linal;
import com.cgvsu.nmath.Matrix4x4;
import com.cgvsu.nmath.Vector2f;
import com.cgvsu.nmath.Vector3f;
import com.cgvsu.nmath.Vector4f;

public class GraphicConveyor {
    public static float cos(float angle){
        return (float) Math.cos(angle);
    }

    public static float sin(float angle){
        return (float) Math.sin(angle);
    }

    public static Matrix4x4 scale(float sx, float sy, float sz) {
        return new Matrix4x4(new float[]{
                sx, 0, 0, 0,
                0, sy, 0, 0,
                0, 0, sz, 0,
                0, 0, 0,  1
        });
    }

    public static Matrix4x4 rotate(float fi, float psi, float theta) {
        Matrix4x4 rotateZ = new Matrix4x4(new float[]{
                cos(fi), sin(fi), 0, 0,
                -sin(fi), cos(fi), 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        });
        Matrix4x4 rotateY = new Matrix4x4(new float[]{
                cos(psi), 0, sin(psi), 0,
                0, 1, 0, 0,
                -sin(psi), 0, cos(psi), 0,
                0, 0, 0, 1
        });
        Matrix4x4 rotateX = new Matrix4x4(new float[]{
                1, 0, 0, 0,
                0, cos(theta), sin(theta), 0,
                0, -sin(theta), cos(theta), 0,
                0, 0, 0, 1
        });
        return rotateZ.multiplyMM(rotateY).multiplyMM(rotateX);
    }

    public static Matrix4x4 transform(float tx, float ty, float tz) {
        return new Matrix4x4(new float[]{
                1, 0, 0, tx,
                0, 1, 0, ty,
                0, 0, 1, tz,
                0, 0, 0, 1
        });
    }

    public static Matrix4x4 lookAt(Vector3f eye, Vector3f target) {
        return lookAt(eye, target, new Vector3f(0F, 1.0F, 0F));
    }

    public static Matrix4x4 lookAt(Vector3f eye, Vector3f target, Vector3f up) {
        Vector3f resultX;
        Vector3f resultY;
        Vector3f resultZ;

        resultZ = Linal.subtract(target, eye);
        resultX = Linal.crossProduct(up, resultZ);
        resultY = Linal.crossProduct(resultZ, resultX);

        resultX.normalize();
        resultY.normalize();
        resultZ.normalize();

        float[] matrix = new float[]{
                resultX.x(), resultX.y(), resultX.z(), -resultX.dotProduct(eye),
                resultY.x(), resultY.y(), resultY.z(), -resultY.dotProduct(eye),
                resultZ.x(), resultZ.y(), resultZ.z(), -resultZ.dotProduct(eye),
                0, 0, 0, 1};
        return new Matrix4x4(matrix);
    }

    public static Vector3f getRay(Vector3f lightSource, Vector3f target) {
        Vector3f result;
        result = Linal.subtract(target, lightSource);
        result.normalize();
        return result;
    }

    public static Matrix4x4 perspective(
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        float tangentMinusOnDegree = (float) (1.0F / (Math.tan(fov * 0.5F)));
        Matrix4x4 result = new Matrix4x4(new float[]{tangentMinusOnDegree, 0, 0, 0,
                0, tangentMinusOnDegree / aspectRatio, 0, 0,
                0, 0, (farPlane + nearPlane) / (farPlane - nearPlane), 2 * (nearPlane * farPlane) / (nearPlane - farPlane),
                0, 0, 1, 0});
        return result;
    }

    public static Vector2f vertexToPoint(final Vector3f vertex, final int width, final int height) {
        return new Vector2f(vertex.x() * width + width / 2.0F, -vertex.y() * height + height / 2.0F);
    }
}

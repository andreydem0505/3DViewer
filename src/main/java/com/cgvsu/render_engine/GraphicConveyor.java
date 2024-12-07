package com.cgvsu.render_engine;

import com.cgvsu.math.Linal;
import com.cgvsu.nmath.Matrix4x4;
import com.cgvsu.nmath.Vector2f;
import com.cgvsu.nmath.Vector3f;

public class GraphicConveyor {

    public static Matrix4x4 rotateScaleTranslate() {
        float[] matrix = new float[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1};
        return new Matrix4x4(matrix);
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
                resultX.x(), resultY.x(), resultZ.x(), 0,
                resultX.y(), resultY.y(), resultZ.y(), 0,
                resultX.z(), resultY.z(), resultZ.z(), 0,
                -resultX.dotProduct(eye), -resultY.dotProduct(eye), -resultZ.dotProduct(eye), 1};
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
                                                     0, 0, (farPlane + nearPlane) / (farPlane - nearPlane), 1,
                                                     0, 0, 2 * (nearPlane * farPlane) / (nearPlane - farPlane), 0});
        return result;
    }

    public static Vector3f multiplyMatrix4ByVector3(final Matrix4x4 matrix, final Vector3f vertex) {
        final float x = (vertex.x() * matrix.get(0, 0)) + (vertex.y() * matrix.get(1, 0)) + (vertex.z() * matrix.get(2, 0)) + matrix.get(3, 0);
        final float y = (vertex.x() * matrix.get(0, 1)) + (vertex.y() * matrix.get(1, 1)) + (vertex.z() * matrix.get(2, 1)) + matrix.get(3, 1);
        final float z = (vertex.x() * matrix.get(0, 2)) + (vertex.y() * matrix.get(1, 2)) + (vertex.z() * matrix.get(2, 2)) + matrix.get(3, 2);
        final float w = (vertex.x() * matrix.get(0, 3)) + (vertex.y() * matrix.get(1, 3)) + (vertex.z() * matrix.get(2, 3)) + matrix.get(3, 3);
        return new Vector3f(x / w, y / w, z / w);
    }

    public static Vector2f vertexToPoint(final Vector3f vertex, final int width, final int height) {
        return new Vector2f(vertex.x() * width + width / 2.0F, -vertex.y() * height + height / 2.0F);
    }
}

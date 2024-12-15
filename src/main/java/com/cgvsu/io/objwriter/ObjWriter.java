package com.cgvsu.io.objwriter;

import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import com.cgvsu.nmath.Vector2f;
import com.cgvsu.nmath.Vector3f;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ObjWriter {
    private static final String OBJ_VERTEX_TOKEN = "v";
    private static final String OBJ_TEXTURE_TOKEN = "vt";
    private static final String OBJ_NORMAL_TOKEN = "vn";
    private static final String OBJ_FACE_TOKEN = "f";

    public void write(Model model, String filename) {
        File file = new File(filename);
        if (!createDir(file.getParentFile()))
            return;
        if (!createFile(file))
            return;
        try (PrintWriter writer = new PrintWriter(file)) {
            model.vertices.forEach(v -> writer.println(vertexToString(v)));
            model.textureVertices.forEach(v -> writer.println(textureVertexToString(v)));
            model.normals.forEach(v -> writer.println(normalToString(v)));
            model.polygons.forEach(v -> writer.println(polygonToString(v)));
        } catch (IOException e) {
            System.out.println("Error while writing file");
        }
    }

    public static String formatOutput(
            Model model,
            String separator        // Разделитель строк
    ) {
        StringBuilder result = new StringBuilder();

        // Форматируем вершины
        for (Vector3f vertex : model.getVertices()) {
            result.append(String.format("v %.1f %.1f %.1f", vertex.x(), vertex.y(), vertex.y()))
                    .append(separator);
        }

        for (Vector2f texture : model.getTextureVertices()) {
            result.append(String.format("vt %.1f %.1f", texture.x(), texture.y()))
                    .append(separator);
        }

        for (Vector3f vertex : model.getNormals()) {
            result.append(String.format("vn %.1f %.1f %.1f", vertex.x(), vertex.y(), vertex.z()))
                    .append(separator);
        }

        // Форматируем грани
        for (Polygon face : model.getPolygons()) {
            result.append("f");
            //            for (int vertexIndex : face.getVertexIndices()) {
            boolean textureIsEmpty = true;
            for (int i = 0; i < face.getVertexIndices().size(); i++) {
                int vertexIndex = face.getVertexIndices().get(i);
                result.append(" ").append(vertexIndex + 1); // т.к в файле obj вершины считаются от 1
                if (!face.getTextureVertexIndices().isEmpty()) {
                    result.append("/").append(face.getTextureVertexIndices().get(i) + 1); // т.к в файле obj вершины считаются от 1
                    textureIsEmpty = false;
                }
                if (!face.getNormalIndices().isEmpty()) {
                    result.append(textureIsEmpty ? "//" : "/").append(face.getNormalIndices().get(i) + 1); // т.к в файле obj вершины считаются от 1
                }
            }
            result.append(separator);
        }

        // Удаляем лишний разделитель в конце и возвращаем результат
        return result.toString().trim();
    }


    private boolean createDir(File directory) {
        if (directory != null && !directory.exists() && !directory.mkdirs()) {
            System.out.println("Couldn't create dir: " + directory);
            return false;
        }
        return true;
    }

    private boolean createFile(File file) {
        try {
            if (!file.createNewFile())
                System.out.println("Warning: " + file.getName() + " already exists");
        } catch (IOException e) {
            System.out.println("Error while creating the file");
            return false;
        }
        return true;
    }

    protected String vertexToString(Vector3f vector) {
        return OBJ_VERTEX_TOKEN + " " + vector.x() + " " + vector.y() + " " + vector.z();
    }

    protected String textureVertexToString(Vector2f vector) {
        return OBJ_TEXTURE_TOKEN + " " + vector.x() + " " + vector.y();
    }

    protected String normalToString(Vector3f vector) {
        return OBJ_NORMAL_TOKEN + " " + vector.x() + " " + vector.y() + " " + vector.z();
    }

    protected String polygonToString(Polygon polygon) {
        StringBuilder stringBuilder = new StringBuilder(OBJ_FACE_TOKEN);
        List<Integer> vertexIndices = polygon.getVertexIndices();
        List<Integer> textureVertexIndices = polygon.getTextureVertexIndices();
        List<Integer> normalIndices = polygon.getNormalIndices();
        boolean hasTextures = textureVertexIndices.size() == vertexIndices.size();
        boolean hasNormals = normalIndices.size() == vertexIndices.size();
        for (int i = 0; i < vertexIndices.size(); i++) {
            stringBuilder.append(" ")
                    .append(getFormattedIndex(vertexIndices, i));
            if (hasNormals) {
                stringBuilder.append("/");
                if (hasTextures) {
                    stringBuilder.append(getFormattedIndex(textureVertexIndices, i))
                            .append("/")
                            .append(getFormattedIndex(normalIndices, i));
                } else {
                    stringBuilder.append("/")
                            .append(getFormattedIndex(normalIndices, i));
                }
            } else {
                if (hasTextures) {
                    stringBuilder.append("/")
                            .append(getFormattedIndex(textureVertexIndices, i));
                }
            }
        }
        return stringBuilder.toString();
    }

    private int getFormattedIndex(List<Integer> indices, int index) {
        return indices.get(index) + 1;
    }
}

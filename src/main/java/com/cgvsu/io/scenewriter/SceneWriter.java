package com.cgvsu.io.scenewriter;

import com.cgvsu.model.ModelPrepared;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class SceneWriter {
    /**Require '/' in the end!*/
    public static void writeModelsData(List<ModelPrepared> models, String path) throws IOException {
        Path sceneFilename = Path.of(path + "Scene.json");

        for (ModelPrepared modelPrepared : models) {
            File textureFile = new File(path + modelPrepared.getName() + "_texture.png");
            ImageIO.write(modelPrepared.getTexture(), "png", textureFile);
        }
    }
}

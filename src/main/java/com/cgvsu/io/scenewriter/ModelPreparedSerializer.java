package com.cgvsu.io.scenewriter;

import com.cgvsu.model.ModelPrepared;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class ModelPreparedSerializer implements JsonSerializer<ModelPrepared> {
    @Override
    public JsonElement serialize(ModelPrepared src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("modelName", src.getName());
        object.add("modelState", context.serialize(src.model));
        object.add("renderMode", context.serialize(src.getRenderMode()));
        object.addProperty("currentModeCode", src.getCurrentModeCode());
//        object.addProperty("currentColorCode", Integer.toHexString(src.getCurrentColorCode()));
        return null;
    }
}

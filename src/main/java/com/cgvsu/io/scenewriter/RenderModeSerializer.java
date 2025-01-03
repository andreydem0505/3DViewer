package com.cgvsu.io.scenewriter;

import com.cgvsu.render_engine.RenderMode;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class RenderModeSerializer implements JsonSerializer<RenderMode> {
    @Override
    public JsonElement serialize(RenderMode src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("grid", src.grid);
        object.addProperty("color", Integer.toHexString(src.color.getRGB()));
        object.addProperty("light", src.light);

        return object;
    }
}

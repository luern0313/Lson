package cn.luern0313.lson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * 被 luern0313 创建于 2020/7/29.
 */

public class LsonObjectUtil
{
    private JsonObject jsonObject;

    public LsonObjectUtil()
    {
        this(JsonNull.INSTANCE);
    }

    public LsonObjectUtil(JsonElement json)
    {
        if(json.isJsonObject())
            this.jsonObject = json.getAsJsonObject();
        else
            this.jsonObject = new JsonObject();
    }

    public boolean has(String key)
    {
        return jsonObject.has(key);
    }

    public JsonElement get(String key)
    {
        return jsonObject.get(key);
    }

    public String[] getKeys()
    {
        return jsonObject.keySet().toArray(new String[0]);
    }

    public boolean getAsBoolean(String key)
    {
        return getAsBoolean(key, false);
    }

    public boolean getAsBoolean(String key, boolean def)
    {
        JsonElement jsonElement = jsonObject.get(key);
        if(jsonElement == null || !(jsonElement instanceof JsonPrimitive && ((JsonPrimitive) jsonElement).isBoolean()))
            return def;
        else return jsonElement.getAsBoolean();
    }

    public String getAsString(String key)
    {
        return getAsString(key, "");
    }

    public String getAsString(String key, String def)
    {
        JsonElement jsonElement = jsonObject.get(key);
        if(jsonElement == null || !(jsonElement instanceof JsonPrimitive && ((JsonPrimitive) jsonElement).isString()))
            return def;
        else return jsonElement.getAsString();
    }

    public int getAsInt(String key)
    {
        return getAsInt(key, 0);
    }

    public int getAsInt(String key, int def)
    {
        JsonElement jsonElement = jsonObject.get(key);
        if(jsonElement == null || !(jsonElement instanceof JsonPrimitive && ((JsonPrimitive) jsonElement).isNumber()))
            return def;
        else return jsonElement.getAsInt();
    }

    public LsonArrayUtil getAsJsonArray(String key)
    {
        JsonElement jsonElement = jsonObject.get(key);
        if(!(jsonElement instanceof JsonArray))
            return new LsonArrayUtil();
        else
            return new LsonArrayUtil(jsonElement.getAsJsonArray());
    }

    public LsonObjectUtil getAsJsonObject(String key)
    {
        JsonElement jsonElement = jsonObject.get(key);
        if(!(jsonElement instanceof JsonObject))
            return new LsonObjectUtil();
        else
            return new LsonObjectUtil(jsonElement.getAsJsonObject());
    }

    public JsonObject getJsonObject()
    {
        return jsonObject;
    }
}

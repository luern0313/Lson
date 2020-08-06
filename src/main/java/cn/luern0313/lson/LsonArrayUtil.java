package cn.luern0313.lson;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * 被 luern0313 创建于 2020/7/29.
 */

public class LsonArrayUtil
{
    private JsonArray jsonArray;

    public LsonArrayUtil()
    {
        this(JsonNull.INSTANCE);
    }

    public LsonArrayUtil(JsonElement json)
    {
        if(json.isJsonArray())
            this.jsonArray = json.getAsJsonArray();
        else
            this.jsonArray = new JsonArray();
    }

    public int size()
    {
        return jsonArray.size();
    }

    public JsonElement get(int key)
    {
        return jsonArray.get(key);
    }

    public boolean getAsBoolean(int key)
    {
        return getAsBoolean(key, false);
    }

    public boolean getAsBoolean(int key, boolean def)
    {
        JsonElement jsonElement = jsonArray.get(key);
        if(jsonElement == null || !(jsonElement instanceof JsonPrimitive && ((JsonPrimitive) jsonElement).isBoolean()))
            return def;
        else return jsonElement.getAsBoolean();
    }

    public String getAsString(int key)
    {
        return getAsString(key, "");
    }

    public String getAsString(int key, String def)
    {
        JsonElement jsonElement = jsonArray.get(key);
        if(jsonElement == null || !(jsonElement instanceof JsonPrimitive && ((JsonPrimitive) jsonElement).isString()))
            return def;
        else return jsonElement.getAsString();
    }

    public int getAsInt(int key)
    {
        return getAsInt(key, 0);
    }

    public int getAsInt(int key, int def)
    {
        JsonElement jsonElement = jsonArray.get(key);
        if(jsonElement == null || !(jsonElement instanceof JsonPrimitive && ((JsonPrimitive) jsonElement).isNumber()))
            return def;
        else return jsonElement.getAsInt();
    }

    public LsonArrayUtil getAsJsonArray(int key)
    {
        JsonElement jsonElement = jsonArray.get(key);
        if(!(jsonElement instanceof JsonArray))
            return new LsonArrayUtil();
        else
            return new LsonArrayUtil(jsonElement.getAsJsonArray());
    }

    public LsonObjectUtil getAsJsonObject(int key)
    {
        JsonElement jsonElement = jsonArray.get(key);
        if(!(jsonElement instanceof JsonObject))
            return new LsonObjectUtil();
        else
            return new LsonObjectUtil(jsonElement.getAsJsonObject());
    }

    public JsonArray getJsonArray()
    {
        return jsonArray;
    }
}

package cn.luern0313.lson;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * 被 luern0313 创建于 2020/8/11.
 */

public class LsonPrimitiveUtil
{
    private JsonPrimitive jsonPrimitive;

    public LsonPrimitiveUtil(JsonElement json)
    {
        if(json.isJsonPrimitive())
            this.jsonPrimitive = json.getAsJsonPrimitive();
        else
            this.jsonPrimitive = new JsonPrimitive("");
    }

    public JsonElement getJsonElement()
    {
        return jsonPrimitive;
    }

    public boolean isBoolean()
    {
        return jsonPrimitive.isBoolean();
    }

    public boolean getAsBoolean()
    {
        return jsonPrimitive.getAsBoolean();
    }

    public boolean isString()
    {
        return jsonPrimitive.isBoolean();
    }

    public String getAsString()
    {
        return jsonPrimitive.getAsString();
    }

    public boolean isNumber()
    {
        return jsonPrimitive.isNumber();
    }

    public int getAsInt()
    {
        return jsonPrimitive.getAsInt();
    }

    public short getAsShort()
    {
        return jsonPrimitive.getAsShort();
    }

    public float getAsFloat()
    {
        return jsonPrimitive.getAsFloat();
    }

    public double getAsDouble()
    {
        return jsonPrimitive.getAsDouble();
    }

    public long getAsLong()
    {
        return jsonPrimitive.getAsLong();
    }

}

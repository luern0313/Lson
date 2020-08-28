package cn.luern0313.lson.element;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 被 luern0313 创建于 2020/7/29.
 */

public class LsonObject extends LsonElement
{
    private LinkedHashMap<String, LsonElement> map;

    public LsonObject()
    {
        this(new LinkedHashMap<>());
    }

    public LsonObject(LinkedHashMap<String, LsonElement> map)
    {
        this.map = map;
    }

    public boolean has(String key)
    {
        return map.containsKey(key);
    }

    public LsonElement get(String key)
    {
        LsonElement lsonElement = map.get(key);
        if(lsonElement != null)
            return lsonElement;
        return LsonNull.getJsonNull();
    }

    public String[] getKeys()
    {
        return map.keySet().toArray(new String[0]);
    }

    public void put(String key, LsonElement value)
    {
        map.put(key, value == null ? LsonNull.getJsonNull() : value);
    }

    public boolean getAsBoolean(String key)
    {
        return getAsBoolean(key, false);
    }

    public boolean getAsBoolean(String key, boolean def)
    {
        LsonElement lsonElement = map.get(key);
        if(lsonElement == null || !(lsonElement.isLsonPrimitive() && lsonElement.getAsLsonPrimitive().isBoolean()))
            return def;
        else
            return lsonElement.getAsLsonPrimitive().getAsBoolean();
    }

    public String getAsString(String key)
    {
        return getAsString(key, "");
    }

    public String getAsString(String key, String def)
    {
        LsonElement lsonElement = map.get(key);
        if(lsonElement == null || !(lsonElement.isLsonPrimitive() && lsonElement.getAsLsonPrimitive().isString()))
            return def;
        else
            return lsonElement.getAsLsonPrimitive().getAsString();
    }

    public int getAsInt(String key)
    {
        return getAsInt(key, 0);
    }

    public int getAsInt(String key, int def)
    {
        LsonElement lsonElement = map.get(key);
        if(lsonElement == null || !(lsonElement instanceof LsonPrimitive && lsonElement.getAsLsonPrimitive().isNumber()))
            return def;
        else
            return lsonElement.getAsLsonPrimitive().getAsInt();
    }

    public LsonArray getAsJsonArray(String key)
    {
        LsonElement lsonElement = map.get(key);
        if(!lsonElement.isLsonArray())
            return new LsonArray();
        else
            return lsonElement.getAsLsonArray();
    }

    public LsonObject getAsJsonObject(String key)
    {
        LsonElement lsonElement = map.get(key);
        if(!lsonElement.isLsonObject())
            return new LsonObject();
        else
            return lsonElement.getAsLsonObject();
    }

    @Override
    public String toString()
    {
        String[] keys = map.keySet().toArray(new String[0]);
        StringBuilder stringBuilder = new StringBuilder("{");
        for (int i = 0; i < keys.length; i++)
        {
            stringBuilder.append("\"")
                         .append(keys[i])
                         .append("\": ")
                         .append(map.get(keys[i]).toString());
            if(i < keys.length - 1)
                stringBuilder.append(", ");
        }
        return stringBuilder.append("}").toString();
    }

    @Override
    public boolean isLsonObject()
    {
        return true;
    }

    @Override
    public LsonObject getAsLsonObject()
    {
        return (LsonObject) this;
    }

    @Override
    public LsonElement deepCopy()
    {
        LsonObject lsonObject = new LsonObject();
        for (Map.Entry<String, LsonElement> entry : map.entrySet())
            lsonObject.put(entry.getKey(), entry.getValue().deepCopy());
        return lsonObject;
    }
}

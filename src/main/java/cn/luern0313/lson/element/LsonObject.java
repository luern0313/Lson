package cn.luern0313.lson.element;

import java.util.LinkedHashMap;
import java.util.Map;

import cn.luern0313.lson.LsonUtil;


/**
 * JSON object类。
 *
 * @author luern0313
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

    public LsonElement hasPut(String key, Class<? extends LsonElement> clz)
    {
        try
        {
            if(!map.containsKey(key))
                return put(key, clz.newInstance());
            else
            {
                LsonElement lsonElement = get(key);
                if(lsonElement.getClass().getName().equals(clz.getName()) && !lsonElement.isLsonPrimitive())
                    return lsonElement;
            }
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public LsonElement get(String key)
    {
        LsonElement lsonElement = map.get(key);
        if(lsonElement != null)
            return lsonElement;
        return LsonNull.getJsonNull();
    }

    public Object getFromPath(String path)
    {
        return LsonUtil.getValue(this, path);
    }

    public <T> T getFromPath(String path, Class<T> clz)
    {
        return LsonUtil.getValue(this, path, clz);
    }

    public void putFromPath(String path, Object value)
    {
        LsonUtil.putValue(this, path, value);
    }

    public String[] getKeys()
    {
        return map.keySet().toArray(new String[0]);
    }

    public LsonElement put(String key, LsonElement value)
    {
        map.put(key, value == null ? LsonNull.getJsonNull() : value);
        return value;
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

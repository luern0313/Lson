package cn.luern0313.lson.element;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * JSON object类。
 *
 * @author luern0313
 */

public class LsonObject extends LsonElement
{
    private final LinkedHashMap<String, LsonElement> map;

    public LsonObject()
    {
        this(new LinkedHashMap<>());
    }

    public LsonObject(LinkedHashMap<String, LsonElement> map)
    {
        this.map = map;
    }

    /**
     * 检查key在map中是否存在。
     *
     * @param key 要检查是否存在的key。
     * @return 在map中是否存在。
     */
    public boolean has(String key)
    {
        return map.containsKey(key);
    }

    public boolean isNull(String key)
    {
        return get(key).isLsonNull();
    }

    /**
     * 检查key在map中是否存在，以及是否是指定的类型。
     *
     * <p>若不存在key，则按指定的类型新建并返回。
     *
     * <p>若存在且为指定的类型，同时不为{@link LsonPrimitive}，则直接返回。
     *
     * <p>若存在但不为指定的类型，或类型为{@link LsonPrimitive}，则返回null。
     *
     * @param key 要检查是否存在的key，
     * @param clz key指定的类型
     * @return key对应的对象或null。
     */
    public LsonElement hasPut(String key, Class<? extends LsonElement> clz)
    {
        try
        {
            if(!map.containsKey(key))
                return put(key, clz.newInstance());
            else
            {
                LsonElement lsonElement = get(key);
                if(lsonElement.getClass().equals(clz) && !lsonElement.isLsonPrimitive())
                    return lsonElement;
            }
        }
        catch (InstantiationException | IllegalAccessException ignored)
        {
        }
        return null;
    }

    /**
     * 返回key对应的value。
     *
     * @param key 要获取value的key。
     * @return key对应的value。
     */
    public LsonElement get(String key)
    {
        LsonElement lsonElement = map.get(key);
        if(lsonElement != null)
            return lsonElement;
        return LsonNull.getJsonNull();
    }

    /**
     * 以数组的形式获取所有的key。
     *
     * @return 所有的key。
     */
    public String[] getKeys()
    {
        return map.keySet().toArray(new String[0]);
    }

    /**
     * 填充key与value，若key存在则替换。
     *
     * @param key 要填充的key。
     * @param value 要填充的value。
     * @return 填充完成的LsonElement。
     */
    public LsonElement put(String key, LsonElement value)
    {
        map.put(key, value == null ? LsonNull.getJsonNull() : value);
        return value;
    }

    public boolean getBoolean(String key)
    {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean def)
    {
        LsonElement lsonElement = map.get(key);
        if(lsonElement == null || !(lsonElement.isLsonPrimitive() && lsonElement.getAsLsonPrimitive().isBoolean()))
            return def;
        else
            return lsonElement.getAsLsonPrimitive().getAsBoolean();
    }

    public String getString(String key)
    {
        return getString(key, "");
    }

    public String getString(String key, String def)
    {
        LsonElement lsonElement = map.get(key);
        if(lsonElement == null || !(lsonElement.isLsonPrimitive() && lsonElement.getAsLsonPrimitive().isString()))
            return def;
        else
            return lsonElement.getAsLsonPrimitive().getAsString();
    }

    public int getInt(String key)
    {
        return getInt(key, 0);
    }

    public int getInt(String key, int def)
    {
        LsonElement lsonElement = map.get(key);
        if(lsonElement == null || !(lsonElement instanceof LsonPrimitive && lsonElement.getAsLsonPrimitive().isNumber()))
            return def;
        else
            return lsonElement.getAsLsonPrimitive().getAsInt();
    }

    public LsonArray getJsonArray(String key)
    {
        LsonElement lsonElement = map.get(key);
        if(!lsonElement.isLsonArray())
            return new LsonArray();
        else
            return lsonElement.getAsLsonArray();
    }

    public LsonObject getJsonObject(String key)
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
        return this;
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

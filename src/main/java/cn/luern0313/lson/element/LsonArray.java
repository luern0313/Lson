package cn.luern0313.lson.element;


import java.util.ArrayList;

import cn.luern0313.lson.LsonUtil;

/**
 * JSON数组类。
 *
 * @author luern0313
 */

public class LsonArray extends LsonElement
{
    private final ArrayList<LsonElement> list;

    public LsonArray()
    {
        this(new ArrayList<>());
    }

    public LsonArray(int size)
    {
        this(new ArrayList<>(size));
    }

    public LsonArray(ArrayList<LsonElement> list)
    {
        this.list = list;
    }

    public int size()
    {
        return list.size();
    }

    public LsonElement get(int index)
    {
        if(index >= 0 && index < list.size())
        {
            LsonElement lsonElement = list.get(index);
            if(lsonElement != null)
                return lsonElement;
        }
        return new LsonNull();
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

    public boolean getAsBoolean(int key)
    {
        return getAsBoolean(key, false);
    }

    public boolean getAsBoolean(int key, boolean def)
    {
        LsonElement lsonElement = list.get(key);
        if(lsonElement == null || !(lsonElement.isLsonPrimitive() && lsonElement.getAsLsonPrimitive().isBoolean()))
            return def;
        else
            return lsonElement.getAsLsonPrimitive().getAsBoolean();
    }

    public String getAsString(int key)
    {
        return getAsString(key, "");
    }

    public String getAsString(int key, String def)
    {
        LsonElement lsonElement = list.get(key);
        if(lsonElement == null || !(lsonElement.isLsonPrimitive() && lsonElement.getAsLsonPrimitive().isString()))
            return def;
        else
            return lsonElement.getAsLsonPrimitive().getAsString();
    }

    public int getAsInt(int key)
    {
        return getAsInt(key, 0);
    }

    public int getAsInt(int key, int def)
    {
        LsonElement lsonElement = list.get(key);
        if(lsonElement == null || !(lsonElement.isLsonPrimitive() && lsonElement.getAsLsonPrimitive().isNumber()))
            return def;
        else
            return lsonElement.getAsLsonPrimitive().getAsInt();
    }

    public LsonArray getAsJsonArray(int key)
    {
        LsonElement lsonElement = list.get(key);
        if(!lsonElement.isLsonArray())
            return new LsonArray();
        else
            return lsonElement.getAsLsonArray();
    }

    public LsonObject getAsJsonObject(int key)
    {
        LsonElement lsonElement = list.get(key);
        if(!lsonElement.isLsonObject())
            return new LsonObject();
        else
            return lsonElement.getAsLsonObject();
    }

    public LsonElement add(LsonElement object)
    {
        if(object != null)
            list.add(object);
        else
            list.add(LsonNull.getJsonNull());
        return object;
    }

    public LsonElement set(int index, LsonElement object)
    {
        if(object == null)
            object = LsonNull.getJsonNull();

        while (list.size() - 1 < index)
            list.add(LsonNull.getJsonNull());
        list.set(index, object);
        return object;
    }

    public LsonElement hasSet(int index, Class<? extends LsonElement> clz)
    {
        try
        {
            LsonElement lsonElement = get(index);
            if(lsonElement.isLsonNull())
                return set(index, clz.newInstance());
            else if(lsonElement.getClass().getName().equals(clz.getName()) && !lsonElement.isLsonPrimitive())
                return lsonElement;
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isLsonArray()
    {
        return true;
    }

    @Override
    public LsonArray getAsLsonArray()
    {
        return (LsonArray) this;
    }

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++)
        {
            stringBuilder.append(list.get(i).toString());
            if(i < list.size() - 1)
                stringBuilder.append(", ");
        }
        return stringBuilder.append("]").toString();
    }

    @Override
    public LsonElement deepCopy()
    {
        LsonArray lsonArray = new LsonArray(size());
        for (int i = 0; i < size(); i++)
            lsonArray.add(get(i).deepCopy());
        return lsonArray;
    }
}

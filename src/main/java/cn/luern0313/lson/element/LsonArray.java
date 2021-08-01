package cn.luern0313.lson.element;


import java.util.ArrayList;

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

    public boolean isNull(int index)
    {
        return get(index) == null;
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

    public boolean getBoolean(int key)
    {
        return getBoolean(key, false);
    }

    public boolean getBoolean(int key, boolean def)
    {
        LsonElement lsonElement = list.get(key);
        if(lsonElement == null || !(lsonElement.isLsonPrimitive() && lsonElement.getAsLsonPrimitive().isBoolean()))
            return def;
        else
            return lsonElement.getAsLsonPrimitive().getAsBoolean();
    }

    public String getString(int key)
    {
        return getString(key, "");
    }

    public String getString(int key, String def)
    {
        LsonElement lsonElement = list.get(key);
        if(lsonElement == null || !(lsonElement.isLsonPrimitive() && lsonElement.getAsLsonPrimitive().isString()))
            return def;
        else
            return lsonElement.getAsLsonPrimitive().getAsString();
    }

    public int getInt(int key)
    {
        return getInt(key, 0);
    }

    public int getInt(int key, int def)
    {
        LsonElement lsonElement = list.get(key);
        if(lsonElement == null || !(lsonElement.isLsonPrimitive() && lsonElement.getAsLsonPrimitive().isNumber()))
            return def;
        else
            return lsonElement.getAsLsonPrimitive().getAsInt();
    }

    public LsonArray getJsonArray(int key)
    {
        LsonElement lsonElement = list.get(key);
        if(!lsonElement.isLsonArray())
            return new LsonArray();
        else
            return lsonElement.getAsLsonArray();
    }

    public LsonObject getJsonObject(int index)
    {
        LsonElement lsonElement = list.get(index);
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

    public void addAll(LsonArray lsonArray)
    {
        if(lsonArray != null)
            for (int i = 0; i < lsonArray.size(); i++)
                add(lsonArray.get(i));
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
        return this;
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

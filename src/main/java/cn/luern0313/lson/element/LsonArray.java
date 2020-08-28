package cn.luern0313.lson.element;


import java.util.ArrayList;

/**
 * 被 luern0313 创建于 2020/7/29.
 */

public class LsonArray extends LsonElement
{
    private ArrayList<LsonElement> list;

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

    public void add(LsonElement object)
    {
        if(object != null)
            list.add(object);
        else
            list.add(LsonNull.getJsonNull());
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

package cn.luern0313.lson.element;

/**
 * JSON元素基类。
 *
 * @author luern0313
 */

public abstract class LsonElement
{
    public boolean isLsonObject()
    {
        return false;
    }

    public LsonObject getAsLsonObject()
    {
        return null;
    }

    public boolean isLsonArray()
    {
        return false;
    }

    public LsonArray getAsLsonArray()
    {
        return null;
    }

    public boolean isLsonPrimitive()
    {
        return false;
    }

    public LsonPrimitive getAsLsonPrimitive()
    {
        return null;
    }

    public abstract LsonElement deepCopy();

    @Override
    public abstract String toString();
}

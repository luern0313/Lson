package cn.luern0313.lson.element;

/**
 * 被 luern0313 创建于 2020/8/11.
 */

public class LsonPrimitive extends LsonElement
{
    private final Object value;

    public LsonPrimitive(Object value)
    {
        this.value = value;
    }

    public boolean isBoolean()
    {
        return value instanceof Boolean;
    }

    public boolean getAsBoolean()
    {
        if(isBoolean())
            return (Boolean) value;
        return Boolean.parseBoolean(getAsString());
    }

    public boolean isString()
    {
        return value instanceof String;
    }

    public String getAsString()
    {
        if(isBoolean())
            return String.valueOf(getAsBoolean());
        else if(isNumber())
            return String.valueOf(getAsNumber().toString());
        return (String) value;
    }

    public boolean isNumber()
    {
        return value instanceof Number;
    }

    public Number getAsNumber()
    {
        if(isString())
            return Double.parseDouble((String) value);
        return (Number) value;
    }

    public int getAsInt()
    {
        if(isNumber())
            return getAsNumber().intValue();
        return Integer.parseInt(getAsString());
    }

    public short getAsShort()
    {
        if(isNumber())
            return getAsNumber().shortValue();
        return Short.parseShort(getAsString());
    }

    public float getAsFloat()
    {
        if(isNumber())
            return getAsNumber().floatValue();
        return Float.parseFloat(getAsString());
    }

    public double getAsDouble()
    {
        if(isNumber())
            return getAsNumber().doubleValue();
        return Double.parseDouble(getAsString());
    }

    public long getAsLong()
    {
        if(isNumber())
            return getAsNumber().longValue();
        return Long.parseLong(getAsString());
    }

    @Override
    public String toString()
    {
        if(isString())
            return "\"" + getAsString() + "\"";
        return getAsString();
    }

    @Override
    public boolean isLsonPrimitive()
    {
        return true;
    }

    @Override
    public LsonPrimitive getAsLsonPrimitive()
    {
        return (LsonPrimitive) this;
    }

    @Override
    public LsonElement deepCopy()
    {
        return this;
    }
}

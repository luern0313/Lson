package cn.luern0313.lson.util;

/**
 * 被 luern0313 创建于 2020/9/11.
 */

public class DeserializationValueUtil
{
    private Object value;
    private final Class<?> type;

    public DeserializationValueUtil(String string)
    {
        this(string, null);
    }

    public DeserializationValueUtil(Object value, Class<?> type)
    {
        if(value instanceof String)
            this.value = new StringBuilder((String) value);
        else if(value instanceof Number)
            this.value = ((Number) value).doubleValue();
        else
            this.value = value;
        this.type = type;
    }

    public Object get()
    {
        return value;
    }

    public Object get(TypeUtil typeUtil)
    {
        return typeUtil.getAsClass().cast(value);
    }

    public Class<?> getType()
    {
        return type;
    }

    public Class<?> getCurrentType()
    {
        return value.getClass();
    }

    public DeserializationValueUtil set(Object value)
    {
        if(this.value instanceof StringBuilder && value instanceof String)
        {
            ((StringBuilder) this.value).setLength(0);
            ((StringBuilder) this.value).append(value);
        }
        else
            this.value = value;
        return this;
    }

    @Override
    public String toString()
    {
        String string = value.toString();
        if(DataProcessUtil.isDouble(string))
        {
            double number = Double.parseDouble(string);
            switch (type.getName())
            {
                case "int":
                case "java.lang.Integer":
                    return String.valueOf((int) number);
                case "short":
                case "java.lang.Short":
                    return String.valueOf((short) number);
                case "long":
                case "java.lang.Long":
                    return String.valueOf((long) number);
                case "float":
                case "java.lang.Float":
                    return String.valueOf((float) number);
            }
        }
        return string;
    }
}

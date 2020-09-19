package cn.luern0313.lson.util;

/**
 * 被 luern0313 创建于 2020/9/11.
 */

public class DeserializationStringUtil
{
    public StringBuilder stringBuilder;
    Class<?> type;

    public DeserializationStringUtil(String string)
    {
        this(string, String.class);
    }

    public DeserializationStringUtil(String string, Class<?> type)
    {
        this.stringBuilder = new StringBuilder(string);
        this.type = type;
    }

    public DeserializationStringUtil set(String text)
    {
        stringBuilder.setLength(0);
        stringBuilder.append(text);
        return this;
    }

    @Override
    public String toString()
    {
        String string = stringBuilder.toString();
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

package cn.luern0313.lson.util;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.luern0313.lson.Deserialization;
import cn.luern0313.lson.annotation.field.LsonNumberFormat;


/**
 * 数据处理相关。
 *
 * @author luern0313
 */

public class DataProcessUtil
{
    public static String getSize(long size)
    {
        String[] unit = new String[]{"B", "KB", "MB", "GB"};
        long s = size * 10;
        int u = 0;
        while (s > 10240 && u < unit.length - 1)
        {
            s /= 1024;
            u++;
        }
        return s / 10.0 + unit[u];
    }

    public static String getSurplusTime(long surplusByte, int speed)
    {
        if(speed <= 0) return "未知";
        long time = surplusByte / speed;

        String sec = String.valueOf(time % 60);
        if(sec.length() == 1) sec = "0" + sec;
        String min = String.valueOf(time / 60 % 60);
        if(min.length() == 1) min = "0" + min;
        String hour = String.valueOf(time / 3600 % 60);
        if(hour.length() == 1) hour = "0" + hour;

        if(hour.equals("00")) return min + ":" + sec;
        else return hour + ":" + min + ":" + sec;
    }

    public static <T> int getIndex(T object, T[] array)
    {
        for (int i = 0; i < array.length; i++)
        {
            if(object == array[i] || object.equals(array[i]))
                return i;
        }
        return -1;
    }

    /**
     * 获取格式化的时间。
     *
     * @param timeStamp 时间戳。
     * @param pattern 格式化时间格式。
     * @return 格式化后的时间。
     *
     * @author luern0313
     */
    public static String getTime(long timeStamp, String pattern)
    {
        try
        {
            Date date = new Date(timeStamp);
            SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
            return format.format(date);
        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
            return "";
        }
    }

    public static Object getNumberFormat(Object value, int digit, LsonNumberFormat.NumberFormatMode mode, TypeUtil fieldType)
    {
        try
        {
            BigDecimal bigDecimal = new BigDecimal(String.valueOf(value.toString()));
            bigDecimal = bigDecimal.setScale(digit, LsonNumberFormat.NumberFormatMode.modeIntegerMap.get(mode));

            if(Deserialization.NUMBER_TYPES.contains(fieldType.getName()))
                return bigDecimal.doubleValue();
            return bigDecimal.toString();
        }
        catch (NumberFormatException e)
        {
            return value;
        }
    }

    public static void replaceAll(StringBuilder builder, String from, String to)
    {
        int index = builder.indexOf(from);
        while (index != -1)
        {
            builder.replace(index,index + from.length(), to);
            index += to.length();
            index = builder.indexOf(from, index);
        }
    }

    public static boolean isDouble(String string)
    {
        try
        {
            Double.parseDouble(string);
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }

    public static String getUnderScoreCase(String name)
    {
        try
        {
            StringReader stringReader = new StringReader(name);
            StringBuilder stringBuilder = new StringBuilder();
            int s;
            while (true)
            {
                s = stringReader.read();
                if(s == -1)
                    break;

                if(s >= 'A' && s <= 'Z' && stringBuilder.length() != 0)
                    stringBuilder.append("_").append((char) (s + 32));
                else if(s >= 'A' && s <= 'Z')
                    stringBuilder.append((char) (s + 32));
                else
                    stringBuilder.append((char) s);
            }
            return stringBuilder.toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}

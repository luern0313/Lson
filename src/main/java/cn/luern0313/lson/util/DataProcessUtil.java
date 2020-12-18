package cn.luern0313.lson.util;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cn.luern0313.lson.annotation.field.LsonDateFormat;
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
            if(object == array[i] || object.equals(array[i]))
                return i;
        return -1;
    }

    public static int getIndex(double object, double[] array)
    {
        for (int i = 0; i < array.length; i++)
            if(object == array[i])
                return i;
        return -1;
    }

    public static int getIndex(Object object, ArrayList<?> list)
    {
        for (int i = 0; i < list.size(); i++)
            if(object == list.get(i) || object.equals(list.get(i)))
                return i;
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

    /**
     * 通过格式化后的时间获得时间戳。
     *
     * @param time 格式化后的时间。
     * @param pattern 格式化时间格式。
     * @param lsonDateFormatMode 时间戳类型。
     * @return 时间戳。
     *
     * @author luern0313
     */
    public static long getTimeStamp(String time, String pattern, LsonDateFormat.LsonDateFormatMode lsonDateFormatMode)
    {
        try
        {
            SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
            return format.parse(time).getTime() / (lsonDateFormatMode == LsonDateFormat.LsonDateFormatMode.SECOND ? 1000 : 0);
        }
        catch (RuntimeException | ParseException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    public static Object getNumberFormat(Object value, int digit, LsonNumberFormat.NumberFormatMode mode)
    {
        try
        {
            BigDecimal bigDecimal = new BigDecimal(String.valueOf(value.toString()));
            bigDecimal = bigDecimal.setScale(digit, LsonNumberFormat.NumberFormatMode.modeIntegerMap.get(mode));

            return bigDecimal.doubleValue();
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

    /**
     * 将小驼峰命名法转换为下划线命名法。
     *
     * <p>例如：<br><code>videoAuthorUid→video_author_uid</code>
     *
     * @param name 小驼峰命名法的变量名
     * @return 下划线命名法变量名
     *
     * @author luern0313
     */
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

    /**
     * 将下划线命名法转换为小驼峰命名法。
     *
     * <p>例如：<br><code>video_author_uid→videoAuthorUid</code>
     *
     * @param name 小驼峰命名法的变量名
     * @return 下划线命名法变量名
     *
     * @author luern0313
     */
    public static String getCamelCase(String name)
    {
        try
        {
            StringReader stringReader = new StringReader(name);
            StringBuilder stringBuilder = new StringBuilder();
            int s;
            boolean isNeedConvertUppercase = false;
            while (true)
            {
                s = stringReader.read();
                if(s == -1)
                    break;

                if(s == '_')
                    isNeedConvertUppercase = true;
                else if(isNeedConvertUppercase)
                {
                    stringBuilder.append((char) (s - 32));
                    isNeedConvertUppercase = false;
                }
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

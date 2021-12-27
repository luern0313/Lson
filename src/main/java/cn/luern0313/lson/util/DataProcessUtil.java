package cn.luern0313.lson.util;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
        if(size < 0)
            size = 0;

        String[] unit = new String[]{"B", "KB", "MB", "GB"};
        long s = size * 10;
        int u = 0;
        while (s >= 10240 && u < unit.length - 1)
        {
            s /= 1024;
            u++;
        }
        return s / 10.0 + unit[u];
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

    public static int getIndex(Object object, List<?> list)
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
            return format.parse(time).getTime() / (lsonDateFormatMode == LsonDateFormat.LsonDateFormatMode.SECOND ? 1000 : 1);
        }
        catch (RuntimeException | ParseException ignored)
        {
        }
        return 0;
    }

    public static Object getNumberFormat(Object value, int digit, LsonNumberFormat.NumberFormatMode mode, boolean isCastInteger)
    {
        try
        {
            BigDecimal bigDecimal = new BigDecimal(value.toString());
            bigDecimal = bigDecimal.setScale(digit, LsonNumberFormat.NumberFormatMode.modeIntegerMap.get(mode));
            double result = bigDecimal.doubleValue();
            if(isCastInteger)
                return (int) result;
            else
                return result;
        }
        catch (NumberFormatException e)
        {
            return value;
        }
    }

    public static void replaceAll(StringBuilder builder, String from, String to)
    {
        if(builder == null || from == null || to == null || from.length() == 0)
            return;

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
        catch (RuntimeException e)
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
        catch (RuntimeException | IOException ignored)
        {
        }
        return null;
    }

    /**
     * 将下划线命名法转换为小驼峰命名法。
     *
     * <p>例如：<br><code>video_author_uid→videoAuthorUid</code>
     *
     * @param name 划线命命名法的变量名
     * @return 小驼峰命名法变量名
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
        catch (RuntimeException | IOException ignored)
        {
        }
        return null;
    }

    public static String join(List<?> arrayList, String separator)
    {
        if(arrayList == null)
            return null;

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < arrayList.size(); i++)
            stringBuilder.append(i == 0 || separator == null ? "" : separator).append(arrayList.get(i));
        return stringBuilder.toString();
    }

    public static String join(Object[] array, String separator)
    {
        if(array == null)
            return null;

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < array.length; i++)
            stringBuilder.append(i == 0 || separator == null ? "" : separator).append(array[i]);
        return stringBuilder.toString();
    }
}

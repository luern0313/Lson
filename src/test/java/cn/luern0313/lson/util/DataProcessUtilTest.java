package cn.luern0313.lson.util;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cn.luern0313.lson.annotation.field.LsonDateFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * 被 luern 创建于 2021/12/6.
 */
public class DataProcessUtilTest
{
    @Test
    public void getSize()
    {
        assertEquals(DataProcessUtil.getSize(Long.MIN_VALUE), "0.0B");
        assertEquals(DataProcessUtil.getSize(-1), "0.0B");
        assertEquals(DataProcessUtil.getSize(0), "0.0B");
        assertEquals(DataProcessUtil.getSize(1), "1.0B");
        assertEquals(DataProcessUtil.getSize(1000), "1000.0B");
        assertEquals(DataProcessUtil.getSize(1024), "1.0KB");
        assertEquals(DataProcessUtil.getSize(10138), "9.9KB");
        assertEquals(DataProcessUtil.getSize(10240), "10.0KB");
        assertEquals(DataProcessUtil.getSize((long) Math.pow(1024, 2)), "1.0MB");
        assertEquals(DataProcessUtil.getSize((long) Math.pow(1024, 3)), "1.0GB");
    }

    @Test
    public void getIndex()
    {
        assertEquals(DataProcessUtil.getIndex(null, new String[]{}), -1);
        assertEquals(DataProcessUtil.getIndex("哈哈哈", new String[]{"1", "a", "哈哈哈哈", "啊啊"}), -1);
        assertEquals(DataProcessUtil.getIndex("哈哈哈哈", new String[]{"1", "a", "哈哈哈哈", "啊啊"}), 2);
    }

    @Test
    public void testGetIndex()
    {
        assertEquals(DataProcessUtil.getIndex(0d, new double[]{}), -1);
        assertEquals(DataProcessUtil.getIndex(0d, new double[]{1, 2.3333, 114.514}), -1);
        assertEquals(DataProcessUtil.getIndex(114.514, new double[]{1, 2.3333, 114.514}), 2);
    }

    @Test
    public void testGetIndex1()
    {
        ArrayList<String> arrayList = new ArrayList<>();
        assertEquals(DataProcessUtil.getIndex(null, arrayList), -1);
        arrayList.add("1");
        arrayList.add("a");
        arrayList.add("哈哈哈哈");
        arrayList.add("啊啊");
        assertEquals(DataProcessUtil.getIndex("哈哈", arrayList), -1);
        assertEquals(DataProcessUtil.getIndex("哈哈哈哈", arrayList), 2);
    }

    @Test
    public void getTime()
    {
        assertEquals(DataProcessUtil.getTime(-1, ""), "");
        assertEquals(DataProcessUtil.getTime(0, ""), "");
        ;
        assertEquals(DataProcessUtil.getTime(0, "yyyy-MM-dd HH:mm:ss"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(0)));
        assertEquals(DataProcessUtil.getTime(1638802803555L, "yyyy年MM月dd日 HH:mm:ss"), new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault()).format(new Date(1638802803555L)));
    }

    @Test
    public void getTimeStamp() throws ParseException
    {
        assertEquals(DataProcessUtil.getTimeStamp(null, null, null), 0);
        assertEquals(DataProcessUtil.getTimeStamp("", "", LsonDateFormat.LsonDateFormatMode.MILLI_SECOND), 0);
        assertEquals(DataProcessUtil.getTimeStamp("1970-01-01 08:00:00", "yyyy-MM-dd HH:mm:ss", LsonDateFormat.LsonDateFormatMode.MILLI_SECOND), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse("1970-01-01 08:00:00").getTime());
        assertEquals(DataProcessUtil.getTimeStamp("2021年12月06日 23:00:03", "yyyy年MM月dd日 HH:mm:ss", LsonDateFormat.LsonDateFormatMode.SECOND), new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault()).parse("2021年12月06日 23:00:03").getTime() / 1000);
        assertEquals(DataProcessUtil.getTimeStamp("2021年12月06日 23:00:03", "yyyy年MM月dd日 HH:mm:ss", LsonDateFormat.LsonDateFormatMode.MILLI_SECOND), new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault()).parse("2021年12月06日 23:00:03").getTime());
    }

    @Test
    public void getNumberFormat()
    {
        //assertEquals(DataProcessUtil.getNumberFormat(0, 0, LsonNumberFormat.NumberFormatMode.DOWN), 0);
    }

    @Test
    public void replaceAll()
    {
        DataProcessUtil.replaceAll(null, null, null);
        StringBuilder stringBuilder = new StringBuilder();
        DataProcessUtil.replaceAll(stringBuilder, "", "");
        assertEquals(stringBuilder.toString(), "");
        stringBuilder.append("aaabbbccc");
        DataProcessUtil.replaceAll(stringBuilder, "bbb", "ddd");
        assertEquals(stringBuilder.toString(), "aaadddccc");
        DataProcessUtil.replaceAll(stringBuilder, "a", "ee");
        assertEquals(stringBuilder.toString(), "eeeeeedddccc");
    }

    @Test
    public void isDouble()
    {
        assertFalse(DataProcessUtil.isDouble(null));
        assertFalse(DataProcessUtil.isDouble(""));
        assertTrue(DataProcessUtil.isDouble("1"));
        assertTrue(DataProcessUtil.isDouble("1.0"));
        assertFalse(DataProcessUtil.isDouble("1.0啊啊啊啊"));
    }

    @Test
    public void getUnderScoreCase()
    {
        assertNull(DataProcessUtil.getUnderScoreCase(null));
        assertEquals(DataProcessUtil.getUnderScoreCase(""), "");
        assertEquals(DataProcessUtil.getUnderScoreCase("add"), "add");
        assertEquals(DataProcessUtil.getUnderScoreCase("addAll"), "add_all");
        assertEquals(DataProcessUtil.getUnderScoreCase("add_all"), "add_all");
    }

    @Test
    public void getCamelCase()
    {
        assertNull(DataProcessUtil.getCamelCase(null));
        assertEquals(DataProcessUtil.getCamelCase(""), "");
        assertEquals(DataProcessUtil.getCamelCase("add"), "add");
        assertEquals(DataProcessUtil.getCamelCase("add_all"), "addAll");
        assertEquals(DataProcessUtil.getCamelCase("addAll"), "addAll");
    }

    @Test
    public void join()
    {
        ArrayList<String> stringArrayList = null;
        assertNull(DataProcessUtil.join(stringArrayList, null));
        stringArrayList = new ArrayList<>();
        assertEquals(DataProcessUtil.join(stringArrayList, null), "");
        stringArrayList.add("111");
        assertEquals(DataProcessUtil.join(stringArrayList, null), "111");
        assertEquals(DataProcessUtil.join(stringArrayList, " "), "111");
        stringArrayList.add("222");
        stringArrayList.add("333");
        assertEquals(DataProcessUtil.join(stringArrayList, ","), "111,222,333");
    }

    @Test
    public void testJoin()
    {
        Object[] array = null;
        assertNull(DataProcessUtil.join(array, null));
        assertEquals(DataProcessUtil.join(new String[]{}, null), "");
        assertEquals(DataProcessUtil.join(new String[]{"111"}, null), "111");
        assertEquals(DataProcessUtil.join(new String[]{"111"}, " "), "111");
        assertEquals(DataProcessUtil.join(new String[]{"111", "222", "333"}, ","), "111,222,333");
    }
}
package cn.luern0313.lson.json;

import org.junit.Test;

import cn.luern0313.lson.LsonUtil;
import cn.luern0313.lson.element.LsonArray;
import cn.luern0313.lson.element.LsonObject;

import static org.junit.Assert.assertEquals;

/**
 * 被 luern 创建于 2022/1/29.
 */
public class JSON5Test
{
    @Test
    public void arrayExtraCommaTest()
    {
        String arrayExtraComma = "[1,2,3,4,]";
        LsonArray arrayExtraCommaArray = LsonUtil.parseAsArray(arrayExtraComma);
        assertEquals(arrayExtraCommaArray.getInt(0), 1);
        assertEquals(arrayExtraCommaArray.getInt(1), 2);
        assertEquals(arrayExtraCommaArray.getInt(2), 3);
        assertEquals(arrayExtraCommaArray.getInt(3), 4);
        assertEquals(arrayExtraCommaArray.size(), 4);
    }

    @Test
    public void objectExtraCommaTest()
    {
        String objectExtraComma = "{\"a\": \"a\", \"b\": \"b\", \"c\": \"c\",}";
        LsonObject arrayExtraCommaObject = LsonUtil.parseAsObject(objectExtraComma);
        assertEquals(arrayExtraCommaObject.getString("a"), "a");
        assertEquals(arrayExtraCommaObject.getString("b"), "b");
        assertEquals(arrayExtraCommaObject.getString("c"), "c");
        assertEquals(arrayExtraCommaObject.getKeys().length, 3);
    }
}
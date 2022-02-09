package cn.luern0313.lson.json.json;

import org.junit.Test;

import cn.luern0313.lson.LsonUtil;
import cn.luern0313.lson.element.LsonElement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * 被 luern 创建于 2022/1/30.
 */

public class JSONPrimitiveTest
{
    @Test
    public void nullTest()
    {
        LsonElement lsonElement = LsonUtil.parse("null");
        assertNull(lsonElement);
    }

    @Test
    public void trueTest()
    {
        LsonElement lsonElement = LsonUtil.parse("true");
        assertTrue(lsonElement.getAsLsonPrimitive().getAsBoolean());
    }

    @Test
    public void falseTest()
    {
        LsonElement lsonElement = LsonUtil.parse("false");
        assertFalse(lsonElement.getAsLsonPrimitive().getAsBoolean());
    }

    @Test
    public void numberTest()
    {
        LsonElement intLsonElement = LsonUtil.parse("1");
        assertEquals(intLsonElement.getAsLsonPrimitive().getAsInt(), 1);

        LsonElement intLsonElement2 = LsonUtil.parse(String.valueOf(Integer.MAX_VALUE + 1L));
        assertEquals(intLsonElement2.getAsLsonPrimitive().getAsLong(), 2147483648L);
    }

    @Test
    public void stringTest()
    {
        LsonElement intLsonElement = LsonUtil.parse("\"aaa\"");
        assertEquals(intLsonElement.getAsLsonPrimitive().getAsString(), "aaa");
    }
}

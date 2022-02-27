package cn.luern0313.lson.json.json5;

import org.junit.Test;

import cn.luern0313.lson.LsonUtil;

import static org.junit.Assert.assertEquals;

/**
 * 被 luern 创建于 2022/2/26.
 */

public class JSON5NumberTest
{
    @Test
    public void NaNTest()
    {
        assertEquals(LsonUtil.parse("NaN").getAsLsonPrimitive().getAsNumber(), Double.NaN);
    }

    @Test
    public void infinityTest()
    {
        assertEquals(LsonUtil.parse("Infinity").getAsLsonPrimitive().getAsNumber(), Double.POSITIVE_INFINITY);
        assertEquals(LsonUtil.parse("+Infinity").getAsLsonPrimitive().getAsNumber(), Double.POSITIVE_INFINITY);
        assertEquals(LsonUtil.parse("-Infinity").getAsLsonPrimitive().getAsNumber(), Double.NEGATIVE_INFINITY);
    }
}

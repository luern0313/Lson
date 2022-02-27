package cn.luern0313.lson.json.json5;

import org.junit.Test;

import cn.luern0313.lson.LsonUtil;

import static org.junit.Assert.assertEquals;

/**
 * 被 luern 创建于 2022/1/30.
 */

public class JSON5MultilineStringTest
{
    @Test
    public void multilineStringTest() {
        assertEquals(LsonUtil.parse("\"aaa\\\nbbb\"").getAsLsonPrimitive().getAsString(), "aaabbb");
        assertEquals(LsonUtil.parse("\"aaa\\\\bbb\"").getAsLsonPrimitive().getAsString(), "aaa\\bbb");
    }
}

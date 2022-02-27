package cn.luern0313.lson.json.json5;

import org.junit.Test;

import cn.luern0313.lson.LsonUtil;
import cn.luern0313.lson.element.LsonElement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * 被 luern 创建于 2022/1/30.
 */

public class JSON5PrimitiveTest
{
    @Test
    public void singleQuotationStringTest() {
        LsonElement singleQuotationLsonElement = LsonUtil.parse("'aaa'");
        assertEquals(singleQuotationLsonElement.getAsLsonPrimitive().getAsString(), "aaa");
    }

    @Test
    public void noneQuotationStringTest() {
        assertNull(LsonUtil.parse("aaa"));
    }
}

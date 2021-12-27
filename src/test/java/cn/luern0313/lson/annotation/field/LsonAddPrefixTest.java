package cn.luern0313.lson.annotation.field;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

/**
 * 被 luern 创建于 2021/12/8.
 */

public class LsonAddPrefixTest
{
    LsonAddPrefix lsonAddPrefix;
    LsonAddPrefix.LsonAddPrefixConfig config;

    @Before
    public void before()
    {
        lsonAddPrefix = Mockito.mock(LsonAddPrefix.class);
        config = new LsonAddPrefix.LsonAddPrefixConfig();
    }

    @Test
    public void deserialization()
    {
        StringBuilder stringBuilder = new StringBuilder();

        Mockito.when(lsonAddPrefix.value()).thenReturn("");
        assertEquals(config.deserialization(stringBuilder, lsonAddPrefix, null).toString(), "");

        stringBuilder.append("aaa");
        assertEquals(config.deserialization(stringBuilder, lsonAddPrefix, null).toString(), "aaa");

        stringBuilder.delete(0, stringBuilder.length());
        Mockito.when(lsonAddPrefix.value()).thenReturn("bbb");
        assertEquals(config.deserialization(stringBuilder, lsonAddPrefix, null).toString(), "bbb");

        stringBuilder.append("aaa");
        assertEquals(config.deserialization(stringBuilder, lsonAddPrefix, null).toString(), "bbbbbbaaa");
    }

    @Test
    public void serialization()
    {
        StringBuilder stringBuilder = new StringBuilder();

        Mockito.when(lsonAddPrefix.value()).thenReturn("");
        assertEquals(config.serialization(stringBuilder, lsonAddPrefix, null).toString(), "");

        stringBuilder.append("aaa");
        assertEquals(config.serialization(stringBuilder, lsonAddPrefix, null).toString(), "aaa");

        stringBuilder.delete(0, stringBuilder.length());
        Mockito.when(lsonAddPrefix.value()).thenReturn("bbb");
        assertEquals(config.serialization(stringBuilder, lsonAddPrefix, null).toString(), "");

        stringBuilder.append("aaa");
        assertEquals(config.serialization(stringBuilder, lsonAddPrefix, null).toString(), "aaa");

        stringBuilder.append("bbb");
        Mockito.when(lsonAddPrefix.value()).thenReturn("aaa");
        assertEquals(config.serialization(stringBuilder, lsonAddPrefix, null).toString(), "bbb");
    }
}

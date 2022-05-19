package cn.luern0313.lson.annotation.field;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

/**
 * 被 luern 创建于 2021/12/8.
 */

public class LsonAddSuffixTest {
    LsonAddSuffix lsonAddSuffix;
    LsonAddSuffix.LsonAddSuffixConfig config;

    @Before
    public void before() {
        lsonAddSuffix = Mockito.mock(LsonAddSuffix.class);
        config = new LsonAddSuffix.LsonAddSuffixConfig();
    }

    @Test
    public void deserialization() {
        StringBuilder stringBuilder = new StringBuilder();

        Mockito.when(lsonAddSuffix.value()).thenReturn("");
        assertEquals(config.deserialization(stringBuilder, lsonAddSuffix, null).toString(), "");

        stringBuilder.append("aaa");
        Mockito.when(lsonAddSuffix.value()).thenReturn("");
        assertEquals(config.deserialization(stringBuilder, lsonAddSuffix, null).toString(), "aaa");

        stringBuilder.delete(0, stringBuilder.length());
        Mockito.when(lsonAddSuffix.value()).thenReturn("bbb");
        assertEquals(config.deserialization(stringBuilder, lsonAddSuffix, null).toString(), "bbb");

        stringBuilder.append("aaa");
        Mockito.when(lsonAddSuffix.value()).thenReturn("bbb");
        assertEquals(config.deserialization(stringBuilder, lsonAddSuffix, null).toString(), "bbbaaabbb");
    }

    @Test
    public void serialization() {
        StringBuilder stringBuilder = new StringBuilder();

        Mockito.when(lsonAddSuffix.value()).thenReturn("");
        assertEquals(config.serialization(stringBuilder, lsonAddSuffix, null).toString(), "");

        stringBuilder.append("aaa");
        Mockito.when(lsonAddSuffix.value()).thenReturn("");
        assertEquals(config.serialization(stringBuilder, lsonAddSuffix, null).toString(), "aaa");

        stringBuilder.delete(0, stringBuilder.length());
        Mockito.when(lsonAddSuffix.value()).thenReturn("bbb");
        assertEquals(config.serialization(stringBuilder, lsonAddSuffix, null).toString(), "");

        stringBuilder.append("aaa");
        Mockito.when(lsonAddSuffix.value()).thenReturn("bbb");
        assertEquals(config.serialization(stringBuilder, lsonAddSuffix, null).toString(), "aaa");

        stringBuilder.append("bbb");
        Mockito.when(lsonAddSuffix.value()).thenReturn("bbb");
        assertEquals(config.serialization(stringBuilder, lsonAddSuffix, null).toString(), "aaa");

        stringBuilder.append("bbb");
        Mockito.when(lsonAddSuffix.value()).thenReturn("aaa");
        assertEquals(config.serialization(stringBuilder, lsonAddSuffix, null).toString(), "aaabbb");
    }
}

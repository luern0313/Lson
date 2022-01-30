package cn.luern0313.lson.annotation.field;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * 被 luern 创建于 2021/12/21.
 */

public class LsonNumberFormatTest
{
    LsonNumberFormat lsonNumberFormat;
    LsonNumberFormat.LsonNumberFormatConfig config;

    @Before
    public void before()
    {
        lsonNumberFormat = Mockito.mock(LsonNumberFormat.class);
        config = new LsonNumberFormat.LsonNumberFormatConfig();
    }

    @Test
    public void deserialization()
    {
        /*Mockito.when(lsonNumberFormat.digit()).thenReturn();
        Mockito.when(lsonNumberFormat.mode()).thenReturn();
        Mockito.when(lsonNumberFormat.isCastInteger()).thenReturn();
        assertEquals();*/
    }

    @Test
    public void serialization()
    {

    }
}

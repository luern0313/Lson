package cn.luern0313.lson.annotation.field;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

/**
 * 被 luern 创建于 2021/12/12.
 */

public class LsonNumberOperationsTest {
    LsonNumberOperations lsonNumberOperations;
    LsonNumberOperations.LsonNumberOperationsConfig config;

    @Before
    public void before() {
        lsonNumberOperations = Mockito.mock(LsonNumberOperations.class);
        config = new LsonNumberOperations.LsonNumberOperationsConfig();
    }

    @Test
    public void deserialization() {
        // 0.0 + 0.0 = 0.0
        Mockito.when(lsonNumberOperations.number()).thenReturn(0d);
        Mockito.when(lsonNumberOperations.operator()).thenReturn(LsonNumberOperations.Operator.ADD);
        Mockito.when(lsonNumberOperations.isCastInteger()).thenReturn(false);
        assertEquals(config.deserialization(0d, lsonNumberOperations, null), 0d);

        // ((int) 0.0 + 0.0) = 0
        Mockito.when(lsonNumberOperations.number()).thenReturn(0d);
        Mockito.when(lsonNumberOperations.operator()).thenReturn(LsonNumberOperations.Operator.ADD);
        Mockito.when(lsonNumberOperations.isCastInteger()).thenReturn(true);
        assertEquals(config.deserialization(0d, lsonNumberOperations, null), 0);

        // 100.0 - 11.0 = 89.0
        Mockito.when(lsonNumberOperations.number()).thenReturn(11d);
        Mockito.when(lsonNumberOperations.operator()).thenReturn(LsonNumberOperations.Operator.MINUS);
        Mockito.when(lsonNumberOperations.isCastInteger()).thenReturn(false);
        assertEquals(config.deserialization(100d, lsonNumberOperations, null), 89d);

        // 100.0 * 2.0 = 200.0
        Mockito.when(lsonNumberOperations.number()).thenReturn(100d);
        Mockito.when(lsonNumberOperations.operator()).thenReturn(LsonNumberOperations.Operator.MULTIPLY);
        Mockito.when(lsonNumberOperations.isCastInteger()).thenReturn(false);
        assertEquals(config.deserialization(2d, lsonNumberOperations, null), 200d);

        // ((int) 1.1122 * 2.0) = 0
        Mockito.when(lsonNumberOperations.number()).thenReturn(1.1122d);
        Mockito.when(lsonNumberOperations.operator()).thenReturn(LsonNumberOperations.Operator.MULTIPLY);
        Mockito.when(lsonNumberOperations.isCastInteger()).thenReturn(true);
        assertEquals(config.deserialization(2d, lsonNumberOperations, null), 2);

        // 33.33 / 11.0 = 3.03
        Mockito.when(lsonNumberOperations.number()).thenReturn(11d);
        Mockito.when(lsonNumberOperations.operator()).thenReturn(LsonNumberOperations.Operator.DIVISION);
        Mockito.when(lsonNumberOperations.isCastInteger()).thenReturn(false);
        assertEquals(config.deserialization(33.33d, lsonNumberOperations, null), 3.03d);

        // ((int) 33.3 / 11.0) = 3
        Mockito.when(lsonNumberOperations.number()).thenReturn(11d);
        Mockito.when(lsonNumberOperations.operator()).thenReturn(LsonNumberOperations.Operator.DIVISION);
        Mockito.when(lsonNumberOperations.isCastInteger()).thenReturn(true);
        assertEquals(config.deserialization(33.3d, lsonNumberOperations, null), 3);
    }

    @Test
    public void serialization() {
        // 0.0 - 0.0 = 0.0
        Mockito.when(lsonNumberOperations.number()).thenReturn(0d);
        Mockito.when(lsonNumberOperations.operator()).thenReturn(LsonNumberOperations.Operator.ADD);
        assertEquals(config.serialization(0d, lsonNumberOperations, null), 0d);

        // 89.0 + 11.0 = 100.0
        Mockito.when(lsonNumberOperations.number()).thenReturn(11d);
        Mockito.when(lsonNumberOperations.operator()).thenReturn(LsonNumberOperations.Operator.MINUS);
        assertEquals(config.serialization(89d, lsonNumberOperations, null), 100d);

        // 200.0 / 2.0 = 100.0
        Mockito.when(lsonNumberOperations.number()).thenReturn(100d);
        Mockito.when(lsonNumberOperations.operator()).thenReturn(LsonNumberOperations.Operator.MULTIPLY);
        assertEquals(config.serialization(200d, lsonNumberOperations, null), 2d);

        // 33.33 / 3.03 = 11.0
        Mockito.when(lsonNumberOperations.number()).thenReturn(11d);
        Mockito.when(lsonNumberOperations.operator()).thenReturn(LsonNumberOperations.Operator.DIVISION);
        assertEquals(config.serialization(3.03d, lsonNumberOperations, null), 33.33d);
    }
}

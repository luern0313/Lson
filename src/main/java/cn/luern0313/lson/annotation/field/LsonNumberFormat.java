package cn.luern0313.lson.annotation.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import cn.luern0313.lson.annotation.LsonDefinedAnnotation;
import cn.luern0313.lson.annotation.other.AnnotationOrder;
import cn.luern0313.lson.util.DataProcessUtil;

/**
 * 以指定方式保留数字指定的位数。
 *
 * <p>反序列化中：输入{@code Number}类型，输出{@code Number}类型。
 * <p>序列化中：输入{@code Number}类型，输出{@code Number}类型。
 *
 * @author luern0313
 */

@LsonDefinedAnnotation(config = LsonNumberFormat.LsonNumberFormatConfig.class,
        acceptableDeserializationType = LsonDefinedAnnotation.AcceptableType.NUMBER,
        acceptableSerializationType = LsonDefinedAnnotation.AcceptableType.NUMBER)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LsonNumberFormat
{
    /**
     * 保留数字的位数。
     *
     * <p>若此位数为正数，则保留数字小数点后几位。
     *
     * <p>支持0或负数，若为0，则保留至个位数，负数则表示保留至十位、百位等。
     *
     * @return 数字保留位数。
     */
    int digit();

    /**
     * 保留数位的模式。
     *
     * <p>默认为{@link NumberFormatMode#HALF_UP}，即四舍五入(大于等于0.5则进位)
     *
     * @return 数字保留模式。
     */
    NumberFormatMode mode() default NumberFormatMode.HALF_UP;

    /**
     * 是否将最终数字转为int，可去除小数点和小数点后的数字。
     *
     * <p>通常在计算数字后结果为整数时使用，否则将向下取整。
     *
     * @return 是否转为Integer
     */
    boolean isCastInteger() default false;

    /**
     * 用于排序注解的执行顺序，见{@link AnnotationOrder}。
     * @return 注解执行顺序
     */
    @AnnotationOrder int order() default Integer.MAX_VALUE;

    enum NumberFormatMode
    {
        /**
         * 向下取整
         */
        DOWN,

        /**
         * 向上取整
         */
        UP,

        /**
         * 若为正数则进位向上，
         * 若为负数则舍位向上
         */
        CEILING,

        /**
         * 若为正数则舍位向上，
         * 若为负数则进位向上
         */
        FLOOR,

        /**
         * 四舍五入，即大于等于0.5时进位
         */
        HALF_UP,

        /**
         * 四舍五入，大于0.5时进位
         */
        HALF_DOWN,

        /**
         * 若舍弃部分左边的数字为奇数，则遵循{@link NumberFormatMode#HALF_UP}，
         * 若舍弃部分左边的数字为奇数，则遵循{@link NumberFormatMode#HALF_DOWN}，
         */
        HALF_EVEN;

        public static Map<NumberFormatMode, Integer> modeIntegerMap = new HashMap<NumberFormatMode, Integer>()
        {{
            put(DOWN, BigDecimal.ROUND_DOWN);
            put(UP, BigDecimal.ROUND_UP);
            put(CEILING, BigDecimal.ROUND_CEILING);
            put(FLOOR, BigDecimal.ROUND_FLOOR);
            put(HALF_UP, BigDecimal.ROUND_HALF_UP);
            put(HALF_DOWN, BigDecimal.ROUND_HALF_DOWN);
            put(HALF_EVEN, BigDecimal.ROUND_HALF_EVEN);
        }};
    }

    class LsonNumberFormatConfig implements LsonDefinedAnnotation.LsonDefinedAnnotationConfig<LsonNumberFormat>
    {
        @Override
        public Object deserialization(Object value, LsonNumberFormat lsonNumberFormat, Object object)
        {
            return DataProcessUtil.getNumberFormat(value, lsonNumberFormat.digit(), lsonNumberFormat.mode(), lsonNumberFormat.isCastInteger());
        }

        @Override
        public Object serialization(Object value, LsonNumberFormat lsonNumberFormat, Object object)
        {
            return value;
        }
    }
}

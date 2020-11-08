package cn.luern0313.lson.annotation.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.luern0313.lson.annotation.LsonDefinedAnnotation;

/**
 * 将时间戳格式化为指定格式。
 *
 * <p>适用于{@code String}类型的变量。
 *
 * @author luern0313
 */

@LsonDefinedAnnotation(acceptableDeserializationType = LsonDefinedAnnotation.AcceptableType.NUMBER, acceptableSerializationType = LsonDefinedAnnotation.AcceptableType.STRING)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LsonDateFormat
{
    /**
     * 时间格式，用于格式化时间。
     *
     * @return 格式化时间格式。
     *
     * @author luern0313
     */
    String value();

    /**
     * 指定要格式化时间戳的类型
     *
     * @return 时间戳类型
     *
     * @author luern0313
     */
    LsonDateFormatMode mode() default LsonDateFormatMode.SECOND;

    enum LsonDateFormatMode
    {
        /**
         * 秒级时间戳（10位）
         */
        SECOND,

        /**
         * 毫秒级时间戳（13位）
         */
        MILLI_SECOND;
    }
}

package cn.luern0313.lson.annotation.field;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.luern0313.lson.annotation.LsonDefinedAnnotation;
import cn.luern0313.lson.util.DataProcessUtil;

/**
 * 将时间戳格式化为指定格式。
 *
 * <p>反序列化中：输入{@code Number}类型，输出{@code String}类型。
 * <p>序列化中：输入{@code String}类型，输出{@code Number}类型。
 *
 * @author luern0313
 */

@LsonDefinedAnnotation(config = LsonDateFormat.LsonDateFormatConfig.class, acceptableDeserializationType = LsonDefinedAnnotation.AcceptableType.NUMBER, acceptableSerializationType = LsonDefinedAnnotation.AcceptableType.STRING)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LsonDateFormat
{
    /**
     * 时间格式，用于格式化时间。
     *
     * @return 格式化时间格式。
     */
    String value();

    /**
     * 指定要格式化时间戳的类型
     *
     * @return 时间戳类型
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
        MILLI_SECOND
    }

    class LsonDateFormatConfig implements LsonDefinedAnnotation.LsonDefinedAnnotationConfig
    {
        @Override
        public Object deserialization(Object value, Annotation annotation, Object object)
        {
            return DataProcessUtil.getTime(((Number) value).longValue() * (((LsonDateFormat) annotation).mode() == LsonDateFormat.LsonDateFormatMode.SECOND ? 1000 : 0), ((LsonDateFormat) annotation).value());
        }

        @Override
        public Object serialization(Object value, Annotation annotation, Object object)
        {
            return DataProcessUtil.getTimeStamp(value.toString(), ((LsonDateFormat) annotation).value(), ((LsonDateFormat) annotation).mode());
        }
    }
}

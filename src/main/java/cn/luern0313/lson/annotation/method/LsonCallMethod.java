package cn.luern0313.lson.annotation.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 反序列化或序列化执行前后，调用被此注解标注的方法。
 *
 * @author luern0313
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LsonCallMethod
{
    /**
     * 在何时调用此方法，可传入多个值。
     *
     * @return 在何时调用此方法。
     */
    CallMethodTiming[] timing();

    enum CallMethodTiming
    {
        /**
         * 在反序列化之前调用此方法。
         */
        BEFORE_DESERIALIZATION,

        /**
         * 在反序列化之后调用此方法。
         */
        AFTER_DESERIALIZATION,

        /**
         * 在序列化之前调用此方法。
         */
        BEFORE_SERIALIZATION,

        /**
         * 在序列化之后调用此方法。
         */
        AFTER_SERIALIZATION
    }
}

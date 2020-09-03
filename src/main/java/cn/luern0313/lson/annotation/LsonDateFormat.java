package cn.luern0313.lson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.luern0313.lson.LsonDefinedAnnotation;

/**
 * 将时间戳格式化为指定格式。
 *
 * <p>适用于{@code String}类型的变量。
 *
 * @author luern0313
 */

@LsonDefinedAnnotation(applyTypeWhiteList = String.class)
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
}

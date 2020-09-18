package cn.luern0313.lson.annotation.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.luern0313.lson.LsonDefinedAnnotation;

/**
 * 为指定变量添加一个后缀。
 *
 * <p>适用于{@code String}类型的变量。
 *
 * @author luern0313
 */

@LsonDefinedAnnotation(applyTypeWhiteList = String.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LsonAddSuffix
{
    /**
     * 你要添加的后缀。
     *
     * @return 后缀文本。
     *
     * @author luern0313
     */
    String value();
}

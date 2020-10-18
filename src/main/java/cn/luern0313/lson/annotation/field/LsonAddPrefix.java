package cn.luern0313.lson.annotation.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.luern0313.lson.LsonDefinedAnnotation;

/**
 * 为指定变量添加一个前缀。
 *
 * <p>适用于{@code String}类型的变量。
 *
 * @author luern0313
 */

@LsonDefinedAnnotation(acceptableType = LsonDefinedAnnotation.AcceptableType.STRING)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LsonAddPrefix
{
    /**
     * 你要添加的前缀。
     *
     * @return 前缀文本。
     *
     * @author luern0313
     */
    String value();
}

package cn.luern0313.lson.annotation.field;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.luern0313.lson.annotation.LsonDefinedAnnotation;

/**
 * 为指定变量添加一个前缀。
 *
 * <p>反序列化中：输入{@code String}类型，输出{@code String}类型。
 * <p>序列化中：输入{@code String}类型，输出{@code String}类型。
 *
 * @author luern0313
 */

@LsonDefinedAnnotation(config = LsonAddPrefix.LsonAddPrefixConfig.class, acceptableDeserializationType = LsonDefinedAnnotation.AcceptableType.STRING, acceptableSerializationType = LsonDefinedAnnotation.AcceptableType.STRING)
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

    class LsonAddPrefixConfig implements LsonDefinedAnnotation.LsonDefinedAnnotationConfig
    {
        @Override
        public Object deserialization(Object value, Annotation annotation, Object object)
        {
            return ((StringBuilder) value).insert(0, ((LsonAddPrefix) annotation).value());
        }

        @Override
        public Object serialization(Object value, Annotation annotation, Object object)
        {
            if(((StringBuilder) value).indexOf(((LsonAddPrefix) annotation).value()) == 0)
                return ((StringBuilder) value).delete(0, ((LsonAddPrefix) annotation).value().length());
            return value;
        }
    }
}

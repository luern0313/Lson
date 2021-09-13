package cn.luern0313.lson.annotation.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.luern0313.lson.annotation.LsonDefinedAnnotation;

/**
 * 为指定变量添加一个后缀。
 *
 * <p>反序列化中：输入{@code String}类型，输出{@code String}类型。
 * <p>序列化中：输入{@code String}类型，输出{@code String}类型。
 *
 * @author luern0313
 */

@LsonDefinedAnnotation(config = LsonAddSuffix.LsonAddSuffixConfig.class,
        acceptableDeserializationType = LsonDefinedAnnotation.AcceptableType.STRING,
        acceptableSerializationType = LsonDefinedAnnotation.AcceptableType.STRING)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LsonAddSuffix
{
    /**
     * 你要添加的后缀。
     *
     * @return 后缀文本。
     */
    String value();

    class LsonAddSuffixConfig implements LsonDefinedAnnotation.LsonDefinedAnnotationConfig<LsonAddPrefix>
    {
        @Override
        public Object deserialization(Object value, LsonAddPrefix lsonAddPrefix, Object object)
        {
            return ((StringBuilder) value).append(lsonAddPrefix.value());
        }

        @Override
        public Object serialization(Object value, LsonAddPrefix lsonAddPrefix, Object object)
        {
            if(((StringBuilder) value).lastIndexOf(lsonAddPrefix.value()) == ((StringBuilder) value).length() - lsonAddPrefix.value().length())
                return ((StringBuilder) value).delete(((StringBuilder) value).length() - lsonAddPrefix.value().length(), ((StringBuilder) value).length());
            return value;
        }
    }
}

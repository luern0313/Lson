package cn.luern0313.lson.annotation.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.luern0313.lson.LsonUtil;
import cn.luern0313.lson.annotation.LsonDefinedAnnotation;

/**
 * 处理json中的json字符串。
 *
 * <p>如需使用这个注解，请保证<b>上一个注解输出字符串
 * </b>或<b>设置{@link LsonPath#preClass()}为
 * {@link String}</b>。</p>
 *
 * <p>反序列化中：输入{@code String}类型，输出任意类型。
 * <p>序列化中：输入任意类型，输出{@code String}类型。
 *
 * @author luern0313
 */

@LsonDefinedAnnotation(config = LsonParseJsonString.LsonParseJsonStringConfig.class,
        acceptableDeserializationType = LsonDefinedAnnotation.AcceptableType.STRING,
        acceptableSerializationType = LsonDefinedAnnotation.AcceptableType.NOT_HANDLE)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LsonParseJsonString
{
    Class<?> value();

    class LsonParseJsonStringConfig implements LsonDefinedAnnotation.LsonDefinedAnnotationConfig<LsonParseJsonString>
    {
        @Override
        public Object deserialization(Object value, LsonParseJsonString annotation, Object object)
        {
            return LsonUtil.fromJson(LsonUtil.parse(((StringBuilder) value).toString()), ((LsonParseJsonString) annotation).value());
        }

        @Override
        public Object serialization(Object value, LsonParseJsonString annotation, Object object)
        {
            return LsonUtil.toJson(value);
        }
    }
}

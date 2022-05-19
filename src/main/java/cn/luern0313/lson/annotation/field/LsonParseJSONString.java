package cn.luern0313.lson.annotation.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.luern0313.lson.Lson;
import cn.luern0313.lson.annotation.LsonDefinedAnnotation;
import cn.luern0313.lson.annotation.other.AnnotationOrder;

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

@LsonDefinedAnnotation(config = LsonParseJSONString.LsonParseJSONStringConfig.class,
        acceptableDeserializationType = LsonDefinedAnnotation.AcceptableType.STRING,
        acceptableSerializationType = LsonDefinedAnnotation.AcceptableType.NOT_HANDLE)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LsonParseJSONString {
    Class<?> value();

    /**
     * 用于排序注解的执行顺序，见{@link AnnotationOrder}。
     *
     * @return 注解执行顺序
     */
    @AnnotationOrder int order() default Integer.MAX_VALUE;

    class LsonParseJSONStringConfig implements LsonDefinedAnnotation.LsonDefinedAnnotationConfig<LsonParseJSONString> {
        @Override
        public Object deserialization(Object value, LsonParseJSONString lsonParseJsonString, Object object) {
            return Lson.def().fromJson(Lson.def().parse(((StringBuilder) value).toString()), lsonParseJsonString.value());
        }

        @Override
        public Object serialization(Object value, LsonParseJSONString lsonParseJsonString, Object object) {
            return Lson.def().toJson(value);
        }
    }
}

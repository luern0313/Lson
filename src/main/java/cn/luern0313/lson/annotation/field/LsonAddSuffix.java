package cn.luern0313.lson.annotation.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.luern0313.lson.annotation.LsonDefinedAnnotation;
import cn.luern0313.lson.annotation.other.AnnotationOrder;

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
public @interface LsonAddSuffix {
    /**
     * 你要添加的后缀。
     *
     * @return 后缀文本。
     */
    String value();

    /**
     * 用于排序注解的执行顺序，见{@link AnnotationOrder}。
     *
     * @return 注解执行顺序
     */
    @AnnotationOrder int order() default Integer.MAX_VALUE;

    class LsonAddSuffixConfig implements LsonDefinedAnnotation.LsonDefinedAnnotationConfig<LsonAddSuffix> {
        @Override
        public Object deserialization(Object value, LsonAddSuffix lsonAddSuffix, Object object) {
            return ((StringBuilder) value).append(lsonAddSuffix.value());
        }

        @Override
        public Object serialization(Object value, LsonAddSuffix lsonAddSuffix, Object object) {
            if (((StringBuilder) value).lastIndexOf(lsonAddSuffix.value()) == ((StringBuilder) value).length() - lsonAddSuffix.value().length())
                return ((StringBuilder) value).delete(((StringBuilder) value).length() - lsonAddSuffix.value().length(), ((StringBuilder) value).length());
            return value;
        }
    }
}

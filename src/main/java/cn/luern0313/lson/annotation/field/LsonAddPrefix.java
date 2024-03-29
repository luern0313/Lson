package cn.luern0313.lson.annotation.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.luern0313.lson.annotation.LsonDefinedAnnotation;
import cn.luern0313.lson.annotation.other.AnnotationOrder;

/**
 * 为指定变量添加一个前缀。
 *
 * <p>反序列化中：输入{@code String}类型，输出{@code String}类型。
 * <p>序列化中：输入{@code String}类型，输出{@code String}类型。
 *
 * @author luern0313
 */

@LsonDefinedAnnotation(config = LsonAddPrefix.LsonAddPrefixConfig.class,
        acceptableDeserializationType = LsonDefinedAnnotation.AcceptableType.STRING,
        acceptableSerializationType = LsonDefinedAnnotation.AcceptableType.STRING)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LsonAddPrefix {
    /**
     * 你要添加的前缀。
     *
     * @return 前缀文本。
     */
    String value();

    /**
     * 用于排序注解的执行顺序，见{@link AnnotationOrder}。
     *
     * @return 注解执行顺序
     */
    @AnnotationOrder int order() default Integer.MAX_VALUE;

    class LsonAddPrefixConfig implements LsonDefinedAnnotation.LsonDefinedAnnotationConfig<LsonAddPrefix> {
        @Override
        public Object deserialization(Object value, LsonAddPrefix lsonAddPrefix, Object object) {
            return ((StringBuilder) value).insert(0, lsonAddPrefix.value());
        }

        @Override
        public Object serialization(Object value, LsonAddPrefix lsonAddPrefix, Object object) {
            if (((StringBuilder) value).indexOf(lsonAddPrefix.value()) == 0)
                return ((StringBuilder) value).delete(0, lsonAddPrefix.value().length());
            return value;
        }
    }
}

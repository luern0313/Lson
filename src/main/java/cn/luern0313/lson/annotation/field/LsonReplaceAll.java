package cn.luern0313.lson.annotation.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.luern0313.lson.annotation.LsonDefinedAnnotation;
import cn.luern0313.lson.annotation.other.AnnotationOrder;
import cn.luern0313.lson.util.DataProcessUtil;

/**
 * 可按规则替换字符串中的部分文本。
 *
 * <p>按顺序将{@link LsonReplaceAll#regex()}中的文本或正则表达式替换为
 * {@link LsonReplaceAll#replacement()}中的文本。
 *
 * <p>反序列化中：输入{@code String}类型，输出{@code String}类型。
 * <p>序列化中：输入{@code String}类型，输出{@code String}类型。
 *
 * @author luern0313
 */

@LsonDefinedAnnotation(config = LsonReplaceAll.LsonReplaceAllConfig.class,
        acceptableDeserializationType = LsonDefinedAnnotation.AcceptableType.STRING,
        acceptableSerializationType = LsonDefinedAnnotation.AcceptableType.STRING)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LsonReplaceAll {
    /**
     * 要替换的文本或正则表达式。
     *
     * @return 要替换的文本数组。
     */
    String[] regex();

    /**
     * 替换后的文本。
     *
     * @return 替换后的文本数组。
     */
    String[] replacement();

    /**
     * 用于排序注解的执行顺序，见{@link AnnotationOrder}。
     *
     * @return 注解执行顺序
     */
    @AnnotationOrder int order() default Integer.MAX_VALUE;

    class LsonReplaceAllConfig implements LsonDefinedAnnotation.LsonDefinedAnnotationConfig<LsonReplaceAll> {
        @Override
        public Object deserialization(Object value, LsonReplaceAll lsonReplaceAll, Object object) {
            String[] regexArray = lsonReplaceAll.regex();
            String[] replacementArray = lsonReplaceAll.replacement();
            for (int i = 0; i < regexArray.length; i++)
                DataProcessUtil.replaceAll((StringBuilder) value, regexArray[i], replacementArray[i]);
            return value;
        }

        @Override
        public Object serialization(Object value, LsonReplaceAll lsonReplaceAll, Object object) {
            String[] regexArray = lsonReplaceAll.regex();
            String[] replacementArray = lsonReplaceAll.replacement();
            for (int i = 0; i < regexArray.length; i++)
                DataProcessUtil.replaceAll((StringBuilder) value, replacementArray[i], regexArray[i]);
            return value;
        }
    }
}

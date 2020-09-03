package cn.luern0313.lson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.luern0313.lson.LsonDefinedAnnotation;

/**
 * 可按规则替换字符串中的部分文本。
 *
 * <p>按顺序将{@link LsonReplaceAll#regex()}中的文本或正则表达式替换为
 * {@link LsonReplaceAll#replacement()}中的文本。
 *
 * <p>适用于{@code String}类型的变量。
 *
 * @author luern0313
 */

@LsonDefinedAnnotation(applyTypeWhiteList = String.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LsonReplaceAll
{
    /**
     * 要替换的文本或正则表达式。
     *
     * @return 要替换的文本数组。
     *
     * @author luern0313
     */
    String[] regex();

    /**
     * 替换后的文本。
     *
     * @return 替换后的文本数组。
     *
     * @author luern0313
     */
    String[] replacement();
}

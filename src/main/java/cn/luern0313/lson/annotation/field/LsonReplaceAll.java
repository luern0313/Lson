package cn.luern0313.lson.annotation.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.luern0313.lson.annotation.LsonDefinedAnnotation;

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

@LsonDefinedAnnotation(acceptableDeserializationType = LsonDefinedAnnotation.AcceptableType.STRING, acceptableSerializationType = LsonDefinedAnnotation.AcceptableType.STRING)
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

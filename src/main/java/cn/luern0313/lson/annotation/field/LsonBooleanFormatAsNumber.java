package cn.luern0313.lson.annotation.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.luern0313.lson.annotation.LsonDefinedAnnotation;

/**
 * 将传入的数转为{@code Boolean}类型。
 *
 * <p>反序列化中：输入{@code Number}类型，输出{@code Boolean}类型。
 * <p>序列化中：输入{@code Boolean}类型，输出{@code Number}类型。
 *
 * @author luern0313
 */

@LsonDefinedAnnotation(acceptableDeserializationType = LsonDefinedAnnotation.AcceptableType.NUMBER, acceptableSerializationType = LsonDefinedAnnotation.AcceptableType.BOOLEAN)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LsonBooleanFormatAsNumber
{
    /**
     * 若此数组不为空，且传入的数据在此数组中，则输出true。
     *
     * @return 数字数组。
     *
     * @author luern0313
     */
    double[] equal() default {};

    /**
     * 若此数组不为空，且传入的数据不在此数组中，则输出true。
     *
     * @return 数字数组。
     *
     * @author luern0313
     */
    double[] notEqual() default {};
}

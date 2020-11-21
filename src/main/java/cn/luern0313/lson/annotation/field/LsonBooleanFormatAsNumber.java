package cn.luern0313.lson.annotation.field;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.luern0313.lson.annotation.LsonDefinedAnnotation;
import cn.luern0313.lson.util.DataProcessUtil;
import cn.luern0313.lson.util.TypeUtil;

/**
 * 将传入的数转为{@code Boolean}类型。
 *
 * <p>反序列化中：输入{@code Number}类型，输出{@code Boolean}类型。
 * <p>序列化中：输入{@code Boolean}类型，输出{@code Number}类型。
 *
 * @author luern0313
 */

@LsonDefinedAnnotation(config = LsonBooleanFormatAsNumber.LsonBooleanFormatAsNumberConfig.class, acceptableDeserializationType = LsonDefinedAnnotation.AcceptableType.NUMBER, acceptableSerializationType = LsonDefinedAnnotation.AcceptableType.BOOLEAN)
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

    class LsonBooleanFormatAsNumberConfig implements LsonDefinedAnnotation.LsonDefinedAnnotationConfig
    {
        @Override
        public Object deserialization(Object value, Annotation annotation, TypeUtil fieldType)
        {
            int result = -1;
            if(((LsonBooleanFormatAsNumber) annotation).equal().length > 0)
                result = DataProcessUtil.getIndex(((Number) value).doubleValue(), ((LsonBooleanFormatAsNumber) annotation).equal()) > -1 ? 1 : 0;
            if(((LsonBooleanFormatAsNumber) annotation).notEqual().length > 0)
                result = (DataProcessUtil.getIndex(((Number) value).doubleValue(), ((LsonBooleanFormatAsNumber) annotation).notEqual()) == -1 && result != 0) ? 1 : 0;
            return result != -1 ? result == 1 : ((double) value) != 0;
        }

        @Override
        public Object serialization(Object value, Annotation annotation, TypeUtil fieldType)
        {
            return null;
        }
    }
}

package cn.luern0313.lson.annotation.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.luern0313.lson.annotation.LsonDefinedAnnotation;
import cn.luern0313.lson.annotation.other.AnnotationOrder;
import cn.luern0313.lson.util.DataProcessUtil;

/**
 * 将传入的数转为{@code Boolean}类型。
 *
 * <p>反序列化中：输入{@code Number}类型，输出{@code Boolean}类型。
 * <p>序列化中：输入{@code Boolean}类型，输出{@code Number}类型。
 *
 * @author luern0313
 */

@LsonDefinedAnnotation(config = LsonBooleanFormatAsNumber.LsonBooleanFormatAsNumberConfig.class,
        acceptableDeserializationType = LsonDefinedAnnotation.AcceptableType.NUMBER,
        acceptableSerializationType = LsonDefinedAnnotation.AcceptableType.BOOLEAN)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LsonBooleanFormatAsNumber
{
    /**
     * 若此数组不为空，且传入的数据在此数组中，则输出true。
     *
     * @return 数字数组。
     */
    double[] equal() default {};

    /**
     * 若此数组不为空，且传入的数据不在此数组中，则输出true。
     *
     * @return 数字数组。
     */
    double[] notEqual() default {};

    /**
     * 用于排序注解的执行顺序，见{@link AnnotationOrder}。
     * @return 注解执行顺序
     */
    @AnnotationOrder int order() default Integer.MAX_VALUE;

    class LsonBooleanFormatAsNumberConfig implements LsonDefinedAnnotation.LsonDefinedAnnotationConfig<LsonBooleanFormatAsNumber>
    {
        @Override
        public Object deserialization(Object value, LsonBooleanFormatAsNumber lsonBooleanFormatAsNumber, Object object)
        {
            int result = -1;
            if(lsonBooleanFormatAsNumber.equal().length > 0)
                result = DataProcessUtil.getIndex(((Number) value).doubleValue(), lsonBooleanFormatAsNumber.equal()) > -1 ? 1 : 0;
            if(lsonBooleanFormatAsNumber.notEqual().length > 0)
                result = (DataProcessUtil.getIndex(((Number) value).doubleValue(), lsonBooleanFormatAsNumber.notEqual()) == -1 && result != 0) ? 1 : 0;
            return result != -1 ? result == 1 : ((double) value) != 0;
        }

        @Override
        public Object serialization(Object value, LsonBooleanFormatAsNumber lsonBooleanFormatAsNumber, Object object)
        {
            return null;
        }
    }
}

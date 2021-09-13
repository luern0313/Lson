package cn.luern0313.lson.annotation.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.luern0313.lson.annotation.LsonDefinedAnnotation;
import cn.luern0313.lson.util.DataProcessUtil;

/**
 * 将传入的数转为{@code Boolean}类型。
 *
 * <p>反序列化中：输入{@code String}类型，输出{@code Boolean}类型。
 * <p>序列化中：输入{@code Boolean}类型，输出{@code String}类型。
 *
 * @author luern0313
 */

@LsonDefinedAnnotation(config = LsonBooleanFormatAsString.LsonBooleanFormatAsStringConfig.class,
        acceptableDeserializationType = LsonDefinedAnnotation.AcceptableType.STRING,
        acceptableSerializationType = LsonDefinedAnnotation.AcceptableType.BOOLEAN)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LsonBooleanFormatAsString
{
    /**
     * 若此数组不为空，且传入的数据在此数组中，则输出true。
     *
     * @return 数字数组。
     */
    String[] equal() default {};

    /**
     * 若此数组不为空，且传入的数据不在此数组中，则输出true。
     *
     * @return 数字数组。
     */
    String[] notEqual() default {};

    class LsonBooleanFormatAsStringConfig implements LsonDefinedAnnotation.LsonDefinedAnnotationConfig<LsonBooleanFormatAsString>
    {
        @Override
        public Object deserialization(Object value, LsonBooleanFormatAsString lsonBooleanFormatAsString, Object object)
        {
            int result = -1;
            if(lsonBooleanFormatAsString.equal().length > 0)
                result = DataProcessUtil.getIndex(((StringBuilder) value).toString(), lsonBooleanFormatAsString.equal()) > -1 ? 1 : 0;
            if(lsonBooleanFormatAsString.notEqual().length > 0)
                result = (DataProcessUtil.getIndex(((StringBuilder) value).toString(), lsonBooleanFormatAsString.notEqual()) == -1 && result != 0) ? 1 : 0;
            return result != -1 ? result == 1 : !((StringBuilder) value).toString().equals("");
        }

        @Override
        public Object serialization(Object value, LsonBooleanFormatAsString lsonBooleanFormatAsString, Object object)
        {
            return null;
        }
    }
}

package cn.luern0313.lson.annotation.field;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;

import cn.luern0313.lson.annotation.LsonDefinedAnnotation;
import cn.luern0313.lson.util.DataProcessUtil;
import cn.luern0313.lson.util.TypeUtil;

/**
 * 用指定的分割符分割字符串为数组。
 *
 * <p>反序列化中：输入{@code String}类型，输出任意类型。
 * <p>序列化中：输入任意类型，输出{@code String}类型。
 *
 * @author luern0313
 */

@LsonDefinedAnnotation(config = LsonSplitString.LsonSplitStringConfig.class, acceptableDeserializationType = LsonDefinedAnnotation.AcceptableType.STRING, acceptableSerializationType = LsonDefinedAnnotation.AcceptableType.NOT_HANDLE, isIgnoreArray = true, isIgnoreList = true, isIgnoreMap = true)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LsonSplitString
{
    /**
     * 分割符，用于分割字符串。
     *
     * @return 分割符。
     */
    String value();

    /**
     * 是否输出{@link ArrayList}，若否，则使用{@link String[]}。
     *
     * @return 是否输出ArrayList类型。
     */
    boolean useArrayList() default false;

    class LsonSplitStringConfig implements LsonDefinedAnnotation.LsonDefinedAnnotationConfig
    {
        @Override
        public Object deserialization(Object value, Annotation annotation, Object object)
        {
            String[] array = ((StringBuilder) value).toString().split(((LsonSplitString) annotation).value());
            if(((LsonSplitString) annotation).useArrayList())
                return new ArrayList<>(Arrays.asList(array));
            return array;
        }

        @Override
        public Object serialization(Object value, Annotation annotation, Object object)
        {
            TypeUtil typeUtil = new TypeUtil(value);
            if(typeUtil.isListTypeClass())
                return DataProcessUtil.join((ArrayList<?>) value, ((LsonSplitString) annotation).value());
            else if(typeUtil.isArrayTypeClass())
                return DataProcessUtil.join((Object[]) value, ((LsonSplitString) annotation).value());
            return null;
        }
    }
}

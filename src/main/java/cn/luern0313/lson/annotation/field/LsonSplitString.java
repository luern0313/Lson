package cn.luern0313.lson.annotation.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

@LsonDefinedAnnotation(config = LsonSplitString.LsonSplitStringConfig.class,
        acceptableDeserializationType = LsonDefinedAnnotation.AcceptableType.STRING,
        acceptableSerializationType = LsonDefinedAnnotation.AcceptableType.NOT_HANDLE,
        isIgnoreArray = true, isIgnoreList = true, isIgnoreMap = true)
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
     * 输出数组的类型，默认为String[]。
     *
     * <p>如使用List，请确保目标Class实现List接口且不为接口或抽象类。
     * <p>如使用Array，数组类型只能为String。
     *
     * @return 输出的数组类型。
     */
    Class<?> arrayType() default String[].class;

    class LsonSplitStringConfig implements LsonDefinedAnnotation.LsonDefinedAnnotationConfig<LsonSplitString>
    {
        @SuppressWarnings("unchecked")
        @Override
        public Object deserialization(Object value, LsonSplitString lsonSplitString, Object object)
        {
            try
            {
                TypeUtil typeUtil = new TypeUtil(lsonSplitString.arrayType());
                String[] array = ((StringBuilder) value).toString().split(lsonSplitString.value());
                if(typeUtil.isArrayType() && typeUtil.getArrayElementType().getAsClass() == String.class)
                    return array;
                else if(typeUtil.isListType())
                {
                    List<String> list = (List<String>) typeUtil.getListType().newInstance();
                    list.addAll(Arrays.asList(array));
                    return list;
                }
            }
            catch (InstantiationException | IllegalAccessException ignored)
            {
            }
            return null;
        }

        @Override
        public Object serialization(Object value, LsonSplitString lsonSplitString, Object object)
        {
            TypeUtil typeUtil = new TypeUtil(value);
            if(typeUtil.isListType())
                return DataProcessUtil.join((ArrayList<?>) value, lsonSplitString.value());
            else if(typeUtil.isArrayType())
                return DataProcessUtil.join((Object[]) value, lsonSplitString.value());
            return null;
        }
    }
}

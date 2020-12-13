package cn.luern0313.lson.annotation.field;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cn.luern0313.lson.annotation.LsonDefinedAnnotation;

/**
 * 以变量为参数，调用类内的方法。
 *
 * <p>反序列化中：输入任意类型，输出任意类型。
 * <p>序列化中：输入任意类型，输出任意类型。
 *
 * @author luern0313
 */

@LsonDefinedAnnotation(config = LsonFieldCallMethod.LsonCallMethodConfig.class, acceptableDeserializationType = LsonDefinedAnnotation.AcceptableType.NOT_HANDLE, acceptableSerializationType = LsonDefinedAnnotation.AcceptableType.NOT_HANDLE, isIgnoreArray = true, isIgnoreList = true, isIgnoreMap = true)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LsonFieldCallMethod
{
    /**
     * 反序列化中，需要调用的方法名称。
     *
     * @return 方法名称。
     */
    String deserialization() default "";

    /**
     * 序列化中，需要调用的方法名称。
     *
     * @return 方法名称。
     */
    String serialization() default "";

    class LsonCallMethodConfig implements LsonDefinedAnnotation.LsonDefinedAnnotationConfig
    {
        @Override
        public Object deserialization(Object value, Annotation annotation, Object object)
        {
            try
            {
                if(!((LsonFieldCallMethod) annotation).deserialization().equals(""))
                {
                    Class<?> clz = object.getClass();
                    Method method = clz.getDeclaredMethod(((LsonFieldCallMethod) annotation).deserialization(), value.getClass());
                    method.setAccessible(true);
                    return method.invoke(object, value);
                }
            }
            catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored)
            {
            }
            return null;
        }

        @Override
        public Object serialization(Object value, Annotation annotation, Object object)
        {
            try
            {
                if(!((LsonFieldCallMethod) annotation).serialization().equals(""))
                {
                    Class<?> clz = object.getClass();
                    Method method = clz.getDeclaredMethod(((LsonFieldCallMethod) annotation).serialization(), value.getClass());
                    method.setAccessible(true);
                    return method.invoke(object, value);
                }
            }
            catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored)
            {
            }
            return null;
        }
    }
}

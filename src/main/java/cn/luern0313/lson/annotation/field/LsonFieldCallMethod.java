package cn.luern0313.lson.annotation.field;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cn.luern0313.lson.annotation.LsonDefinedAnnotation;
import cn.luern0313.lson.element.LsonElement;

/**
 * 被 luern0313 创建于 2020/11/28.
 */

@LsonDefinedAnnotation(config = LsonFieldCallMethod.LsonCallMethodConfig.class, acceptableDeserializationType = LsonDefinedAnnotation.AcceptableType.NOT_HANDLE, acceptableSerializationType = LsonDefinedAnnotation.AcceptableType.NOT_HANDLE, isIgnoreArray = true, isIgnoreList = true, isIgnoreMap = true)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LsonFieldCallMethod
{
    String deserialization() default "";

    String serialization() default "";

    @SuppressWarnings("JavaReflectionInvocation")
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
                    Method method = clz.getDeclaredMethod(((LsonFieldCallMethod) annotation).deserialization(), LsonElement.class);
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
                    Method method = clz.getDeclaredMethod(((LsonFieldCallMethod) annotation).serialization(), LsonElement.class);
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

package cn.luern0313.lson.annotation.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cn.luern0313.lson.annotation.LsonDefinedAnnotation;
import cn.luern0313.lson.annotation.other.AnnotationOrder;

/**
 * 以变量为参数，调用类内的方法。
 *
 * <p>反序列化中：输入任意类型，输出任意类型。
 * <p>序列化中：输入任意类型，输出任意类型。
 *
 * @author luern0313
 */

@LsonDefinedAnnotation(config = LsonFieldCallMethod.LsonCallMethodConfig.class,
        acceptableDeserializationType = LsonDefinedAnnotation.AcceptableType.NOT_HANDLE,
        acceptableSerializationType = LsonDefinedAnnotation.AcceptableType.NOT_HANDLE,
        isIgnoreArray = true, isIgnoreList = true, isIgnoreMap = true)
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

    /**
     * 用于排序注解的执行顺序，见{@link AnnotationOrder}。
     * @return 注解执行顺序
     */
    @AnnotationOrder int order() default Integer.MAX_VALUE;

    class LsonCallMethodConfig implements LsonDefinedAnnotation.LsonDefinedAnnotationConfig<LsonFieldCallMethod>
    {
        @Override
        public Object deserialization(Object value, LsonFieldCallMethod lsonFieldCallMethod, Object object)
        {
            try
            {
                if(!lsonFieldCallMethod.deserialization().equals(""))
                {
                    Class<?> clz = object.getClass();
                    Method method = clz.getDeclaredMethod(lsonFieldCallMethod.deserialization(), value.getClass());
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
        public Object serialization(Object value, LsonFieldCallMethod lsonFieldCallMethod, Object object)
        {
            try
            {
                if(!lsonFieldCallMethod.serialization().equals(""))
                {
                    Class<?> clz = object.getClass();
                    Method method = clz.getDeclaredMethod(lsonFieldCallMethod.serialization(), value.getClass());
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

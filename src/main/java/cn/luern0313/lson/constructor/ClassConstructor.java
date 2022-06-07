package cn.luern0313.lson.constructor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import cn.luern0313.lson.exception.InstantiationException;
import cn.luern0313.lson.util.TypeUtil;
import sun.misc.Unsafe;

/**
 * 类的实例化实现类
 *
 * <p>创建于 2022/5/3.
 */

public class ClassConstructor {
    private final CustomConstructorList customConstructorList;

    public ClassConstructor(CustomConstructorList customConstructorList) {
        this.customConstructorList = customConstructorList;
    }

    public Object create(TypeUtil typeUtil, Object genericSuperclass) {
        try {
            if (customConstructorList != null) {
                CustomConstructor<?> customConstructor = customConstructorList.get(typeUtil);
                if (customConstructor != null) {
                    Object instance = customConstructor.create(typeUtil.getAsType());
                    if (instance instanceof InstanceResult)
                        return ((InstanceResult<?>) instance).getInstance();
                    else
                        return instance;
                }
            }
        } catch (RuntimeException ignored) {
        }

        try {
            Object object = null;
            Constructor<?> constructor = typeUtil.getConstructor();
            if (constructor != null)
                object = constructor.newInstance();
            else if (genericSuperclass != null) {
                Constructor<?> genericConstructor = typeUtil.getConstructor(genericSuperclass.getClass());
                object = genericConstructor.newInstance(genericSuperclass);
            }
            if (object != null)
                return object;
        } catch (RuntimeException | IllegalAccessException | java.lang.InstantiationException | InvocationTargetException ignored) {
        }

        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Unsafe unsafe = (Unsafe) field.get(null);
            return unsafe.allocateInstance(typeUtil.getAsClass());
        } catch (IllegalAccessException | NoSuchFieldException | java.lang.InstantiationException ignored) {
        }

        throw new InstantiationException(typeUtil.getName(), "There is no construction method specified.");
    }
}

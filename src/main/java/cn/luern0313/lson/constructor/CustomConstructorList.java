package cn.luern0313.lson.constructor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

import cn.luern0313.lson.util.TypeUtil;

/**
 * 被 luern 创建于 2022/5/4.
 */

public class CustomConstructorList {
    private final HashMap<Class<?>, CustomConstructor<?>> customConstructorMap;

    public CustomConstructorList() {
        customConstructorMap = new HashMap<>();
    }

    public void add(CustomConstructor<?> customConstructor) {
        if (customConstructor != null) {
            Type type = customConstructor.getClass().getGenericInterfaces()[0];
            Class<?> clz = ((Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0]);
            customConstructorMap.put(clz, customConstructor);
        }
    }

    public void addAll(CustomConstructorList customConstructorList) {
        if (customConstructorList != null) {
            customConstructorMap.putAll(customConstructorList.customConstructorMap);
        }
    }

    public CustomConstructor<?> get(TypeUtil typeUtil) {
        return customConstructorMap.get(typeUtil.getAsClass());
    }
}
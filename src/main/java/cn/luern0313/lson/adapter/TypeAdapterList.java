package cn.luern0313.lson.adapter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

import cn.luern0313.lson.util.TypeUtil;

/**
 * 被 luern 创建于 2022/5/1.
 */

public class TypeAdapterList {
    private final HashMap<Class<?>, TypeAdapter<?>> typeAdapterMap;

    public TypeAdapterList() {
        typeAdapterMap = new HashMap<>();
    }

    public void add(TypeAdapter<?> typeAdapter) {
        if (typeAdapter != null) {
            Type type = typeAdapter.getClass().getGenericInterfaces()[0];
            Class<?> clz = ((Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0]);
            typeAdapterMap.put(clz, typeAdapter);
        }
    }

    public void addAll(TypeAdapterList typeAdapterList) {
        if (typeAdapterList != null) {
            typeAdapterMap.putAll(typeAdapterList.typeAdapterMap);
        }
    }

    public TypeAdapter<?> get(TypeUtil typeUtil) {
        return typeAdapterMap.get(typeUtil.getAsClass());
    }
}

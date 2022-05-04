package cn.luern0313.lson.constructor;

import java.lang.reflect.Type;

import cn.luern0313.lson.util.TypeUtil;

/**
 * 被 luern 创建于 2022/5/3.
 */

public interface CustomConstructor<T> {
    T create(Type type);
    default boolean isAAA() {
        return false;
    }
}

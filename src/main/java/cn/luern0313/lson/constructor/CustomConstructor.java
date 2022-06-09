package cn.luern0313.lson.constructor;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

/**
 * 自定义实例化方法接口
 *
 * <p>创建于 2022/5/3.
 */
public abstract class CustomConstructor<T> {
    public abstract T create(Type type, @Nullable Object... parameter) throws Exception;
}
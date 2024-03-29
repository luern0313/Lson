package cn.luern0313.lson.adapter;

import org.jetbrains.annotations.NotNull;

import cn.luern0313.lson.element.LsonElement;

/**
 * 自定义类的反序列化 / 序列化过程
 *
 * <p>创建于 2022/4/30.
 */

public interface TypeAdapter<T> {
    /**
     * 自定义某个类的反序列化过程
     *
     * @param value json中的元素，这个变量不会为null，但可能为LsonNull
     * @return 该类的实例
     */
    default T deserialization(@NotNull LsonElement value) {
        return null;
    }

    /**
     * 自定义某个类的序列化过程
     *
     * @param obj 该类的实例
     * @return 序列化后的LsonElement实例
     */
    default LsonElement serialization(T obj) {
        return null;
    }
}

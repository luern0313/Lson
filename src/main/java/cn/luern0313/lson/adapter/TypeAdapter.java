package cn.luern0313.lson.adapter;

import cn.luern0313.lson.element.LsonElement;

/**
 * 被 luern 创建于 2022/4/30.
 */

public interface TypeAdapter<T> {
    T deserialization(Object value);
    LsonElement serialization(T object);
}

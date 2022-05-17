package cn.luern0313.lson.element;

import cn.luern0313.lson.Lson;
import cn.luern0313.lson.TypeReference;

/**
 * JSON元素基类。
 *
 * @author luern0313
 */

public abstract class LsonElement {
    public boolean isLsonObject() {
        return false;
    }

    public LsonObject getAsLsonObject() {
        return null;
    }

    public boolean isLsonArray() {
        return false;
    }

    public LsonArray getAsLsonArray() {
        return null;
    }

    public boolean isLsonPrimitive() {
        return false;
    }

    public LsonPrimitive getAsLsonPrimitive() {
        return null;
    }

    public boolean isLsonNull() {
        return false;
    }

    /**
     * 根据JSONPath返回对应的值。
     *
     * @param path JSONPath，用于描述要取到的值在json中的位置。
     * @return JSONPath对应的值。
     */
    public Object getFromPath(String path) {
        return Lson.def().getValue(this, path);
    }

    /**
     * 根据JSONPath返回对应的值，并指明该值的类型。
     *
     * @param path JSONPath，用于描述要取到的值在json中的位置。
     * @param clz  该值的类型，Lson会尝试将该值转为指定的类型。
     * @param <T>  指定的类型。
     * @return JSONPath对应的值。
     */
    public <T> T getFromPath(String path, Class<T> clz) {
        return Lson.def().getValue(this, path, clz);
    }

    /**
     * 根据JSONPath返回对应的值，并在存在泛型的情况下指明该值的类型。
     *
     * @param path          JSONPath，用于描述要取到的值在json中的位置。
     * @param typeReference 用于描述一个泛型类。
     * @param <T>           指定的类型。
     * @return JSONPath对应的值。
     */
    public <T> T getFromPath(String path, TypeReference<T> typeReference) {
        return Lson.def().getValue(this, path, typeReference.type);
    }

    /**
     * 根据JSONPath将数据填充至LsonElement中。
     *
     * @param path  标注数据位置的JSONPath。
     * @param value 要填充的数据。
     * @return 填充完成的LsonElement。
     */
    public LsonElement putFromPath(String path, Object value) {
        return Lson.def().putValue(this, path, value);
    }

    public abstract LsonElement deepCopy();

    @Override
    public abstract String toString();
}

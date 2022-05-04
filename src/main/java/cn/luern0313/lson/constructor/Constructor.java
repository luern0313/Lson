package cn.luern0313.lson.constructor;

import java.lang.reflect.Type;

import cn.luern0313.lson.util.TypeUtil;

/**
 * 被 luern 创建于 2022/5/3.
 */

public class Constructor<T> {
    private TypeUtil typeUtil;
    private Object genericSuperclass;

    public Constructor(TypeUtil typeUtil, Object genericSuperclass) {
        this.typeUtil = typeUtil;
        this.genericSuperclass = genericSuperclass;
    }

    public T create() {

    }
}

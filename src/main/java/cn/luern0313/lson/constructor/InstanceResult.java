package cn.luern0313.lson.constructor;

/**
 * 被 luern 创建于 2022/6/3.
 */

public class InstanceResult<T> {
    private final T instance;
    public InstanceResult(T instance) {
        this.instance = instance;
    }

    public T getInstance() {
        return instance;
    }
}

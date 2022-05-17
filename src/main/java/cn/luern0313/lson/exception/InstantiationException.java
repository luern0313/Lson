package cn.luern0313.lson.exception;

/**
 * 实例化时错误
 *
 * <p>创建于 2020/8/15.
 */

public class InstantiationException extends RuntimeException {
    public InstantiationException(String className, String reason) {
        super(String.format("Instantiation %s class error.%nReason: %s", className, reason));
    }
}

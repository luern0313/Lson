package cn.luern0313.lson.exception;

/**
 * 被 luern0313 创建于 2020/8/15.
 */

public class InstantiationException extends RuntimeException
{
    public InstantiationException(String className)
    {
        super(String.format("Instantiation %s class error.", className));
    }
}

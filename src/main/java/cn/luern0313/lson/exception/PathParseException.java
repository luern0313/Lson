package cn.luern0313.lson.exception;

/**
 * 被 luern0313 创建于 2020/8/9.
 */

public class PathParseException extends RuntimeException
{
    String message;
    int index;

    public PathParseException()
    {
        this("Unknown Exception.");
    }

    public PathParseException(String message)
    {
        this(message, -1);
    }

    public PathParseException(String message, int index)
    {
        this.message = message;
        this.index = index;
    }

    @Override
    public String getMessage()
    {
        return message + " " + index;
    }
}

package cn.luern0313.lson.exception;

/**
 * 被 luern0313 创建于 2020/8/23.
 */

public class JsonParseException extends RuntimeException
{
    String message;
    int index;

    public JsonParseException()
    {
        this("Unknown Exception.");
    }

    public JsonParseException(String message)
    {
        this(message, -1);
    }

    public JsonParseException(String message, int index)
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

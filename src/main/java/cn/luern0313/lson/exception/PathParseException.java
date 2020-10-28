package cn.luern0313.lson.exception;

import cn.luern0313.lson.util.CharReader;

/**
 * 被 luern0313 创建于 2020/8/9.
 */

public class PathParseException extends RuntimeException
{
    String message;
    CharReader.ErrorMessage errorMessage;

    public PathParseException()
    {
        this("Unknown Exception.");
    }

    public PathParseException(String message)
    {
        this(message, null);
    }

    public PathParseException(String message, CharReader.ErrorMessage errorMessage)
    {
        this.message = message;
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage()
    {
        StringBuilder error = new StringBuilder();
        if(errorMessage != null)
        {
            error.append(message).append(" in index ").append(errorMessage.index).append(": ");
            int length = error.length() + errorMessage.messageErrorIndex + 47;
            error.append(errorMessage.message).append("\n");
            for (int i = 0; i < length; i++)
                error.append(" ");
            error.append("∧");
        }
        else
            error.append(message);
        return error.toString();
    }
}

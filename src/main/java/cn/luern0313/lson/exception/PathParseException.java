package cn.luern0313.lson.exception;

import cn.luern0313.lson.util.CharReader;

/**
 * 被 luern0313 创建于 2020/8/9.
 */

public class PathParseException extends RuntimeException
{
    String message;
    int index;
    CharReader.ErrorMessage errorMessage;

    public PathParseException()
    {
        this("Unknown Exception.");
    }

    public PathParseException(String message)
    {
        this(message, -1, null);
    }

    public PathParseException(String message, int index, CharReader.ErrorMessage errorMessage)
    {
        this.message = message;
        this.index = index;
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage()
    {
        StringBuilder error = new StringBuilder();
        if(index > -1 && errorMessage != null)
        {
            error.append(message).append(" in index ").append(index).append(": ");
            int length = error.length() + errorMessage.index + 48;
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

package cn.luern0313.lson.exception;

import cn.luern0313.lson.util.CharReaderUtil;

/**
 * 被 luern0313 创建于 2020/8/23.
 */

public class JsonParseException extends RuntimeException
{
    String message;
    CharReaderUtil.ErrorMessage errorMessage;

    public JsonParseException()
    {
        this("Unknown Exception.");
    }

    public JsonParseException(String message)
    {
        this(message, null);
    }

    public JsonParseException(String message, CharReaderUtil.ErrorMessage errorMessage)
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

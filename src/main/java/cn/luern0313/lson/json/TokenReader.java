package cn.luern0313.lson.json;

import cn.luern0313.lson.exception.JsonParseException;

/**
 * 被 luern0313 创建于 2020/8/22.
 */

class TokenReader
{
    CharReader reader;

    TokenReader(CharReader reader)
    {
        this.reader = reader;
    }

    boolean isWhiteSpace(char ch)
    {
        return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r';
    }

    TokenType readNextToken()
    {
        if(!reader.hasMore())
            return TokenType.END_DOCUMENT;
        char ch = reader.peek();
        while (isWhiteSpace(ch))
            reader.next();
        switch (ch)
        {
            case '{':
                reader.next();
                return TokenType.OBJECT_BEGIN;
            case '}':
                reader.next();
                return TokenType.OBJECT_END;
            case '[':
                reader.next();
                return TokenType.ARRAY_BEGIN;
            case ']':
                reader.next();
                return TokenType.ARRAY_END;
            case ':':
                reader.next();
                return TokenType.SPLIT_COLON;
            case ',':
                reader.next();
                return TokenType.SPLIT_COMMA;
            case '-':
                return TokenType.NUMBER;
            case 'n':
                return TokenType.NULL;
            case 't':
            case 'f':
                return TokenType.BOOLEAN;
        }
        if(ch >= '0' && ch <= '9')
            return TokenType.NUMBER;
        else if(ch == '"')
            return TokenType.STRING;
        System.out.println(reader.pos);
        throw new JsonParseException("Unexpected char " + ch);
    }

    String readString()
    {
        StringBuilder sb = new StringBuilder();
        reader.next();
        while (true)
        {
            char ch = reader.next();

            if(ch == '\\')
            {
                if(!isEscape())
                    throw new JsonParseException("Invalid escape character");
                sb.append('\\');
                ch = reader.peek();
                sb.append(ch);
                if(ch == 'u')
                {
                    for (int i = 0; i < 4; i++)
                    {
                        ch = reader.next();
                        if(isHex(ch))
                            sb.append(ch);
                        else
                            throw new JsonParseException("Invalid character");
                    }
                }
            }
            else if(ch == '"')
                return sb.toString();
            else if(ch == '\r' || ch == '\n')
                throw new JsonParseException("Invalid character");
            else
                sb.append(ch);
        }
    }

    private boolean isEscape()
    {
        char ch = reader.next();
        return (ch == '"' || ch == '\\' || ch == 'u' || ch == 'r' || ch == 'n' || ch == 'b' || ch == 't' || ch == 'f' || ch == '/');
    }

    private boolean isHex(char ch)
    {
        return ((ch >= '0' && ch <= '9') || ('a' <= ch && ch <= 'f') || ('A' <= ch && ch <= 'F'));
    }

    Number readNumber()
    {
        StringBuilder sb = new StringBuilder();
        char ch;

        while (reader.hasMore())
        {
            ch = reader.peek();
            if((ch >= '0' && ch <= '9') || ch == '-' || ch == '.' || ch == 'e' || ch == 'E')
            {
                reader.next();
                sb.append(ch);
            }
            else
                break;
        }

        String number = sb.toString();
        if(number.contains(".") || number.contains("e") || number.contains("E"))
            return Double.valueOf(number);
        else
        {
            long longNumber = Long.parseLong(number);
            if(longNumber > Integer.MIN_VALUE && longNumber < Integer.MAX_VALUE)
                return (int) longNumber;
            else
                return longNumber;
        }
    }

    boolean readBoolean()
    {
        char ch = reader.next();
        if(ch == 't' && reader.next() == 'r' && reader.next() == 'u' && reader.next() == 'e')
            return true;
        else if(ch == 'f' && reader.next() == 'a' && reader.next() == 'l' && reader.next() == 's' && reader.next() == 'e')
            return false;
        throw new JsonParseException("Unexpected boolean");
    }

    void readNull()
    {
        if(!(reader.next() == 'n' && reader.next() == 'u' && reader.next() == 'l' && reader.next() == 'l'))
            throw new JsonParseException("Unexpected null");
    }
}

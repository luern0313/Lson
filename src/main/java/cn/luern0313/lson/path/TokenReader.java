package cn.luern0313.lson.path;

import cn.luern0313.lson.exception.PathParseException;
import cn.luern0313.lson.util.DataProcessUtil;
import cn.luern0313.lson.util.CharReader;

/**
 * 被 luern0313 创建于 2020/8/9.
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
        char ch;
        if(!reader.hasMore())
            return TokenType.END_DOCUMENT;
        /*while (true)
        {
            /*if(!isWhiteSpace(ch))
            {
                break;
            }
            reader.next(); // skip white space
        }*/
        ch = reader.peek();
        switch (ch)
        {
            case '$':
                reader.next(); // skip
                return TokenType.JSON_ROOT;
            case '@':
                reader.next(); // skip
                return TokenType.JSON_CURRENT;
            case '.':
                reader.next(); // skip
                return TokenType.SPLIT_POINT;
            case ':':
                reader.next(); // skip
                return TokenType.SPLIT_COLON;
            case ',':
                reader.next(); // skip
                return TokenType.SPLIT_COMMA;
            case '[':
                reader.next(); // skip
                return TokenType.EXPRESSION_START;
            case ']':
                reader.next(); // skip
                return TokenType.EXPRESSION_END;
            case '*':
                reader.next();
                return TokenType.SYNTAX_ASTERISK;
            case '?':
                reader.next();
                if(reader.peek() == '(')
                {
                    reader.next();
                    return TokenType.FILTER_START;
                }
            case ')':
                reader.next();
                return TokenType.FILTER_END;
            case '-':
                return TokenType.NUMBER;
        }
        Character[] comparison = new Character[]{'=', '<', '>', '!'};
        if(ch >= '0' && ch <= '9')
            return TokenType.NUMBER;
        else if(DataProcessUtil.getIndex(ch, comparison) > -1)
            return TokenType.FILTER_COMPARISON;
        else
            return TokenType.STRING;
    }

    String readString(boolean isRemoveQuotationMarks)
    {
        Character[] safeChar = new Character[]{'.', ',', ':', '[', ']', '(', ')', '*', '-', '<', '>', '?', '@', '$', '~', '=', '!', '/'};
        StringBuilder sb = new StringBuilder(16);
        char ch;
        while (reader.hasMore())
        {
            ch = reader.peek();
            if(DataProcessUtil.getIndex(ch, safeChar) > -1)
                break;
            else
            {
                reader.next();
                sb.append(ch);
            }
        }

        if(isRemoveQuotationMarks)
        {
            if((sb.charAt(0) == '\'' && sb.charAt(sb.length() - 1) == '\'') || (sb.charAt(0) == '"' && sb.charAt(sb.length() - 1) == '"'))
            {
                sb.deleteCharAt(0);
                sb.deleteCharAt(sb.length() - 1);
            }
        }
        return sb.toString();
    }

    PathType.PathFilter.FilterComparator readComparator()
    {
        char ch = reader.next();
        switch (ch)
        {
            case '<':
                if(reader.peek() == '=')
                {
                    reader.next();
                    return PathType.PathFilter.FilterComparator.LESS_EQUAL;
                }
                return PathType.PathFilter.FilterComparator.LESS;
            case '>':
                if(reader.peek() == '=')
                {
                    reader.next();
                    return PathType.PathFilter.FilterComparator.GREATER_EQUAL;
                }
                return PathType.PathFilter.FilterComparator.GREATER;
            case '=':
                if(reader.peek() == '=')
                {
                    reader.next();
                    return PathType.PathFilter.FilterComparator.EQUAL;
                }
            case '!':
                if(reader.peek() == '=')
                {
                    reader.next();
                    return PathType.PathFilter.FilterComparator.NOT_EQUAL;
                }
        }
        throw new PathParseException("Unexpected char: " + reader.next(), reader.readed, null);
    }

    Number readNumber()
    {
        StringBuilder sb = new StringBuilder();
        char ch;

        while (reader.hasMore())
        {
            ch = reader.peek();
            if((ch >= '0' && ch <= '9') || ch == '-' || ch == '.')
            {
                reader.next();
                sb.append(ch);
            }
            else
                break;
        }

        String number = sb.toString();
        if(number.contains("."))
            return Double.valueOf(number);
        else
        {
            long longNumber = Long.parseLong(number);
            if(longNumber > Integer.MIN_VALUE && longNumber < Integer.MAX_VALUE)
                return (int) longNumber;
            else return longNumber;
        }
    }
}

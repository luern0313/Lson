package cn.luern0313.lson.path;

import cn.luern0313.lson.exception.PathParseException;
import cn.luern0313.lson.util.DataProcessUtil;

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
        throw new PathParseException("Unexpected char: " + reader.next(), reader.readed);
    }

    static final int READ_NUMBER_INT_PART = 0;
    static final int READ_NUMBER_END = 1;

    Number readNumber()
    {
        StringBuilder intPart = null;
        char ch = reader.peek();
        boolean minusSign = ch == '-';
        if(minusSign)
            reader.next();
        int status = READ_NUMBER_INT_PART;
        while (true)
        {
            if(reader.hasMore())
                ch = reader.peek();
            else
                status = READ_NUMBER_END;
            switch (status)
            {
                case READ_NUMBER_INT_PART:
                    if(ch >= '0' && ch <= '9')
                    {
                        if(intPart == null)
                            intPart = new StringBuilder(10);
                        intPart.append(reader.next());
                    }
                    else
                    {
                        if(intPart == null)
                            throw new PathParseException("Unexpected char: " + reader.next(), reader.readed);
                        status = READ_NUMBER_END;
                    }
                    continue;
                case READ_NUMBER_END:
                    int readed = reader.readed;
                    if(intPart == null)
                        throw new PathParseException("Missing integer part of number.", readed);
                    long lInt = minusSign ? -string2Long(intPart, readed) : string2Long(intPart, readed);
                    if((double) lInt > MAX_SAFE_DOUBLE)
                        throw new NumberFormatException("Exceeded maximum value: 1.7976931348623157e+308");
                    return (double) lInt;
            }
        }
    }

    long string2Long(CharSequence cs, int readed)
    {
        if(cs.length() > 16)
            throw new PathParseException("Number string is too long.", readed);
        long n = 0;
        for (int i = 0; i < cs.length(); i++)
        {
            n = n * 10 + (cs.charAt(i) - '0');
            if(n > MAX_SAFE_INTEGER)
                throw new PathParseException("Exceeded maximum value: " + MAX_SAFE_INTEGER, readed);
        }
        return n;
    }

    static final long MAX_SAFE_INTEGER = 9007199254740991L;
    static final double MAX_SAFE_DOUBLE = 1.7976931348623157e+308;
}

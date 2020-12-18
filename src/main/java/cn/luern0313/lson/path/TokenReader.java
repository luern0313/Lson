package cn.luern0313.lson.path;

import cn.luern0313.lson.exception.PathParseException;
import cn.luern0313.lson.util.DataProcessUtil;
import cn.luern0313.lson.util.CharReaderUtil;

/**
 * 被 luern0313 创建于 2020/8/9.
 */

class TokenReader
{
    CharReaderUtil reader;

    TokenReader(CharReaderUtil reader)
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
        while (true)
        {
            if(reader.hasMore())
            {
                ch = reader.peek();
                if(isWhiteSpace(ch))
                    reader.next();
                else
                    break;
            }
            else
                return TokenType.END_DOCUMENT;
        }
        switch (ch)
        {
            case '$':
                reader.next();
                return TokenType.JSON_ROOT;
            case '@':
                reader.next();
                return TokenType.JSON_CURRENT;
            case '.':
                reader.next();
                return TokenType.SPLIT_POINT;
            case ':':
                reader.next();
                return TokenType.SPLIT_COLON;
            case ',':
                reader.next();
                return TokenType.SPLIT_COMMA;
            case '[':
                reader.next();
                return TokenType.EXPRESSION_START;
            case ']':
                reader.next();
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

    String readString(boolean isExpressionPath, boolean isDeleteQuotationMarks)
    {
        return readStringBuilder(isExpressionPath, isDeleteQuotationMarks).toString();
    }

    StringBuilder readStringBuilder(boolean isExpressionPath, boolean isDeleteQuotationMarks)
    {
        Character[] expressionStopChar = new Character[]{']', ','};
        Character[] pathStopChar = new Character[]{'.', ',', ':', '[', ']', '(', ')', '*', '-', '<', '>', '?', '@', '$', '~', '=', '!', '/', ' '};
        StringBuilder sb = new StringBuilder(16);
        boolean isQuotationMarks = false;
        while (reader.hasMore())
        {
            char ch = reader.peek();
            if((!isExpressionPath && DataProcessUtil.getIndex(ch, pathStopChar) > -1) || (isExpressionPath && isQuotationMarks && DataProcessUtil.getIndex(ch, expressionStopChar) > -1))
                break;
            else
            {
                if(isExpressionPath && (ch == '\'' || ch == '"'))
                    isQuotationMarks = true;
                reader.next();
                sb.append(ch);
            }
        }

        if(isDeleteQuotationMarks)
            deleteQuotationMarks(sb);
        return sb;
    }

    static boolean isHasQuotationMarks(StringBuilder sb)
    {
        return (sb.charAt(0) == '\'' && sb.charAt(sb.length() - 1) == '\'') || (sb.charAt(0) == '"' && sb.charAt(sb.length() - 1) == '"');
    }

    static void deleteQuotationMarks(StringBuilder sb)
    {
        if(isHasQuotationMarks(sb))
        {
            sb.deleteCharAt(0);
            sb.deleteCharAt(sb.length() - 1);
        }
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
                break;
            case '!':
                if(reader.peek() == '=')
                {
                    reader.next();
                    return PathType.PathFilter.FilterComparator.NOT_EQUAL;
                }
                break;
            case 'i':
                if(reader.peek() == 'n')
                {
                    reader.next();
                    return PathType.PathFilter.FilterComparator.IN;
                }
                break;
            case 'n':
                if(reader.next(2).equals("in"))
                    return PathType.PathFilter.FilterComparator.NOT_IN;
        }
        throw new PathParseException("Unexpected char: " + reader.next(), reader.getErrorMessage());
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

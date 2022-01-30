package cn.luern0313.lson.json;

import cn.luern0313.lson.exception.JsonParseException;
import cn.luern0313.lson.util.CharReaderUtil;

/**
 * 被 luern0313 创建于 2020/8/22.
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
            if(!reader.hasMore())
                return TokenType.END_DOCUMENT;
            if(isWhiteSpace(ch = reader.peek()))
                reader.next();
            else
                break;
        }

        for (TokenType value : TokenType.values()) {
            if (parserSymbol(value.getSymbol(), ch)) {
                return value;
            }
        }

        if(ch >= '0' && ch <= '9')
            return TokenType.NUMBER;
        return TokenType.STRING;
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
                ch = reader.next();
                switch (ch)
                {
                    case '\"':
                        sb.append('\"');
                        break;
                    case '\\':
                        sb.append('\\');
                        break;
                    case '/':
                        sb.append('/');
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'u':
                        int u = 0;
                        for (int i = 0; i < 4; i++)
                        {
                            char uch = reader.next();
                            if(uch >= '0' && uch <= '9')
                                u = (u << 4) + (uch - '0');
                            else if(uch >= 'a' && uch <= 'f')
                                u = (u << 4) + (uch - 'a') + 10;
                            else if(uch >= 'A' && uch <= 'F')
                                u = (u << 4) + (uch - 'A') + 10;
                            else
                                throw new JsonParseException("Unexpected char: " + uch, reader.getErrorMessage());
                        }
                        sb.append((char) u);
                        break;
                    default:
                        throw new JsonParseException("Unexpected char: " + ch, reader.getErrorMessage());
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
            if((ch >= '0' && ch <= '9') || ch == '-' || ch == '.' || ch == 'e' || ch == 'E' || ch == '+')
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

    private boolean parserSymbol(String symbol) {
        if (symbol == null)
            return false;
        return parserSymbol(symbol.toCharArray(), 0);
    }

    private boolean parserSymbol(String symbol, char currentChar) {
        if (symbol == null)
            return false;
        reader.next();
        boolean result = parserSymbol(symbol.toCharArray(), 0, currentChar);
        if (!result) {
            reader.pos--;
            return false;
        }
        return true;
    }

    private boolean parserSymbol(char[] symbolChars, int index) {
        boolean result = parserSymbol(symbolChars, index, reader.next());
        if (!result) {
            reader.pos--;
            return false;
        }
        return true;
    }

    private boolean parserSymbol(char[] symbolChars, int index, char currentChar) {
        if (currentChar == symbolChars[index]) {
            if (symbolChars.length > index + 1) {
                boolean result = parserSymbol(symbolChars, index + 1);
                if (!result) {
                    reader.pos--;
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}

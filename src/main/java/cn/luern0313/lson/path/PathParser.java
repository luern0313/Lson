package cn.luern0313.lson.path;


import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import static cn.luern0313.lson.path.Status.STATUS_EXPECT_COLON;
import static cn.luern0313.lson.path.Status.STATUS_EXPECT_COMMA;
import static cn.luern0313.lson.path.Status.STATUS_EXPECT_END_DOCUMENT;
import static cn.luern0313.lson.path.Status.STATUS_EXPECT_EXPRESSION_END;
import static cn.luern0313.lson.path.Status.STATUS_EXPECT_EXPRESSION_START;
import static cn.luern0313.lson.path.Status.STATUS_EXPECT_JSON_ROOT;
import static cn.luern0313.lson.path.Status.STATUS_EXPECT_NUMBER;
import static cn.luern0313.lson.path.Status.STATUS_EXPECT_PATH;
import static cn.luern0313.lson.path.Status.STATUS_EXPECT_POINT;

/**
 * 被 luern0313 创建于 2020/8/8.
 */

public class PathParser
{
    final TokenReader reader;

    public PathParser(Reader reader)
    {
        this.reader = new TokenReader(new CharReader(reader));
    }

    static boolean hasStatus(int status, int expectedStatus)
    {
        return ((status & expectedStatus) > 0);
    }

    public static ArrayList<Object> parse(String path)
    {
        return parse(new StringReader(path));
    }

    public static ArrayList<Object> parse(Reader r)
    {
        TokenReader reader = new TokenReader(new CharReader(r));
        ArrayList<Object> pathArrayList = new ArrayList<>();
        Stack stack = new Stack();
        int status = STATUS_EXPECT_JSON_ROOT.index | STATUS_EXPECT_EXPRESSION_START.index | STATUS_EXPECT_PATH.index;
        while (true)
        {
            TokenType currentToken = reader.readNextToken();
            switch (currentToken)
            {
                case JSON_ROOT:
                    if(hasStatus(status, STATUS_EXPECT_JSON_ROOT.index))
                    {
                        pathArrayList.add(new PathType.PathJsonRoot());
                        status = STATUS_EXPECT_POINT.index | STATUS_EXPECT_EXPRESSION_START.index | STATUS_EXPECT_END_DOCUMENT.index;
                        continue;
                    }
                    throw new PathParseException("Unexpected $", reader.reader.readed);
                case SPLIT_POINT:
                    if(hasStatus(status, STATUS_EXPECT_POINT.index))
                    {
                        status = STATUS_EXPECT_PATH.index;
                        continue;
                    }
                    throw new PathParseException("Unexpected .", reader.reader.readed);
                case STRING:
                    if(hasStatus(status, STATUS_EXPECT_PATH.index))
                    {
                        String path = reader.readString();
                        pathArrayList.add(new PathType.PathPath(path));
                        status = STATUS_EXPECT_POINT.index | STATUS_EXPECT_EXPRESSION_START.index | STATUS_EXPECT_END_DOCUMENT.index;
                        continue;
                    }
                    throw new PathParseException("Unexpected String", reader.reader.readed);
                case EXPRESSION_START:
                    if(hasStatus(status, STATUS_EXPECT_EXPRESSION_START.index))
                    {
                        stack.push(StackValue.newExpression());
                        status = STATUS_EXPECT_NUMBER.index | STATUS_EXPECT_COLON.index;
                        continue;
                    }
                    throw new PathParseException("Unexpected [", reader.reader.readed);
                case NUMBER:
                    if(hasStatus(status, STATUS_EXPECT_NUMBER.index))
                    {
                        Number number = reader.readNumber();
                        stack.peek(StackValue.TYPE_EXPRESSION).valueAsExpression().index.add(number.intValue());
                        stack.peek(StackValue.TYPE_EXPRESSION).valueAsExpression().isJustColon = false;
                        status = STATUS_EXPECT_COLON.index | STATUS_EXPECT_COMMA.index | STATUS_EXPECT_EXPRESSION_END.index;
                        continue;
                    }
                    throw new PathParseException("Unexpected number", reader.reader.readed);
                case SPLIT_COLON: // :
                    if(hasStatus(status, STATUS_EXPECT_COLON.index))
                    {
                        stack.peek(StackValue.TYPE_EXPRESSION).valueAsExpression().isIndex = true;
                        if(stack.peek(StackValue.TYPE_EXPRESSION).valueAsExpression().isJustColon)
                            stack.peek(StackValue.TYPE_EXPRESSION).valueAsExpression().index.add(Integer.MIN_VALUE);
                        else
                            stack.peek(StackValue.TYPE_EXPRESSION).valueAsExpression().isJustColon = true;
                        status = STATUS_EXPECT_NUMBER.index | STATUS_EXPECT_EXPRESSION_END.index | STATUS_EXPECT_COLON.index;
                        continue;
                    }
                    throw new PathParseException("Unexpected :", reader.reader.readed);
                case SPLIT_COMMA: // ,
                    if(hasStatus(status, STATUS_EXPECT_COMMA.index))
                    {
                        if(!stack.peek(StackValue.TYPE_EXPRESSION).valueAsExpression().isIndex)
                        {
                            status = STATUS_EXPECT_NUMBER.index;
                            continue;
                        }
                    }
                    throw new PathParseException("Unexpected ,", reader.reader.readed);
                case EXPRESSION_END:
                    if(hasStatus(status, STATUS_EXPECT_EXPRESSION_END.index))
                    {
                        StackValue.Expression expression = stack.pop(StackValue.TYPE_EXPRESSION).valueAsExpression();
                        status = STATUS_EXPECT_POINT.index | STATUS_EXPECT_EXPRESSION_START.index | STATUS_EXPECT_END_DOCUMENT.index;
                        if(expression.isIndex)
                        {
                            switch (expression.index.size())
                            {
                                case 3:
                                    pathArrayList.add(new PathType.PathIndex(expression.index.get(0), expression.index.get(1), expression.index.get(2)));
                                    continue;
                                case 2:
                                    pathArrayList.add(new PathType.PathIndex(expression.index.get(0), expression.index.get(1)));
                                    continue;
                                case 1:
                                    pathArrayList.add(new PathType.PathIndex(expression.index.get(0)));
                                    continue;
                            }
                        }
                        else
                        {
                            pathArrayList.add(new PathType.PathIndexArray(expression.index));
                            continue;
                        }
                    }
                    throw new PathParseException("Unexpected ]", reader.reader.readed);
                case END_DOCUMENT:
                    if(hasStatus(status, STATUS_EXPECT_END_DOCUMENT.index))
                    {
                        if(stack.pos == 0)
                            return pathArrayList;
                    }
                    throw new PathParseException("Unexpected EOF.", reader.reader.readed);
            }
        }
    }
}

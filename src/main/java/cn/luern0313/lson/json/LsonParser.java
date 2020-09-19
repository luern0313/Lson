package cn.luern0313.lson.json;

import java.io.Reader;
import java.io.StringReader;

import cn.luern0313.lson.element.LsonArray;
import cn.luern0313.lson.element.LsonElement;
import cn.luern0313.lson.element.LsonNull;
import cn.luern0313.lson.element.LsonObject;
import cn.luern0313.lson.element.LsonPrimitive;
import cn.luern0313.lson.exception.JsonParseException;
import cn.luern0313.lson.exception.PathParseException;

import static cn.luern0313.lson.json.Status.*;

/**
 * JSON解析相关类。
 *
 * @author luern0313
 */

public class LsonParser
{
    final TokenReader reader;

    public LsonParser(Reader reader)
    {
        this.reader = new TokenReader(new CharReader(reader));
    }

    static boolean hasStatus(int status, int expectedStatus)
    {
        return ((status & expectedStatus) > 0);
    }

    public static LsonElement parse(String json)
    {
        return parse(new StringReader(json));
    }

    public static LsonObject parseAsObject(String json)
    {
        return parse(new StringReader(json)).getAsLsonObject();
    }

    public static LsonArray parseAsArray(String json)
    {
        return parse(new StringReader(json)).getAsLsonArray();
    }

    private static LsonElement parse(Reader r)
    {
        TokenReader reader = new TokenReader(new CharReader(r));
        Stack stack = new Stack();
        int status = STATUS_EXPECT_OBJECT_BEGIN.index | STATUS_EXPECT_ARRAY_BEGIN.index | STATUS_EXPECT_SINGLE_ELEMENT.index;
        while (true)
        {
            TokenType currentToken = reader.readNextToken();
            switch (currentToken)
            {
                case OBJECT_BEGIN:
                {
                    if(hasStatus(status, STATUS_EXPECT_OBJECT_BEGIN.index))
                    {
                        stack.push(StackValue.newJsonObject());
                        status = STATUS_EXPECT_OBJECT_KEY.index | STATUS_EXPECT_OBJECT_END.index;
                        continue;
                    }
                    throw new PathParseException("Unexpected {", reader.reader.readed);
                }
                case ARRAY_BEGIN:
                {
                    if(hasStatus(status, STATUS_EXPECT_ARRAY_BEGIN.index))
                    {
                        stack.push(StackValue.newJsonArray());
                        status = STATUS_EXPECT_ARRAY_ELEMENT.index | STATUS_EXPECT_OBJECT_BEGIN.index | STATUS_EXPECT_ARRAY_BEGIN.index | STATUS_EXPECT_ARRAY_END.index;
                        continue;
                    }
                    throw new PathParseException("Unexpected [", reader.reader.readed);
                }
                case STRING:
                {
                    String string = reader.readString();
                    LsonPrimitive lsonPrimitive = new LsonPrimitive(string);
                    if(hasStatus(status, STATUS_EXPECT_OBJECT_KEY.index))
                    {
                        stack.push(StackValue.newJsonObjectKey(string));
                        status = STATUS_EXPECT_COLON.index;
                        continue;
                    }
                    else if(hasStatus(status, STATUS_EXPECT_OBJECT_VALUE.index))
                    {
                        String key = stack.pop(StackValue.TYPE_OBJECT_KEY).getAsJsonObjectKeyValue().key;
                        stack.peek(StackValue.TYPE_OBJECT).getAsJsonObjectValue().put(key, lsonPrimitive);
                        status = STATUS_EXPECT_COMMA.index | STATUS_EXPECT_OBJECT_END.index;
                        continue;
                    }
                    else if(hasStatus(status, STATUS_EXPECT_ARRAY_ELEMENT.index))
                    {
                        stack.peek(StackValue.TYPE_ARRAY).getAsJsonArrayValue().add(lsonPrimitive);
                        status = STATUS_EXPECT_COMMA.index | STATUS_EXPECT_ARRAY_END.index;
                        continue;
                    }
                    else if(hasStatus(status, STATUS_EXPECT_SINGLE_ELEMENT.index))
                    {
                        stack.push(StackValue.newJsonSingle(lsonPrimitive));
                        status = STATUS_EXPECT_END_DOCUMENT.index;
                        continue;
                    }
                    throw new PathParseException("Unexpected string", reader.reader.readed);
                }
                case NUMBER:
                {
                    Number number = reader.readNumber();
                    LsonPrimitive lsonPrimitive = new LsonPrimitive(number);
                    if(hasStatus(status, STATUS_EXPECT_OBJECT_VALUE.index))
                    {
                        String key = stack.pop(StackValue.TYPE_OBJECT_KEY).getAsJsonObjectKeyValue().key;
                        stack.peek(StackValue.TYPE_OBJECT).getAsJsonObjectValue().put(key, lsonPrimitive);
                        status = STATUS_EXPECT_COMMA.index | STATUS_EXPECT_OBJECT_END.index;
                        continue;
                    }
                    else if(hasStatus(status, STATUS_EXPECT_ARRAY_ELEMENT.index))
                    {
                        stack.peek(StackValue.TYPE_ARRAY).getAsJsonArrayValue().add(lsonPrimitive);
                        status = STATUS_EXPECT_COMMA.index | STATUS_EXPECT_ARRAY_END.index;
                        continue;
                    }
                    else if(hasStatus(status, STATUS_EXPECT_SINGLE_ELEMENT.index))
                    {
                        stack.push(StackValue.newJsonSingle(lsonPrimitive));
                        status = STATUS_EXPECT_END_DOCUMENT.index;
                        continue;
                    }
                    throw new PathParseException("Unexpected number", reader.reader.readed);
                }
                case BOOLEAN:
                {
                    boolean bool = reader.readBoolean();
                    LsonPrimitive lsonPrimitive = new LsonPrimitive(bool);
                    if(hasStatus(status, STATUS_EXPECT_OBJECT_VALUE.index))
                    {
                        String key = stack.pop(StackValue.TYPE_OBJECT_KEY).getAsJsonObjectKeyValue().key;
                        stack.peek(StackValue.TYPE_OBJECT).getAsJsonObjectValue().put(key, lsonPrimitive);
                        status = STATUS_EXPECT_COMMA.index | STATUS_EXPECT_OBJECT_END.index;
                        continue;
                    }
                    else if(hasStatus(status, STATUS_EXPECT_ARRAY_ELEMENT.index))
                    {
                        stack.peek(StackValue.TYPE_ARRAY).getAsJsonArrayValue().add(lsonPrimitive);
                        status = STATUS_EXPECT_COMMA.index | STATUS_EXPECT_ARRAY_END.index;
                        continue;
                    }
                    else if(hasStatus(status, STATUS_EXPECT_SINGLE_ELEMENT.index))
                    {
                        stack.push(StackValue.newJsonSingle(lsonPrimitive));
                        status = STATUS_EXPECT_END_DOCUMENT.index;
                        continue;
                    }
                    throw new PathParseException("Unexpected boolean", reader.reader.readed);
                }
                case NULL:
                {
                    reader.readNull();
                    if(hasStatus(status, STATUS_EXPECT_OBJECT_VALUE.index))
                    {
                        String key = stack.pop(StackValue.TYPE_OBJECT_KEY).getAsJsonObjectKeyValue().key;
                        stack.peek(StackValue.TYPE_OBJECT).getAsJsonObjectValue().put(key, null);
                        status = STATUS_EXPECT_COMMA.index | STATUS_EXPECT_OBJECT_END.index;
                        continue;
                    }
                    else if(hasStatus(status, STATUS_EXPECT_ARRAY_ELEMENT.index))
                    {
                        stack.peek(StackValue.TYPE_ARRAY).getAsJsonArrayValue().add(null);
                        status = STATUS_EXPECT_COMMA.index | STATUS_EXPECT_ARRAY_END.index;
                        continue;
                    }
                    else if(hasStatus(status, STATUS_EXPECT_SINGLE_ELEMENT.index))
                    {
                        stack.push(StackValue.newJsonSingle(null));
                        status = STATUS_EXPECT_END_DOCUMENT.index;
                        continue;
                    }
                    throw new PathParseException("Unexpected null", reader.reader.readed);
                }
                case SPLIT_COMMA: // ,
                {
                    if(hasStatus(status, STATUS_EXPECT_COMMA.index))
                    {
                        if (hasStatus(status, STATUS_EXPECT_OBJECT_END.index))
                        {
                            status = STATUS_EXPECT_OBJECT_KEY.index;
                            continue;
                        }
                        if (hasStatus(status, STATUS_EXPECT_ARRAY_END.index))
                        {
                            status = STATUS_EXPECT_ARRAY_ELEMENT.index | STATUS_EXPECT_ARRAY_BEGIN.index | STATUS_EXPECT_OBJECT_BEGIN.index;
                            continue;
                        }
                    }
                    throw new PathParseException("Unexpected ,", reader.reader.readed);
                }
                case SPLIT_COLON: // :
                    if (hasStatus(status, STATUS_EXPECT_COLON.index))
                    {
                        status = STATUS_EXPECT_OBJECT_VALUE.index | STATUS_EXPECT_OBJECT_BEGIN.index | STATUS_EXPECT_ARRAY_BEGIN.index;
                        continue;
                    }
                    throw new PathParseException("Unexpected :", reader.reader.readed);
                case ARRAY_END:
                    if(hasStatus(status, STATUS_EXPECT_ARRAY_END.index))
                    {
                        if(stack.pos == 1)
                        {
                            status = STATUS_EXPECT_END_DOCUMENT.index;
                            continue;
                        }
                        else
                        {
                            StackValue array = stack.pop(StackValue.TYPE_ARRAY);
                            if(stack.getTopValueType() == StackValue.TYPE_OBJECT_KEY)
                            {
                                String key = stack.pop(StackValue.TYPE_OBJECT_KEY).getAsJsonObjectKeyValue().key;
                                stack.peek(StackValue.TYPE_OBJECT).getAsJsonObjectValue().put(key, array.getAsJsonArrayValue().lsonArray);
                                status = STATUS_EXPECT_COMMA.index | STATUS_EXPECT_OBJECT_END.index;
                                continue;
                            }
                            if(stack.getTopValueType() == StackValue.TYPE_ARRAY)
                            {
                                stack.peek(StackValue.TYPE_ARRAY).getAsJsonArrayValue().add(array.getAsJsonArrayValue().lsonArray);
                                status = STATUS_EXPECT_COMMA.index | STATUS_EXPECT_ARRAY_END.index;
                                continue;
                            }
                        }
                    }
                    throw new PathParseException("Unexpected ]", reader.reader.readed);
                case OBJECT_END:
                    if (hasStatus(status, STATUS_EXPECT_OBJECT_END.index))
                    {
                        if(stack.pos == 1)
                        {
                            status = STATUS_EXPECT_END_DOCUMENT.index;
                            continue;
                        }
                        else
                        {
                            StackValue object = stack.pop(StackValue.TYPE_OBJECT);
                            if (stack.getTopValueType() == StackValue.TYPE_OBJECT_KEY)
                            {
                                String key = stack.pop(StackValue.TYPE_OBJECT_KEY).getAsJsonObjectKeyValue().key;
                                stack.peek(StackValue.TYPE_OBJECT).getAsJsonObjectValue().put(key, object.getAsJsonObjectValue().lsonObject);
                                status = STATUS_EXPECT_COMMA.index | STATUS_EXPECT_OBJECT_END.index;
                                continue;
                            }
                            if (stack.getTopValueType() == StackValue.TYPE_ARRAY)
                            {
                                stack.peek(StackValue.TYPE_ARRAY).getAsJsonArrayValue().add(object.getAsJsonObjectValue().lsonObject);
                                status = STATUS_EXPECT_COMMA.index | STATUS_EXPECT_ARRAY_END.index;
                                continue;
                            }
                        }
                    }
                    throw new JsonParseException("Unexpected }.", reader.reader.readed);
                case END_DOCUMENT:
                    if(hasStatus(status, STATUS_EXPECT_END_DOCUMENT.index))
                    {
                        if(stack.getTopValueType() == StackValue.TYPE_OBJECT)
                            return stack.pop().getAsJsonObjectValue().lsonObject;
                        else if(stack.getTopValueType() == StackValue.TYPE_ARRAY)
                            return stack.pop().getAsJsonArrayValue().lsonArray;
                        else if(stack.getTopValueType() == StackValue.TYPE_SINGLE)
                            return stack.pop().getAsJsonSingleValue().value;
                        else
                            return LsonNull.getJsonNull();
                    }
                    throw new PathParseException("Unexpected EOF.", reader.reader.readed);
            }
        }
    }
}

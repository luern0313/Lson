package cn.luern0313.lson.json;

import java.io.Reader;

import cn.luern0313.lson.element.LsonElement;
import cn.luern0313.lson.element.LsonNull;
import cn.luern0313.lson.element.LsonPrimitive;
import cn.luern0313.lson.exception.JsonParseException;
import cn.luern0313.lson.util.CharReaderUtil;

import static cn.luern0313.lson.json.Status.STATUS_EXPECT_ARRAY_BEGIN;
import static cn.luern0313.lson.json.Status.STATUS_EXPECT_ARRAY_ELEMENT;
import static cn.luern0313.lson.json.Status.STATUS_EXPECT_ARRAY_END;
import static cn.luern0313.lson.json.Status.STATUS_EXPECT_COLON;
import static cn.luern0313.lson.json.Status.STATUS_EXPECT_COMMA;
import static cn.luern0313.lson.json.Status.STATUS_EXPECT_END_DOCUMENT;
import static cn.luern0313.lson.json.Status.STATUS_EXPECT_OBJECT_BEGIN;
import static cn.luern0313.lson.json.Status.STATUS_EXPECT_OBJECT_END;
import static cn.luern0313.lson.json.Status.STATUS_EXPECT_OBJECT_KEY;
import static cn.luern0313.lson.json.Status.STATUS_EXPECT_OBJECT_VALUE;
import static cn.luern0313.lson.json.Status.STATUS_EXPECT_SINGLE_ELEMENT;

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
        this.reader = new TokenReader(new CharReaderUtil(reader));
    }

    static boolean hasStatus(int status, int expectedStatus)
    {
        return ((status & expectedStatus) > 0);
    }

    public static LsonElement parse(Reader r)
    {
        TokenReader reader = new TokenReader(new CharReaderUtil(r));
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
                    throw new JsonParseException("Unexpected {", reader.reader.getErrorMessage());
                }
                case ARRAY_BEGIN:
                {
                    if(hasStatus(status, STATUS_EXPECT_ARRAY_BEGIN.index))
                    {
                        stack.push(StackValue.newJsonArray());
                        status = STATUS_EXPECT_ARRAY_ELEMENT.index | STATUS_EXPECT_OBJECT_BEGIN.index | STATUS_EXPECT_ARRAY_BEGIN.index | STATUS_EXPECT_ARRAY_END.index;
                        continue;
                    }
                    throw new JsonParseException("Unexpected [", reader.reader.getErrorMessage());
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
                    throw new JsonParseException("Unexpected string", reader.reader.getErrorMessage());
                }
                case STRING_WITHOUT_QUOTATION:
                {
                    String string = reader.readString();
                    if(hasStatus(status, STATUS_EXPECT_OBJECT_KEY.index))
                    {
                        stack.push(StackValue.newJsonObjectKey(string));
                        status = STATUS_EXPECT_COLON.index;
                        continue;
                    }
                    throw new JsonParseException("Unexpected string-without-quotation", reader.reader.getErrorMessage());
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
                    throw new JsonParseException("Unexpected number", reader.reader.getErrorMessage());
                }
                case BOOLEAN_TRUE: {
                    LsonPrimitive lsonPrimitive = new LsonPrimitive(true);
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
                    throw new JsonParseException("Unexpected true", reader.reader.getErrorMessage());
                }
                case BOOLEAN_FALSE: {
                    LsonPrimitive lsonPrimitive = new LsonPrimitive(false);
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
                    throw new JsonParseException("Unexpected false", reader.reader.getErrorMessage());
                }
                case NULL:
                {
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
                    throw new JsonParseException("Unexpected null", reader.reader.getErrorMessage());
                }
                case SPLIT_COMMA: // ,
                {
                    if(hasStatus(status, STATUS_EXPECT_COMMA.index))
                    {
                        if (hasStatus(status, STATUS_EXPECT_OBJECT_END.index))
                        {
                            status = STATUS_EXPECT_OBJECT_KEY.index | STATUS_EXPECT_OBJECT_END.index;
                            continue;
                        }
                        if (hasStatus(status, STATUS_EXPECT_ARRAY_END.index))
                        {
                            status = STATUS_EXPECT_ARRAY_ELEMENT.index | STATUS_EXPECT_ARRAY_BEGIN.index | STATUS_EXPECT_OBJECT_BEGIN.index | STATUS_EXPECT_ARRAY_END.index;
                            continue;
                        }
                    }
                    throw new JsonParseException("Unexpected ,", reader.reader.getErrorMessage());
                }
                case SPLIT_COLON: // :
                    if (hasStatus(status, STATUS_EXPECT_COLON.index))
                    {
                        status = STATUS_EXPECT_OBJECT_VALUE.index | STATUS_EXPECT_OBJECT_BEGIN.index | STATUS_EXPECT_ARRAY_BEGIN.index;
                        continue;
                    }
                    throw new JsonParseException("Unexpected :", reader.reader.getErrorMessage());
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
                    throw new JsonParseException("Unexpected ]", reader.reader.getErrorMessage());
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
                    throw new JsonParseException("Unexpected }", reader.reader.getErrorMessage());
                case COMMENT_SINGLE:
                    reader.readComment("\n");
                    break;
                case COMMENT_MULTIPLE_START:
                    reader.readComment("*/");
                    break;
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
                    throw new JsonParseException("Unexpected EOF.", reader.reader.getErrorMessage());
            }
        }
    }
}

package cn.luern0313.lson.json;

import cn.luern0313.lson.element.LsonArray;
import cn.luern0313.lson.element.LsonElement;
import cn.luern0313.lson.element.LsonObject;
import cn.luern0313.lson.element.LsonPrimitive;

/**
 * 被 luern0313 创建于 2020/8/22.
 */

class StackValue
{
    static final int TYPE_OBJECT = 0;

    static final int TYPE_OBJECT_KEY = 1;

    static final int TYPE_ARRAY = 2;

    static final int TYPE_SINGLE = 3;

    final int type;
    final Object value;

    private StackValue(int type, Object value)
    {
        this.type = type;
        this.value = value;
    }

    JsonObjectKeyValue getAsJsonObjectKeyValue()
    {
        return (JsonObjectKeyValue) value;
    }

    JsonObjectValue getAsJsonObjectValue()
    {
        return (JsonObjectValue) value;
    }

    JsonArrayValue getAsJsonArrayValue()
    {
        return (JsonArrayValue) value;
    }

    JsonSingleValue getAsJsonSingleValue()
    {
        return (JsonSingleValue) value;
    }

    static StackValue newJsonObject()
    {
        return new StackValue(TYPE_OBJECT, new JsonObjectValue());
    }

    static StackValue newJsonObjectKey(String key)
    {
        return new StackValue(TYPE_OBJECT_KEY, new JsonObjectKeyValue(key));
    }

    static StackValue newJsonArray()
    {
        return new StackValue(TYPE_ARRAY, new JsonArrayValue());
    }

    static StackValue newJsonSingle(LsonPrimitive value)
    {
        return new StackValue(TYPE_SINGLE, new JsonSingleValue(value));
    }

    static class JsonObjectKeyValue
    {
        String key;

        public JsonObjectKeyValue(String key)
        {
            this.key = key;
        }
    }

    static class JsonObjectValue
    {
        LsonObject lsonObject = new LsonObject();

        void put(String key, LsonElement value)
        {
            lsonObject.put(key, value);
        }
    }

    static class JsonArrayValue
    {
        LsonArray lsonArray = new LsonArray();

        void add(LsonElement element)
        {
            lsonArray.add(element);
        }
    }

    static class JsonSingleValue
    {
        final LsonPrimitive value;

        public JsonSingleValue(LsonPrimitive value)
        {
            this.value = value;
        }
    }
}

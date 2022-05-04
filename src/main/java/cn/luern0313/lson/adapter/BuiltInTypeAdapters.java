package cn.luern0313.lson.adapter;

import java.util.Date;

import cn.luern0313.lson.element.LsonArray;
import cn.luern0313.lson.element.LsonElement;
import cn.luern0313.lson.element.LsonObject;
import cn.luern0313.lson.element.LsonPrimitive;
import cn.luern0313.lson.util.TypeUtil;

/**
 * 被 luern 创建于 2022/4/30.
 */

public class BuiltInTypeAdapters {
    public static final TypeAdapter<StringBuilder> stringBuilder = new TypeAdapter<StringBuilder>() {
        @Override
        public StringBuilder deserialization(Object value) {
            return new StringBuilder(value.toString());
        }

        @Override
        public LsonElement serialization(StringBuilder object) {
            return new LsonPrimitive(object.toString());
        }
    };

    public static final TypeAdapter<StringBuffer> stringBuffer = new TypeAdapter<StringBuffer>() {
        @Override
        public StringBuffer deserialization(Object value) {
            return new StringBuffer(value.toString());
        }

        @Override
        public LsonElement serialization(StringBuffer object) {
            return new LsonPrimitive(object.toString());
        }
    };

    public static final TypeAdapter<java.util.Date> date = new TypeAdapter<Date>() {
        @Override
        public Date deserialization(Object value) {
            TypeUtil valueType = new TypeUtil(value.getClass());
            if (valueType.isNumber())
                return new java.util.Date(((Number) value).longValue());
            else if (valueType.isString())
                return new java.util.Date(Long.parseLong(value.toString()));
            return null;
        }

        @Override
        public LsonElement serialization(Date object) {
            return new LsonPrimitive(object.getTime());
        }
    };

    public static final TypeAdapter<java.sql.Date> sqlDate = new TypeAdapter<java.sql.Date>() {
        @Override
        public java.sql.Date deserialization(Object value) {
            TypeUtil valueType = new TypeUtil(value.getClass());
            if (valueType.isNumber())
                return new java.sql.Date(((Number) value).longValue());
            else if (valueType.isString())
                return new java.sql.Date(Long.parseLong(value.toString()));
            return null;
        }

        @Override
        public LsonElement serialization(java.sql.Date object) {
            return new LsonPrimitive(object.getTime());
        }
    };

    public static final TypeAdapter<LsonElement> lsonElement = new TypeAdapter<LsonElement>() {
        @Override
        public LsonElement deserialization(Object value) {
            if (value instanceof LsonElement)
                return (LsonElement) value;
            return null;
        }

        @Override
        public LsonElement serialization(LsonElement object) {
            return object;
        }
    };

    public static final TypeAdapter<LsonObject> lsonObject = new TypeAdapter<LsonObject>() {
        @Override
        public LsonObject deserialization(Object value) {
            if (value instanceof LsonElement)
                return ((LsonElement) value).getAsLsonObject();
            return null;
        }

        @Override
        public LsonElement serialization(LsonObject object) {
            return object;
        }
    };

    public static final TypeAdapter<LsonArray> lsonArray = new TypeAdapter<LsonArray>() {
        @Override
        public LsonArray deserialization(Object value) {
            if (value instanceof LsonElement)
                return ((LsonElement) value).getAsLsonArray();
            return null;
        }

        @Override
        public LsonElement serialization(LsonArray object) {
            return object;
        }
    };

    public static final TypeAdapter<LsonPrimitive> lsonPrimitive = new TypeAdapter<LsonPrimitive>() {
        @Override
        public LsonPrimitive deserialization(Object value) {
            if (value instanceof LsonElement)
                return ((LsonElement) value).getAsLsonPrimitive();
            return null;
        }

        @Override
        public LsonElement serialization(LsonPrimitive object) {
            return object;
        }
    };
}

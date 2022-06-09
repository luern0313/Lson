package cn.luern0313.lson;

import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import cn.luern0313.lson.adapter.BuiltInTypeAdapters;
import cn.luern0313.lson.adapter.TypeAdapter;
import cn.luern0313.lson.adapter.TypeAdapterList;
import cn.luern0313.lson.constructor.ClassConstructor;
import cn.luern0313.lson.constructor.CustomConstructor;
import cn.luern0313.lson.constructor.CustomConstructorList;
import cn.luern0313.lson.element.LsonArray;
import cn.luern0313.lson.element.LsonElement;
import cn.luern0313.lson.element.LsonObject;
import cn.luern0313.lson.json.LsonParser;
import cn.luern0313.lson.util.DataProcessUtil;
import cn.luern0313.lson.util.TypeUtil;

/**
 * 被 luern 创建于 2022/5/1.
 */

public class Lson {
    public static Lson instance = new Lson();

    private final TypeAdapterList typeAdapterList;
    private final ClassConstructor classConstructor;

    private Deserialization deserialization;
    private Serialization serialization;

    private Lson() {
        this(null, null);
    }

    private Lson(TypeAdapterList typeAdapterList, CustomConstructorList customConstructorList) {
        this.typeAdapterList = new TypeAdapterList();

        for (Field field : BuiltInTypeAdapters.class.getDeclaredFields()) {
            try {
                if (field.getType() == TypeAdapter.class) {
                    field.setAccessible(true);
                    this.typeAdapterList.add((TypeAdapter<?>) field.get(null));
                }
            } catch (IllegalAccessException ignored) {
            }
        }

        this.typeAdapterList.addAll(typeAdapterList);
        this.classConstructor = new ClassConstructor(customConstructorList);
    }

    /**
     * 获取Lson的默认实例
     *
     * @return Lson的默认实例
     */
    public static Lson def() {
        return instance;
    }

    /**
     * 将一个JSON字符串解析为LsonElement对象
     *
     * @param json 要解析的JSON字符串
     * @return LsonElement对象
     * @author luern0313
     */
    public LsonElement parse(String json) {
        return parse(new StringReader(json));
    }

    /**
     * 将一个JSON字符串解析为LsonElement对象
     *
     * @param reader 要解析的JSON字符串流
     * @return LsonElement对象
     * @author luern0313
     */
    public LsonElement parse(Reader reader) {
        return LsonParser.parse(reader);
    }

    /**
     * 将一个JSON字符串解析为LsonObject对象
     *
     * @param json 要解析的JSON字符串
     * @return LsonObject对象
     * @author luern0313
     */
    public LsonObject parseAsObject(String json) {
        return parse(json).getAsLsonObject();
    }

    /**
     * 将一个JSON字符串解析为LsonArray对象
     *
     * @param json 要解析的JSON字符串
     * @return LsonArray对象
     * @author luern0313
     */
    public LsonArray parseAsArray(String json) {
        return parse(json).getAsLsonArray();
    }

    /**
     * 将json反序列化为指定的实体类
     *
     * @param json           Lson解析过的json对象
     * @param clz            要反序列化实体类的Class对象
     * @param <T>            反序列化为的实体类
     * @return 返回反序列化后的实体类
     * @author luern0313
     */
    public <T> T fromJson(LsonElement json, Class<T> clz, Object... parameter) {
        return fromJson(json, new TypeUtil(clz), parameter);
    }

    /**
     * 将json反序列化为指定的实体类
     *
     * @param json           JSON字符串
     * @param clz            要反序列化实体类的Class对象
     * @param <T>            反序列化为的实体类
     * @return 返回反序列化后的实体类
     * @author luern0313
     */
    public <T> T fromJson(String json, Class<T> clz, Object... parameter) {
        return fromJson(this.parse(json), new TypeUtil(clz), parameter);
    }

    /**
     * 将json反序列化为指定的实体类
     *
     * @param json           Lson解析过的JSON对象
     * @param typeReference  {@link TypeReference}类，用于泛型类的反序列化
     * @param <T>            反序列化为的实体类
     * @return 返回反序列化后的实体类
     * @author luern0313
     */
    public <T> T fromJson(LsonElement json, TypeReference<T> typeReference, Object... parameter) {
        return fromJson(json, new TypeUtil(typeReference.rawType, typeReference), parameter);
    }

    /**
     * 将json反序列化为指定的实体类
     *
     * @param json           JSON字符串
     * @param typeReference  {@link TypeReference}类，用于泛型类的反序列化
     * @param <T>            反序列化为的实体类
     * @return 返回反序列化后的实体类
     * @author luern0313
     */
    public <T> T fromJson(String json, TypeReference<T> typeReference, Object... parameter) {
        return fromJson(this.parse(json), new TypeUtil(typeReference.rawType, typeReference), parameter);
    }

    private <T> T fromJson(LsonElement json, TypeUtil typeUtil, Object[] parameter) {
        if (deserialization == null)
            deserialization = new Deserialization(this);
        classConstructor.setParameter(parameter);
        return deserialization.fromJson(json, typeUtil, null, new ArrayList<>());
    }

    /**
     * 获取json中对应JSONPath的值
     *
     * @param json Lson解析过的json对象
     * @param path JSONPath，用于描述要取到的值在json中的位置
     * @return JSONPath对应的值
     * @author luern0313
     */
    public Object getValue(LsonElement json, String path) {
        return getValue(json, path, Object.class);
    }

    /**
     * 获取json中对应JSONPath的值，并指明该值的类型
     *
     * @param json Lson解析过的json对象
     * @param path JSONPath，用于描述要取到的值在json中的位置
     * @param clz  该值的类型，Lson会尝试将该值转为指定的类型
     * @param <T>  指定的类型
     * @return JSONPath对应的值
     * @author luern0313
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue(LsonElement json, String path, Type clz) {
        if (deserialization == null)
            deserialization = new Deserialization(this);
        TypeUtil typeUtil = new TypeUtil(clz);
        T t = (T) deserialization.finalValueHandle(deserialization.getValue(json, new String[]{path}, new ArrayList<>(), typeUtil, null), typeUtil);
        if (t == null && typeUtil.isPrimitive())
            return (T) PRIMITIVE_DEFAULT_VALUE.get(typeUtil.getName());
        return t;
    }

    /**
     * 将任意类型数据序列化为json
     *
     * @param object 要序列化的数据
     * @return 序列化结果
     * @author luern0313
     */
    public String toJson(Object object) {
        return toJsonElement(object).toString();
    }

    /**
     * 将任意类型数据序列化为LsonElement
     *
     * @param object 要序列化的数据
     * @return 序列化结果
     * @author luern0313
     */
    public LsonElement toJsonElement(Object object) {
        if (serialization == null)
            serialization = new Serialization(this);
        return serialization.toJson(object);
    }

    /**
     * 根据JSONPath将数据填充至LsonElement中
     *
     * @param lsonElement 被填充的LsonElement
     * @param path        标注数据位置的JSONPath
     * @param value       要填充的数据
     * @return 填充完成的LsonElement
     */
    public LsonElement putValue(LsonElement lsonElement, String path, Object value) {
        if (serialization == null)
            serialization = new Serialization(this);
        serialization.setValue(serialization.toJson(value), path, new ArrayList<>(), lsonElement);
        return lsonElement;
    }

    public TypeAdapterList getTypeAdapterList() {
        return typeAdapterList;
    }

    public ClassConstructor getClassConstructor() {
        return classConstructor;
    }

    protected static HashMap<String, Object> PRIMITIVE_DEFAULT_VALUE = new HashMap<String, Object>() {{
        put(int.class.getName(), 0);
        put(byte.class.getName(), (byte) 0);
        put(char.class.getName(), (char) 0);
        put(double.class.getName(), 0d);
        put(float.class.getName(), 0f);
        put(long.class.getName(), 0L);
        put(short.class.getName(), (short) 0);
        put(boolean.class.getName(), false);
    }};

    public static class LsonBuilder {
        private TypeAdapterList typeAdapterList;
        private CustomConstructorList customConstructorList;

        public LsonBuilder setTypeAdapter(TypeAdapter<?> typeAdapter) {
            if (this.typeAdapterList == null)
                this.typeAdapterList = new TypeAdapterList();
            this.typeAdapterList.add(typeAdapter);
            return this;
        }

        public LsonBuilder setCustomConstructor(CustomConstructor<?> customConstructor) {
            if (this.customConstructorList == null)
                this.customConstructorList = new CustomConstructorList();
            this.customConstructorList.add(customConstructor);
            return this;
        }

        public Lson build() {
            return new Lson(typeAdapterList, customConstructorList);
        }
    }
}

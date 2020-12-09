package cn.luern0313.lson;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import cn.luern0313.lson.element.LsonArray;
import cn.luern0313.lson.element.LsonElement;
import cn.luern0313.lson.element.LsonObject;
import cn.luern0313.lson.json.LsonParser;
import cn.luern0313.lson.util.TypeUtil;

/**
 * Lson相关类。
 *
 * @author luern0313
 */

public class LsonUtil
{
    /**
     * 将一个JSON字符串解析为LsonElement对象。
     *
     * @param json 要解析的JSON字符串。
     * @return LsonElement对象。
     *
     * @author luern0313
     */
    public static LsonElement parse(String json)
    {
        return LsonParser.parse(new StringReader(json));
    }

    /**
     * 将一个JSON字符串解析为LsonObject对象。
     *
     * @param json 要解析的JSON字符串。
     * @return LsonObject对象。
     *
     * @author luern0313
     */
    public static LsonObject parseAsObject(String json)
    {
        return LsonParser.parse(new StringReader(json)).getAsLsonObject();
    }

    /**
     * 将一个JSON字符串解析为LsonArray对象。
     *
     * @param json 要解析的JSON字符串。
     * @return LsonArray对象。
     *
     * @author luern0313
     */
    public static LsonArray parseAsArray(String json)
    {
        return LsonParser.parse(new StringReader(json)).getAsLsonArray();
    }

    /**
     * 将json反序列化为指定的实体类。
     *
     * @param json Lson解析过的json对象。
     * @param clz 要反序列化实体类的Class对象。
     * @param parameters 实例化类时，构造函数需要的参数。
     * @param <T> 反序列化为的实体类。
     * @return 返回反序列化后的实体类。
     *
     * @author luern0313
     */
    public static <T> T fromJson(LsonElement json, Class<T> clz, Object... parameters)
    {
        return Deserialization.fromJson(json, new TypeUtil(clz), new ArrayList<>(), null, getParameterTypes(parameters), parameters);
    }

    /**
     * 将json反序列化为指定的实体类。
     *
     * @param json Lson解析过的json对象。
     * @param clz 要反序列化实体类的Class对象。
     * @param parameterTypes 实例化类时，构造函数需要参数的类型。
     * @param parameters 实例化类时，构造函数需要的参数。
     * @param <T> 反序列化为的实体类。
     * @return 返回反序列化后的实体类。
     *
     * @author luern0313
     */
    public static <T> T fromJson(LsonElement json, Class<T> clz, Class<?>[] parameterTypes, Object[] parameters)
    {
        return Deserialization.fromJson(json, new TypeUtil(clz), new ArrayList<>(), null, parameterTypes, parameters);
    }

    /**
     * 将json反序列化到指定的对象。
     *
     * @param json Lson解析过的json对象。
     * @param t 要反序列化的对象。
     * @param <T> 反序列化为的实体类。
     * @return 返回反序列化后的实体类。
     *
     * @author luern0313
     */
    public static <T> T packFromJson(LsonElement json, T t)
    {
        return Deserialization.fromJson(json, t, new ArrayList<>());
    }

    /**
     * 将json反序列化为指定的实体类。
     *
     * @param json Lson解析过的json对象。
     * @param typeReference {@link TypeReference}类，用于泛型类的反序列化。
     * @param parameters 实例化类时，构造函数需要的参数。
     * @param <T> 反序列化为的实体类。
     * @return 返回反序列化后的实体类。
     *
     * @author luern0313
     */
    public static <T> T fromJson(LsonElement json, TypeReference<T> typeReference, Object... parameters)
    {
        Deserialization.typeReference = typeReference;
        Deserialization.parameterizedTypes.clear();
        return Deserialization.fromJson(json, new TypeUtil(typeReference.type), new ArrayList<>(), null, getParameterTypes(parameters), parameters);
    }

    /**
     * 将json反序列化为指定的实体类。
     *
     * @param json Lson解析过的json对象。
     * @param typeReference {@link TypeReference}类，用于泛型类的反序列化。
     * @param parameterTypes 实例化类时，构造函数需要参数的类型。
     * @param parameters 实例化类时，构造函数需要的参数。
     * @param <T> 反序列化为的实体类。
     * @return 返回反序列化后的实体类。
     *
     * @author luern0313
     */
    public static <T> T fromJson(LsonElement json, TypeReference<T> typeReference, Class<?>[] parameterTypes, Object[] parameters)
    {
        Deserialization.typeReference = typeReference;
        Deserialization.parameterizedTypes.clear();
        return Deserialization.fromJson(json, new TypeUtil(typeReference.type), new ArrayList<>(), null, parameterTypes, parameters);
    }

    /**
     * 获取json中对应JSONPath的值。
     *
     * @param json Lson解析过的json对象。
     * @param path JSONPath，用于描述要取到的值在json中的位置。
     * @return JSONPath对应的值。
     *
     * @author luern0313
     */
    public static Object getValue(LsonElement json, String path)
    {
        return getValue(json, path, Object.class);
    }

    /**
     * 获取json中对应JSONPath的值，并指明该值的类型。
     *
     * @param json Lson解析过的json对象。
     * @param path JSONPath，用于描述要取到的值在json中的位置。
     * @param clz 该值的类型，Lson会尝试将该值转为指定的类型。
     * @param <T> 指定的类型。
     * @return JSONPath对应的值。
     *
     * @author luern0313
     */
    @SuppressWarnings("unchecked")
    public static <T> T getValue(LsonElement json, String path, Class<T> clz)
    {
        TypeUtil typeUtil = new TypeUtil(clz);
        T t = (T) Deserialization.finalValueHandle(Deserialization.getValue(json, new String[]{path}, new ArrayList<>(), typeUtil, null), typeUtil);
        if(t == null && typeUtil.isPrimitive())
            return (T) PRIMITIVE_DEFAULT_VALUE.get(clz.getName());
        return t;
    }

    /**
     * 将任意类型数据序列化为json。
     *
     * @param object 要序列化的数据。
     * @return 序列化结果。
     *
     * @author luern0313
     */
    public static String toJson(Object object)
    {
        return Serialization.toJson(object).toString();
    }

    /**
     * 根据JSONPath将数据填充至LsonElement中。
     *
     * @param lsonElement 被填充的LsonElement。
     * @param path 标注数据位置的JSONPath。
     * @param value 要填充的数据。
     * @return 填充完成的LsonElement。
     */
    public static LsonElement putValue(LsonElement lsonElement, String path, Object value)
    {
        Serialization.setValue(Serialization.toJson(value), path, new ArrayList<>(), lsonElement);
        return lsonElement;
    }

    private static Class<?>[] getParameterTypes(Object[] parameters)
    {
        Class<?>[] parameterTypes = new Class<?>[parameters.length];
        for (int i = 0; i < parameters.length; i++)
        {
            TypeUtil typeUtils = new TypeUtil(parameters[i].getClass());
            if(typeUtils.isWrapClass())
                parameterTypes[i] = typeUtils.getPrimitiveClass();
            else
                parameterTypes[i] = typeUtils.getAsClass();
        }
        return parameterTypes;
    }

    protected static HashMap<String, Object> PRIMITIVE_DEFAULT_VALUE = new HashMap<String, Object>()
    {{
        put(int.class.getName(), 0);
        put(byte.class.getName(), (byte) 0);
        put(char.class.getName(), (char) 0);
        put(double.class.getName(), 0d);
        put(float.class.getName(), 0f);
        put(long.class.getName(), 0L);
        put(short.class.getName(), (short) 0);
        put(boolean.class.getName(), false);
    }};
}

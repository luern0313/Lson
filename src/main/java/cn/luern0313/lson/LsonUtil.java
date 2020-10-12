package cn.luern0313.lson;

import java.util.ArrayList;
import java.util.HashMap;

import cn.luern0313.lson.element.LsonElement;
import cn.luern0313.lson.util.TypeUtil;

/**
 * Lson反序列化相关类。
 *
 * @author luern0313
 */

public class LsonUtil
{
    /**
     * 将json反序列化为指定的实体类。
     *
     * @param json Lson解析过的json对象。
     * @param clz 要反序列化实体类的Class对象。
     * @param <T> 反序列化为的实体类。
     * @return 返回反序列化后的实体类。
     *
     * @author luern0313
     */
    public static <T> T fromJson(LsonElement json, Class<T> clz)
    {
        return Deserialization.fromJson(json, new TypeUtil(clz), null, new ArrayList<>());
    }

    /**
     * 将json反序列化为指定的实体类。
     *
     * @param json Lson解析过的json对象。
     * @param typeReference {@link TypeReference}类，用于泛型类的反序列化。
     * @param <T> 反序列化为的实体类。
     * @return 返回反序列化后的实体类。
     *
     * @author luern0313
     */
    public static <T> T fromJson(LsonElement json, TypeReference<T> typeReference)
    {
        Deserialization.typeReference = typeReference;
        Deserialization.parameterizedTypes.clear();
        return Deserialization.fromJson(json, new TypeUtil(typeReference.type), null, new ArrayList<>());
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
        T t = (T) Deserialization.finalValueHandle(Deserialization.getValue(json, new String[]{path}, null, typeUtil, null), typeUtil);
        if(t == null && clz.isPrimitive())
            return (T) primitiveDefaultValue.get(clz.getName());
        return t;
    }

    /**
     * 程序开始时，通过此方法传入实现{@link Deserialization.LsonAnnotationListener}接口类的实例，自定义注解才可正常运行。
     *
     * @param lsonAnnotationListener 实现{@link Deserialization.LsonAnnotationListener}接口的实例。
     *
     * @author luern0313
     */
    public static void setLsonAnnotationListener(Deserialization.LsonAnnotationListener lsonAnnotationListener)
    {
        Deserialization.lsonAnnotationListener = lsonAnnotationListener;
    }

    private static HashMap<String, Object> primitiveDefaultValue = new HashMap<String, Object>()
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

package cn.luern0313.lson;

import java.util.ArrayList;

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
    @SuppressWarnings("unchecked")
    public static <T> T fromJson(LsonElement json, TypeReference<T> typeReference)
    {
        Deserialization.typeReference = typeReference;
        Deserialization.parameterizedTypes.clear();
        return (T) Deserialization.fromJson(json, new TypeUtil(typeReference.type), null, new ArrayList<>());
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
        return Deserialization.getValue(json, new String[]{path}, null, null, null);
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
}

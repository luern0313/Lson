package cn.luern0313.lson

import cn.luern0313.lson.element.LsonElement
import kotlin.reflect.javaType
import kotlin.reflect.typeOf

/**
 * 被 luern 创建于 2022/4/21.
 */
object LsonUtilKT {
    /**
     * 将json反序列化为指定的实体类。
     *
     * 使用例：LsonUtilKt.fromJson<XXXModel>(XXXElement, xxx...)
     *
     * @param json Lson解析过的json对象。
     * @param parameters 实例化类时，构造函数需要的参数。
     * @param <T> 反序列化为的实体类。
     * @return 返回反序列化后的实体类。
     *
     * @author luern0313
     */
    @OptIn(ExperimentalStdlibApi::class)
    @JvmStatic
    inline fun <reified T> fromJson(json: LsonElement?, vararg parameters: Any?): T {
        return LsonUtil.fromJson(json, TypeReference(typeOf<T>().javaType), *parameters)
    }

    /**
     * 将json反序列化为指定的实体类。
     *
     * 使用例：LsonUtilKt.fromJson<XXXModel>(XXXElement, xxx, xxx)
     *
     * @param json Lson解析过的json对象。
     * @param parameterTypes 实例化类时，构造函数需要参数的类型。
     * @param parameters 实例化类时，构造函数需要的参数。
     * @param <T> 反序列化为的实体类。
     * @return 返回反序列化后的实体类。
     *
     * @author luern0313
     */
    @OptIn(ExperimentalStdlibApi::class)
    @JvmStatic
    inline fun <reified T> fromJson(json: LsonElement?, parameterTypes: Array<Class<*>?>?, parameters: Array<Any?>?): T {
        return LsonUtil.fromJson(json, TypeReference(typeOf<T>().javaType), parameterTypes, parameters)
    }
}

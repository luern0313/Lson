package cn.luern0313.lson

import cn.luern0313.lson.element.LsonElement
import kotlin.reflect.javaType
import kotlin.reflect.typeOf

/**
 * 被 luern 创建于 2022/4/21.
 */

/**
 * 将json反序列化为指定的实体类
 *
 * 使用例：Lson.def().fromJson<XXXModel>(XXXElement, xxx...)
 *
 * @param json Lson解析过的json对象
 * @param <T> 反序列化为的实体类
 * @return 返回反序列化后的实体类
 *
 * @author luern0313
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T> Lson.fromJson(json: LsonElement?, vararg parameter: Any): T {
    return fromJson(json, TypeReference(typeOf<T>().javaType), *parameter)
}

/**
 * 将json反序列化为指定的实体类
 *
 * 使用例：Lson.def().fromJson<XXXModel>(XXXElement, xxx...)
 *
 * @param json JSON字符串
 * @param <T> 反序列化为的实体类
 * @param parameter
 * @return 返回反序列化后的实体类
 *
 * @author luern0313
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T> Lson.fromJson(json: String?, vararg parameter: Any): T {
    return fromJson(json, TypeReference(typeOf<T>().javaType), *parameter)
}

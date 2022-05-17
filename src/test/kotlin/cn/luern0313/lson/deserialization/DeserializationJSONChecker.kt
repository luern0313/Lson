package cn.luern0313.lson.deserialization

import java.lang.reflect.Type
import kotlin.reflect.javaType
import kotlin.reflect.typeOf

/**
 * 被 luern 创建于 2022/4/25.
 */
interface DeserializationJSONChecker<Model> {
    fun json(): String
    fun check(model: Model)
}
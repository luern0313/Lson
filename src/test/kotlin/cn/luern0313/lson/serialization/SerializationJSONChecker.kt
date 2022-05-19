package cn.luern0313.lson.serialization

import cn.luern0313.lson.element.LsonElement
import java.lang.reflect.Type
import kotlin.reflect.javaType
import kotlin.reflect.typeOf

/**
 * 被 luern 创建于 2022/4/25.
 */
interface SerializationJSONChecker<Model> {
    fun model(): Model
    fun check(lsonElement: LsonElement)
}
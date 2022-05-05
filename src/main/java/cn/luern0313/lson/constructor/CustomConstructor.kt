package cn.luern0313.lson.constructor

import java.lang.reflect.Type

/**
 * 被 luern 创建于 2022/5/3.
 */
interface CustomConstructor<T> {
    fun create(type: Type?): T
}
package cn.luern0313.lson

import cn.luern0313.lson.serialization.*
import org.junit.Test

/**
 * 被 luern 创建于 2022/5/31.
 */
class SerializationTestKt {
    @Test
    fun toJsonTest1() {
        JSON1.check(Lson.def().toJsonElement(JSON1.model()))
    }
}

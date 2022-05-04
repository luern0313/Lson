package cn.luern0313.lson

import cn.luern0313.lson.deserialization.Json1
import cn.luern0313.lson.deserialization.Json2
import org.junit.Test

/**
 * 被 luern 创建于 2022/4/24.
 */
class LsonKTTest {
    @Test
    fun fromJsonTest() {
        Json1.check(Lson.def().fromJson(Lson.def().parse(Json1.json())))
        Json2.check(Lson.def().fromJson(Lson.def().parse(Json2.json())))
    }
}

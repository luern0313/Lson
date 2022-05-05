package cn.luern0313.lson

import cn.luern0313.lson.constructor.CustomConstructor
import cn.luern0313.lson.deserialization.Json1
import cn.luern0313.lson.deserialization.Json2
import cn.luern0313.lson.deserialization.Json3
import cn.luern0313.lson.deserialization.Json4
import org.junit.Test
import java.lang.reflect.Type

/**
 * 被 luern 创建于 2022/4/24.
 */
class DeserializationTestKt {
    @Test
    fun fromJsonTest() {
        Json1.check(Lson.def().fromJson(Lson.def().parse(Json1.json())))
        Json2.check(Lson.def().fromJson(Lson.def().parse(Json2.json())))
        Json3.check(Lson.def().fromJson(Lson.def().parse(Json3.json())))

        val lson4: Lson = Lson.LsonBuilder().setCustomConstructor(object : CustomConstructor<Json4.FeedItemModel> {
            override fun create(type: Type?): Json4.FeedItemModel {
                return Json4.FeedItemModel(123)
            }
        }).setCustomConstructor(object : CustomConstructor<Json4.FeedItemModel.FeedUserModel> {
            override fun create(type: Type?): Json4.FeedItemModel.FeedUserModel {
                return Json4.FeedItemModel.FeedUserModel(123)
            }
        }).build()

        Json4.check(lson4.fromJson(lson4.parse(Json4.json())))
    }
}

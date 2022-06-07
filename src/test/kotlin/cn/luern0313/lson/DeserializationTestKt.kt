package cn.luern0313.lson

import cn.luern0313.lson.adapter.TypeAdapter
import cn.luern0313.lson.constructor.CustomConstructor
import cn.luern0313.lson.deserialization.*
import cn.luern0313.lson.element.LsonArray
import cn.luern0313.lson.element.LsonElement
import cn.luern0313.lson.element.LsonObject
import cn.luern0313.lson.element.LsonPrimitive
import org.junit.Test
import java.awt.Color
import java.lang.reflect.Type

/**
 * 被 luern 创建于 2022/4/24.
 */
class DeserializationTestKt {
    @Test
    fun fromJsonTest1() {
        JSON1.check(Lson.def().fromJson(Lson.def().parse(JSON1.json())))
    }

    @Test
    fun fromJsonTest2() {
        JSON2.check(Lson.def().fromJson(Lson.def().parse(JSON2.json())))
    }

    @Test
    fun fromJsonTest3() {
        JSON3.check(Lson.def().fromJson(Lson.def().parse(JSON3.json())))
    }

    @Test
    fun fromJsonTest4() {
        val lson4: Lson = Lson.LsonBuilder().setCustomConstructor(object : CustomConstructor<JSON4.FeedItemModel> {
                override fun create(type: Type): JSON4.FeedItemModel {
                    return JSON4.FeedItemModel(123)
                }
            }).setCustomConstructor(object : CustomConstructor<JSON4.FeedItemModel.FeedUserModel> {
                override fun create(type: Type): JSON4.FeedItemModel.FeedUserModel {
                    return JSON4.FeedItemModel.FeedUserModel(1234)
                }
            }).build()
        JSON4.check(lson4.fromJson(lson4.parse(JSON4.json())))
    }

    @Test
    fun fromJsonTest5() {
        val lson5: Lson = Lson.LsonBuilder().setTypeAdapter(object : TypeAdapter<Color> {
            override fun deserialization(value: LsonElement): Color? {
                return when (value) {
                    is LsonPrimitive -> Color.decode(value.asString);
                    is LsonObject -> des(value["r"], value["g"], value["b"])
                    is LsonArray -> des(value[0], value[1], value[2])
                    else -> null
                }
            }

            private fun des(r: LsonElement?, g: LsonElement?, b: LsonElement?): Color? {
                if (r is LsonPrimitive && g is LsonPrimitive && b is LsonPrimitive) {
                    return if (r.isFloat || r.isDouble)
                        Color(r.asFloat, g.asFloat, b.asFloat)
                    else
                        Color(r.asInt, g.asInt, b.asInt)
                }
                return null
            }

            override fun serialization(obj: Color?): LsonElement? {
                return null
            }
        }).build()
        JSON5.check(lson5.fromJson(lson5.parse(JSON5.json())))
    }
}

package cn.luern0313.lson

import cn.luern0313.lson.Lson.LsonBuilder
import cn.luern0313.lson.adapter.TypeAdapter
import cn.luern0313.lson.constructor.CustomConstructor
import cn.luern0313.lson.constructor.InstanceResult
import cn.luern0313.lson.deserialization.*
import cn.luern0313.lson.deserialization.JSON6.ItemModel
import cn.luern0313.lson.deserialization.JSON6.ItemModel.*
import cn.luern0313.lson.deserialization.JSON6.check
import cn.luern0313.lson.element.LsonArray
import cn.luern0313.lson.element.LsonElement
import cn.luern0313.lson.element.LsonObject
import cn.luern0313.lson.element.LsonPrimitive
import org.junit.Test
import java.awt.Color
import java.lang.reflect.Type
import java.util.*

/**
 * 被 luern 创建于 2022/4/24.
 */
class DeserializationTestKt {
    @Test
    fun fromJsonTest1() {
        JSON1.check(Lson.def().fromJson(JSON1.json()))
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
        val lson4: Lson = Lson.LsonBuilder().setCustomConstructor(object :
            CustomConstructor<JSON4.FeedItemModel>() {
            override fun create(type: Type, vararg parameter: Any?): JSON4.FeedItemModel {
                return JSON4.FeedItemModel(123)
            }
        }).setCustomConstructor(object : CustomConstructor<JSON4.FeedItemModel.FeedUserModel>() {
            override fun create(
                type: Type,
                vararg parameter: Any?
            ): JSON4.FeedItemModel.FeedUserModel {
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
                    is LsonPrimitive -> Color.decode(value.asString)
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

    @Test
    fun fromJsonTest6() {
        val lson6 = LsonBuilder().setCustomConstructor(object : CustomConstructor<N1?>() {
            override fun create(type: Type, vararg parameter: Any): N1 {
                return N1(1)
            }
        }).setCustomConstructor(object : CustomConstructor<InstanceResult<out N23?>?>() {
            override fun create(type: Type, vararg parameter: Any): InstanceResult<out N23?>? {
                if (type === N2::class.java) {
                    return InstanceResult(N2(2))
                } else if (type === N3::class.java) {
                    return InstanceResult(N3(3))
                }
                return null
            }
        }).setCustomConstructor(object : CustomConstructor<N4<Int?>?>() {
            override fun create(type: Type, vararg parameter: Any): N4<Int?> {
                return N4(4)
            }
        }).setCustomConstructor(object : CustomConstructor<N4<String?>?>() {
            override fun create(type: Type, vararg parameter: Any): N4<String?> {
                return N4("4")
            }
        }).setCustomConstructor(object : CustomConstructor<InstanceResult<N5?>?>() {
            override fun create(type: Type, vararg parameter: Any): InstanceResult<N5?> {
                return InstanceResult(N5(5))
            }
        }).setCustomConstructor(object : CustomConstructor<N6<Int?>?>() {
            override fun create(type: Type, vararg parameter: Any): N6<Int?> {
                return N6(6)
            }
        }).setCustomConstructor(object : CustomConstructor<N6<String?>?>() {
            override fun create(type: Type, vararg parameter: Any): N6<String?> {
                return N6("6")
            }
        }).build()
        check(lson6.fromJson(JSON6.json(), ItemModel::class.java))
    }

    @Test
    fun fromJsonTest7() {
        JSON7.check(Lson.def().fromJson(JSON7.json()))
    }
}

package cn.luern0313.lson.deserialization

import cn.luern0313.lson.annotation.field.LsonPath
import kotlin.test.assertEquals

/**
 * 测试Constructor
 * 2022/6/4
 */
object JSON6: DeserializationJSONChecker<JSON6.ItemModel> {
    private val json = """
        {
            "number1": 1,
            "number2": 2,
            "number3": 3,
            "number4": 4,
            "number5": [5, 4, 3],
            "number6": 6
        }
    """.trimIndent()

    override fun json(): String {
        return json
    }

    override fun check(model: ItemModel) {
        assertEquals(model.n1.n1, 1)
        assertEquals(model.n1.constructor, 1)
        assertEquals(model.n2.n2, 2)
        assertEquals(model.n2.constructor, 2)
        assertEquals(model.n3.n3, 3)
        assertEquals(model.n3.constructor, 3)
        assertEquals(model.n41.n4, 4)
        assertEquals(model.n41.constructor, 4)
        assertEquals(model.n42.n4, 4)
        assertEquals(model.n42.constructor, "4")

        assertEquals(model.n5.size, 3)
        assertEquals(model.n5[0].n5, 5)
        assertEquals(model.n5[1].n5, 4)
        assertEquals(model.n5[2].n5, 3)

        assertEquals(model.n61.n6, 6)
        assertEquals(model.n61.constructor, 6)
        assertEquals(model.n62.n6, 6)
        assertEquals(model.n62.constructor, "6")
    }

    @Suppress("ArrayInDataClass")
    data class ItemModel(
        @LsonPath("number1")
        val n1: N1,

        @LsonPath("number2")
        val n2: N2,

        @LsonPath("number3")
        val n3: N3,

        @LsonPath("number4")
        val n41: N4<Int>,

        @LsonPath("number4")
        val n42: N4<String>,

        @LsonPath("number5")
        val n5: Array<N5>,

        @LsonPath("number6")
        val n61: N6<Int>,

        @LsonPath("number6")
        val n62: N6<String>
    ) {
        class N1(val constructor: Int /* 1 */) {
            @LsonPath("\$.number1")
            val n1: Int? = null
        }
        open class N23(val constructor: Int /* 23 */)

        class N2(constructor: Int /* 2 */) : N23(constructor) {
            @LsonPath("\$.number2")
            val n2: Int? = null
        }
        class N3(constructor: Int /* 3 */) : N23(constructor) {
            @LsonPath("\$.number3")
            val n3: Int? = null
        }
        class N4<T>(val constructor: T /* 4 "4" */) {
            @LsonPath("\$.number4")
            val n4: Int? = null
        }
        class N5(val constructor: Int /* 6 */) {
            @LsonPath("@")
            var n5: Int? = null
        }
        class N6<T>(val constructor: T /* 6 "6" */) {
            @LsonPath("\$.number6")
            val n6: Int? = null
        }
    }
}

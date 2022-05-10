package cn.luern0313.lson.deserialization

import cn.luern0313.lson.annotation.field.LsonPath
import cn.luern0313.lson.element.LsonArray
import cn.luern0313.lson.element.LsonElement
import cn.luern0313.lson.element.LsonObject
import cn.luern0313.lson.element.LsonPrimitive
import com.sun.org.apache.xml.internal.utils.FastStringBuffer
import java.awt.Color
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * 测试TypeAdapter
 * 2022/5/9
 */
object Json5: DeserializationJsonChecker<Json5.ColorModel> {
    private val json = """
        {
            "color1": "#123456",
            "color2": {
                "r": 80,
                "g": 216,
                "b": 23
            },
            "color3": [43, 165, 134],
            "color4": {
                "r": 0.1,
                "g": 0.34,
                "b": 0.81
            },
            "color5": [0.56, 0.6, 0.498],
            "string_builder1": "stringBuilder123456",
            "string_builder2": {
                "i": "l",
                "d": ["e", "r"]
            },
            "string_builder3": [1, 2, 3],
            "string_builder4": null,
            "string_buffer1": "stringBuffer1234567",
            "string_buffer2": {
                "f": "f",
                "e": ["r", "r"]
            },
            "string_buffer3": [4, 5, 6],
            "string_buffer4": null,
            "util_date1": 1652096977,
            "util_date2": "1652096978",
            "util_date3": null,
            "sql_date1": 1652096979,
            "sql_date2": "1652096980",
            "sql_date3": null,
            "lson_element1": "lson_element",
            "lson_element2": 114514,
            "lson_element3": {
                "k": "v",
                "q": 113355
            },
            "lson_element4": [1, 2, 3],
            "lson_element5": null
        }
    """.trimIndent()

    override fun json(): String {
        return json
    }

    override fun check(model: ColorModel) {
        assertEquals(model.color1.red, 18)
        assertEquals(model.color1.green, 52)
        assertEquals(model.color1.blue, 86)
        
        assertEquals(model.color2.red, 80)
        assertEquals(model.color2.green, 216)
        assertEquals(model.color2.blue, 23)

        assertEquals(model.color3.red, 43)
        assertEquals(model.color3.green, 165)
        assertEquals(model.color3.blue, 134)

        assertEquals(model.color4.red, 26)
        assertEquals(model.color4.green, 87)
        assertEquals(model.color4.blue, 207)

        assertEquals(model.color5.red, 143)
        assertEquals(model.color5.green, 153)
        assertEquals(model.color5.blue, 127)
        
        assertEquals(model.stringBuilder1.toString(), "stringBuilder123456")
        assertEquals(model.stringBuilder2.toString(), "{\"i\": \"l\", \"d\": [\"e\", \"r\"]}")
        assertEquals(model.stringBuilder3.toString(), "[1, 2, 3]")
        assertNull(model.stringBuilder4)
        assertEquals(model.stringBuffer1.toString(), "stringBuffer1234567")
        assertEquals(model.stringBuffer2.toString(), "{\"f\": \"f\", \"e\": [\"r\", \"r\"]}")
        assertEquals(model.stringBuffer3.toString(), "[4, 5, 6]")
        assertNull(model.stringBuffer4)
        
        assertEquals(model.utilDate1.time, 1652096977)
        assertEquals(model.utilDate2.time, 1652096978)
        assertNull(model.utilDate3)
        assertEquals(model.sqlDate1.time, 1652096979)
        assertEquals(model.sqlDate2.time, 1652096980)
        assertNull(model.sqlDate3)

        assertEquals(model.lsonElement1.asLsonPrimitive.asString, "lson_element")
        assertEquals(model.lsonElement2.asLsonPrimitive.asInt, 114514)
        assertEquals(model.lsonElement3.asLsonObject.toString(), "{\"k\": \"v\", \"q\": 113355}")
        assertEquals(model.lsonElement4.asLsonArray.toString(), "[1, 2, 3]")
        assertNull(model.lsonElement5)

        assertNull(model.lsonObject1)
        assertNull(model.lsonObject2)
        assertEquals(model.lsonObject3.asLsonObject.toString(), "{\"k\": \"v\", \"q\": 113355}")
        assertNull(model.lsonObject4)
        assertNull(model.lsonObject5)

        assertNull(model.lsonArray1)
        assertNull(model.lsonArray2)
        assertNull(model.lsonArray3)
        assertEquals(model.lsonArray4.asLsonArray.toString(), "[1, 2, 3]")
        assertNull(model.lsonArray5)

        assertEquals(model.lsonPrimitive1.asLsonPrimitive.asString, "lson_element")
        assertEquals(model.lsonPrimitive2.asLsonPrimitive.asInt, 114514)
        assertNull(model.lsonPrimitive3)
        assertNull(model.lsonPrimitive4)
        assertNull(model.lsonPrimitive5)
    }

    data class ColorModel (
        @LsonPath
        val color1: Color,

        @LsonPath
        val color2: Color,

        @LsonPath
        val color3: Color,

        @LsonPath
        val color4: Color,

        @LsonPath
        val color5: Color,
        
        @LsonPath
        val stringBuilder1: StringBuilder,

        @LsonPath
        val stringBuilder2: StringBuilder,

        @LsonPath
        val stringBuilder3: StringBuilder,

        @LsonPath
        val stringBuilder4: StringBuilder?,
        
        @LsonPath
        val stringBuffer1: StringBuffer,

        @LsonPath
        val stringBuffer2: StringBuffer,

        @LsonPath
        val stringBuffer3: StringBuffer,

        @LsonPath
        val stringBuffer4: StringBuilder?,

        @LsonPath
        val utilDate1: java.util.Date,

        @LsonPath
        val utilDate2: java.util.Date,

        @LsonPath
        val utilDate3: java.util.Date?,

        @LsonPath
        val sqlDate1: java.sql.Date,

        @LsonPath
        val sqlDate2: java.sql.Date,

        @LsonPath
        val sqlDate3: java.sql.Date?,

        @LsonPath("lson_element1")
        val lsonElement1: LsonElement,

        @LsonPath("lson_element2")
        val lsonElement2: LsonElement,

        @LsonPath("lson_element3")
        val lsonElement3: LsonElement,

        @LsonPath("lson_element4")
        val lsonElement4: LsonElement,

        @LsonPath("lson_element5")
        val lsonElement5: LsonElement,

        @LsonPath("lson_element1")
        val lsonObject1: LsonObject,

        @LsonPath("lson_element2")
        val lsonObject2: LsonObject,

        @LsonPath("lson_element3")
        val lsonObject3: LsonObject,

        @LsonPath("lson_element4")
        val lsonObject4: LsonObject,

        @LsonPath("lson_element5")
        val lsonObject5: LsonObject,

        @LsonPath("lson_element1")
        val lsonArray1: LsonArray,

        @LsonPath("lson_element2")
        val lsonArray2: LsonArray,

        @LsonPath("lson_element3")
        val lsonArray3: LsonArray,

        @LsonPath("lson_element4")
        val lsonArray4: LsonArray,

        @LsonPath("lson_element5")
        val lsonArray5: LsonArray,

        @LsonPath("lson_element1")
        val lsonPrimitive1: LsonPrimitive,

        @LsonPath("lson_element2")
        val lsonPrimitive2: LsonPrimitive,

        @LsonPath("lson_element3")
        val lsonPrimitive3: LsonPrimitive,

        @LsonPath("lson_element4")
        val lsonPrimitive4: LsonPrimitive,

        @LsonPath("lson_element5")
        val lsonPrimitive5: LsonPrimitive
    )
}

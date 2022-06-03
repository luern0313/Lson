package cn.luern0313.lson.element;

import org.junit.Test;

import cn.luern0313.lson.Lson;

import static org.junit.Assert.assertEquals;

/**
 * 被 luern 创建于 2022/6/2.
 */

public class LsonPrimitiveTest {
    @Test
    public void escapeStringTest() {
        assertEquals(Lson.def().parse("\"aaa\"").getAsLsonPrimitive().get(), "aaa");
        assertEquals(Lson.def().parse("\"a\\\"aa\"").getAsLsonPrimitive().get(), "a\"aa");
        assertEquals(Lson.def().parse("\"a\\/aa\"").getAsLsonPrimitive().get(), "a/aa");
        assertEquals(Lson.def().parse("\"aa\\ba\"").getAsLsonPrimitive().get(), "aa\ba");
        assertEquals(Lson.def().parse("\"aa\\fa\"").getAsLsonPrimitive().get(), "aa\fa");
        assertEquals(Lson.def().parse("\"aa\\na\"").getAsLsonPrimitive().get(), "aa\na");
        assertEquals(Lson.def().parse("\"aaa\\r\"").getAsLsonPrimitive().get(), "aaa\r");
        assertEquals(Lson.def().parse("\"a\\taa\"").getAsLsonPrimitive().get(), "a\taa");
        assertEquals(Lson.def().parse("\"a\\u566b\\u5618\\u550faaa\"").getAsLsonPrimitive().get(), "a噫嘘唏aaa");
        assertEquals(Lson.def().parse("\"核物理学又称\\\"原子核物理学\\\"，是20世纪新建立的一一\\b个\\u7269\\u7406\\u5b66分支。\\n它研究原子核的结构和变化规律\\/射线束的产生、探测和分析技术\\/以及同核能、\\t核技术应用有关的\\r物理问题。\\f它是一门既有深刻理论意义，又有\\u91cd\\u5927\\u5b9e\\u8df5\\u610f\\u4e49的学科。\"").getAsLsonPrimitive().get(),
                "核物理学又称\"原子核物理学\"，是20世纪新建立的一一\b个\u7269\u7406\u5b66分支。\n它研究原子核的结构和变化规律/射线束的产生、探测和分析技术/以及同核能、\t核技术应用有关的\r物理问题。\f它是一门既有深刻理论意义，又有\u91cd\u5927\u5b9e\u8df5\u610f\u4e49的学科。");
    }

    @Test
    public void unescapeStringTest() {
        assertEquals(new LsonPrimitive("aaa").toString(), "\"aaa\"");
        assertEquals(new LsonPrimitive("a\"aa").toString(), "\"a\\\"aa\"");
        assertEquals(new LsonPrimitive("a/aa").toString(), "\"a\\/aa\"");
        assertEquals(new LsonPrimitive("aa\ba").toString(), "\"aa\\ba\"");
        assertEquals(new LsonPrimitive("aa\fa").toString(), "\"aa\\fa\"");
        assertEquals(new LsonPrimitive("aa\na").toString(), "\"aa\\na\"");
        assertEquals(new LsonPrimitive("aaa\r").toString(), "\"aaa\\r\"");
        assertEquals(new LsonPrimitive("a\taa").toString(), "\"a\\taa\"");
        assertEquals(new LsonPrimitive("核物理学又称\"原子核物理学\"，是20世纪新建立的一一\b个\u7269\u7406\u5b66分支。\n它研究原子核的结构和变化规律/射线束的产生、探测和分析技术/以及同核能、\t核技术应用有关的\r物理问题。\f它是一门既有深刻理论意义，又有\u91cd\u5927\u5b9e\u8df5\u610f\u4e49的学科。").toString(),
                "\"核物理学又称\\\"原子核物理学\\\"，是20世纪新建立的一一\\b个物理学分支。\\n它研究原子核的结构和变化规律\\/射线束的产生、探测和分析技术\\/以及同核能、\\t核技术应用有关的\\r物理问题。\\f它是一门既有深刻理论意义，又有重大实践意义的学科。\"");
    }
}

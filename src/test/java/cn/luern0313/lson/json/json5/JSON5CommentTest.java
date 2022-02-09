package cn.luern0313.lson.json.json5;

import org.junit.Test;

import cn.luern0313.lson.LsonUtil;
import cn.luern0313.lson.element.LsonArray;

import static org.junit.Assert.assertEquals;

/**
 * 被 luern 创建于 2022/2/2.
 */

public class JSON5CommentTest
{
    @Test
    public void singleCommentTest()
    {
        assertEquals(LsonUtil.parse("//aaaa/aa\n1111").getAsLsonPrimitive().getAsInt(), 1111);

        LsonArray lsonArray = LsonUtil.parseAsArray("[1111,\n//aaaa/aa\n2222]");
        assertEquals(lsonArray.get(0).getAsLsonPrimitive().getAsInt(), 1111);
        assertEquals(lsonArray.get(1).getAsLsonPrimitive().getAsInt(), 2222);
    }

    @Test
    public void multipleCommentTest()
    {
        assertEquals(LsonUtil.parse("1111\n/*aaaaaa\nbbbbb*/").getAsLsonPrimitive().getAsInt(), 1111);
        assertEquals(LsonUtil.parse("/*aaaaaabbbbb*/\n1111").getAsLsonPrimitive().getAsInt(), 1111);
        assertEquals(LsonUtil.parse("/*aaaaaabbbbb*/1111").getAsLsonPrimitive().getAsInt(), 1111);
        assertEquals(LsonUtil.parse("1111\n/*aaaaaa\nbb*bbb*/").getAsLsonPrimitive().getAsInt(), 1111);

        LsonArray lsonArray = LsonUtil.parseAsArray("/*comment*/[/*comment*/1111/*comment*/,/*comment*/2222]/*comment*/");
        assertEquals(lsonArray.get(0).getAsLsonPrimitive().getAsInt(), 1111);
        assertEquals(lsonArray.get(1).getAsLsonPrimitive().getAsInt(), 2222);
    }
}

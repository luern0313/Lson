package cn.luern0313.lson.element;

/**
 * 被 luern0313 创建于 2020/8/11.
 */

public class LsonNull extends LsonElement
{
    public static LsonNull getJsonNull()
    {
        return new LsonNull();
    }

    @Override
    public LsonElement deepCopy()
    {
        return this;
    }
}

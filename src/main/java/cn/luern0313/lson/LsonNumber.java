package cn.luern0313.lson;

/**
 * 被 luern0313 创建于 2020/8/15.
 */

public class LsonNumber<T>
{
    Class<T> targetClass;
    Object value;

    public LsonNumber(Object value)
    {
        //targetClass = value.getClass();
        this.value = value;
    }

    public void changeValue(Object value)
    {
        this.value = value;
    }

}

package cn.luern0313.lson;

import cn.luern0313.lson.element.LsonPrimitive;
import cn.luern0313.lson.util.TypeUtil;

/**
 * Lson序列化相关类。
 *
 * @author luern0313
 */

public class Serialization
{
    public static String toJson(Object value)
    {
        TypeUtil typeUtil = new TypeUtil(value.getClass());
        if(typeUtil.isPrimitive())
            return new LsonPrimitive(value).toString();
        else if(typeUtil.isArrayTypeClass() || typeUtil.isListTypeClass())
            return null;
        return null;
    }
}

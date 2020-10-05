package cn.luern0313.lson.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 被 luern0313 创建于 2020/9/8.
 */

public class TypeUtil
{
    Type type;

    public TypeUtil(Type type)
    {
        this.type = type;
    }

    public Type getAsType()
    {
        return type;
    }

    public boolean isClass()
    {
        return type instanceof Class;
    }

    public Class<?> getAsClass()
    {
        if(type instanceof ParameterizedType)
            return (Class<?>) ((ParameterizedType) type).getRawType();
        return (Class<?>) getAsType();
    }

    public String getName()
    {
        if(type != null)
        {
            String name = type.toString();
            if(name.startsWith("class "))
                return name.substring(6);
            return name;
        }
        return null;
    }

    public boolean isNull()
    {
        return type == null;
    }
}

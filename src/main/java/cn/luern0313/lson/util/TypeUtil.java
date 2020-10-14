package cn.luern0313.lson.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import cn.luern0313.lson.exception.LsonInstantiationException;

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

    public boolean isPrimitive()
    {
        return getAsClass().isPrimitive();
    }

    public Constructor<?> getConstructor(Class<?>... parameterTypes)
    {
        try
        {
            Constructor<?> constructor = getAsClass().getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            return constructor;
        }
        catch (RuntimeException | NoSuchMethodException e)
        {
            return null;
        }
    }

    public boolean isMapTypeClass()
    {
        try
        {
            return Map.class.isAssignableFrom(getAsClass()) || getConstructor().newInstance() instanceof Map;
        }
        catch (IllegalAccessException | LsonInstantiationException | InstantiationException | InvocationTargetException | NullPointerException | ClassCastException ignored)
        {
        }
        return false;
    }

    public boolean isListTypeClass()
    {
        try
        {
            return List.class.isAssignableFrom(getAsClass()) || getConstructor().newInstance() instanceof List;
        }
        catch (IllegalAccessException | LsonInstantiationException | InstantiationException | InvocationTargetException | NullPointerException | ClassCastException ignored)
        {
        }
        return false;
    }

    public boolean isArrayTypeClass()
    {
        if(isClass())
            return getAsClass().isArray();
        return getAsType() instanceof GenericArrayType;
    }

    public TypeUtil getMapType()
    {
        Type t = getAsType();
        if (t instanceof ParameterizedType)
            t = ((ParameterizedType) t).getActualTypeArguments()[1];
        return new TypeUtil(t);
    }

    public TypeUtil getListType()
    {
        Type t = getAsType();
        if(t instanceof ParameterizedType)
            t = ((ParameterizedType) t).getActualTypeArguments()[0];
        return new TypeUtil(t);
    }

    public TypeUtil getArrayType()
    {
        Type t = getAsType();
        if(isClass())
            t = getAsClass().getComponentType();
        else if(t instanceof GenericArrayType)
            t = ((GenericArrayType) t).getGenericComponentType();
        return new TypeUtil(t);
    }

    public TypeUtil getArrayRealType()
    {
        TypeUtil type = null;
        while (isArrayTypeClass())
            type = getArrayType();
        return type;
    }
}

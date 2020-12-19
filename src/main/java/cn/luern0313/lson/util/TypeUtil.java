package cn.luern0313.lson.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.luern0313.lson.element.LsonArray;
import cn.luern0313.lson.element.LsonElement;
import cn.luern0313.lson.element.LsonObject;
import cn.luern0313.lson.element.LsonPrimitive;

/**
 * 被 luern0313 创建于 2020/9/8.
 */

public class TypeUtil
{
    Type type;

    public TypeUtil(Object value)
    {
        this(value.getClass());
    }

    public TypeUtil(Type type)
    {
        this.type = type;
    }

    public static TypeUtil nullType()
    {
        return new TypeUtil(null);
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

    public void setType(Type type)
    {
        this.type = type;
    }

    public boolean isNull()
    {
        return type == null;
    }

    public boolean isPrimitive()
    {
        return getAsClass().isPrimitive();
    }

    public boolean isPrimitivePlus()
    {
        return PRIMITIVE_TYPES.contains(getName());
    }

    public boolean isWrapClass()
    {
        try
        {
            return ((Class<?>) getAsClass().getField("TYPE").get(null)).isPrimitive();
        }
        catch (Exception ignored)
        {
        }
        return false;
    }

    public boolean isBuiltInClass()
    {
        return BUILT_IN_CLASS.contains(getName());
    }

    public boolean isString()
    {
        return STRING_TYPES.contains(getName());
    }

    public boolean isNumber()
    {
        return NUMBER_TYPES.contains(getName());
    }

    public boolean isBoolean()
    {
        return BOOLEAN_TYPES.contains(getName());
    }

    public Constructor<?> getConstructor(Class<?>... parameterTypes)
    {
        try
        {
            Constructor<?> constructor = getAsClass().getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            return constructor;
        }
        catch (Exception ignored)
        {
        }
        return null;
    }

    public boolean isMapTypeClass()
    {
        try
        {
            return Map.class.isAssignableFrom(getAsClass()) || getConstructor().newInstance() instanceof Map;
        }
        catch (Exception ignored)
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
        catch (Exception ignored)
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

    public Class<?> getPrimitiveClass()
    {
        try
        {
            return (Class<?>) getAsClass().getField("TYPE").get(null);
        }
        catch (Exception ignored)
        {
        }
        return null;
    }

    public TypeUtil getMapType()
    {
        Type type = getAsType();
        if (type instanceof ParameterizedType)
            return new TypeUtil(((ParameterizedType) type).getActualTypeArguments()[1]);
        return new TypeUtil(Object.class);
    }

    public TypeUtil getListType()
    {
        Type type = getAsType();
        if(type instanceof ParameterizedType)
            return new TypeUtil(((ParameterizedType) type).getActualTypeArguments()[0]);
        return new TypeUtil(Object.class);
    }

    public TypeUtil getArrayType()
    {
        Type type = getAsType();
        if(isClass())
            return new TypeUtil(getAsClass().getComponentType());
        else if(type instanceof GenericArrayType)
            return new TypeUtil(((GenericArrayType) type).getGenericComponentType());
        return new TypeUtil(Object.class);
    }

    public TypeUtil getArrayRealType()
    {
        TypeUtil type = this;
        while (type.isArrayTypeClass())
            type = type.getArrayType();
        return type;
    }

    private static final ArrayList<String> PRIMITIVE_TYPES = new ArrayList<String>()
    {{
        add(String.class.getName());
        add(Boolean.class.getName());
        add(Integer.class.getName());
        add(Short.class.getName());
        add(Long.class.getName());
        add(Float.class.getName());
        add(Double.class.getName());
        add(boolean.class.getName());
        add(int.class.getName());
        add(short.class.getName());
        add(long.class.getName());
        add(float.class.getName());
        add(double.class.getName());
    }};

    private static final ArrayList<String> BUILT_IN_CLASS = new ArrayList<String>()
    {{
        add(StringBuilder.class.getName());
        add(StringBuffer.class.getName());
        add(java.util.Date.class.getName());
        add(java.sql.Date.class.getName());
        add(LsonElement.class.getName());
        add(LsonObject.class.getName());
        add(LsonArray.class.getName());
        add(LsonPrimitive.class.getName());
    }};

    public static final ArrayList<String> STRING_TYPES = new ArrayList<String>()
    {{
        add(String.class.getName());
        add(StringBuilder.class.getName());
        add(StringBuffer.class.getName());
    }};

    public static final ArrayList<String> NUMBER_TYPES = new ArrayList<String>()
    {{
        add(Integer.class.getName());
        add(Short.class.getName());
        add(Long.class.getName());
        add(Float.class.getName());
        add(Double.class.getName());
        add(int.class.getName());
        add(short.class.getName());
        add(long.class.getName());
        add(float.class.getName());
        add(double.class.getName());
    }};

    public static final ArrayList<String> BOOLEAN_TYPES = new ArrayList<String>()
    {{
        add(Boolean.class.getName());
        add(boolean.class.getName());
    }};
}

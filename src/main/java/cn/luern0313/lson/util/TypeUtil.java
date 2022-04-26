package cn.luern0313.lson.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.luern0313.lson.TypeReference;
import cn.luern0313.lson.element.LsonArray;
import cn.luern0313.lson.element.LsonElement;
import cn.luern0313.lson.element.LsonObject;
import cn.luern0313.lson.element.LsonPrimitive;

/**
 * Type或Class的封装类。
 * 2020/09/08
 *
 * @author luern0313
 */

public class TypeUtil
{
    private Type type;

    private final TypeReference<?> typeReference;

    public TypeUtil(Object value)
    {
        this(value != null ? value.getClass() : null);
    }

    public TypeUtil(Type type)
    {
        this(type, null);
    }

    public TypeUtil(Type type, TypeReference<?> typeReference)
    {
        this.type = type;
        this.typeReference = typeReference;
    }

    public static TypeUtil nullType()
    {
        return new TypeUtil(null);
    }

    private TypeUtil extendType(Type type)
    {
        return new TypeUtil(type, typeReference);
    }

    public Type getAsType()
    {
        return type;
    }

    public TypeReference<?> getTypeReference()
    {
        return typeReference;
    }

    public Class<?> getAsClass()
    {
        if(type instanceof ParameterizedType)
            return (Class<?>) ((ParameterizedType) type).getRawType();
        else if(type instanceof TypeVariable)
            return typeReference.typeMap.get(((TypeVariable<?>) type).getName()).rawType;
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
            Constructor<?>[] constructors = getAsClass().getDeclaredConstructors();
            for (Constructor<?> value : constructors)
            {
                Class<?>[] constructorTypes = value.getParameterTypes();
                if(constructorTypes.length == 0 && (parameterTypes == null || parameterTypes.length == 0))
                {
                    value.setAccessible(true);
                    return value;
                }
                else if(constructorTypes.length == parameterTypes.length)
                {
                    boolean flag = false;
                    for (int j = 0; j < parameterTypes.length; j++)
                        if(parameterTypes[j] != constructorTypes[j])
                            if(parameterTypes[j] != null || new TypeUtil(constructorTypes[j]).isWrapClass())
                                flag = true;
                    if(!flag)
                    {
                        value.setAccessible(true);
                        return value;
                    }
                }
            }
        }
        catch (Exception ignored)
        {
        }
        return null;
    }

    public boolean isMapType()
    {
        return Map.class.isAssignableFrom(getAsClass());
    }

    public boolean isListType()
    {
        return List.class.isAssignableFrom(getAsClass());
    }

    public boolean isArrayType()
    {
        return getAsClass().isArray() || getAsType() instanceof GenericArrayType;
    }

    public boolean isSetType()
    {
        return Set.class.isAssignableFrom(getAsClass());
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

    public boolean instantiateAble()
    {
        try
        {
            Class<?> clz = getAsClass();
            if(!clz.isInterface() && !Modifier.isAbstract(clz.getModifiers()))
            {
                clz.getConstructor();
                return true;
            }
        }
        catch (NoSuchMethodException ignored)
        {
        }
        return false;
    }

    public Class<?> getMapType()
    {
        return instantiateAble() ? getAsClass() : LinkedHashMap.class;
    }

    public Class<?> getListType()
    {
        return instantiateAble() ? getAsClass() : ArrayList.class;
    }

    public Class<?> getSetType()
    {
        return instantiateAble() ? getAsClass() : LinkedHashSet.class;
    }

    public TypeUtil getMapElementType()
    {
        Type type = getAsType();
        if (type instanceof ParameterizedType)
            return extendType(((ParameterizedType) type).getActualTypeArguments()[1]);
        else if(type instanceof TypeVariable)
        {
            LinkedHashMap<String, TypeReference<?>> map = typeReference.typeMap.get(((TypeVariable<?>) type).getName()).typeMap;
            if(map.size() > 1)
                return extendType(((TypeReference<?>) map.entrySet().toArray(new Map.Entry[0])[1].getValue()).type);
        }
        return extendType(Object.class);
    }

    public TypeUtil getListElementType()
    {
        Type type = getAsType();
        if(type instanceof ParameterizedType)
            return extendType(((ParameterizedType) type).getActualTypeArguments()[0]);
        else if(type instanceof TypeVariable)
        {
            LinkedHashMap<String, TypeReference<?>> map = typeReference.typeMap.get(((TypeVariable<?>) type).getName()).typeMap;
            if(map.size() > 0)
                return extendType(map.entrySet().iterator().next().getValue().type);
        }
        return extendType(Object.class);
    }

    public TypeUtil getArrayElementType()
    {
        Type type = getAsType();
        if(type instanceof GenericArrayType)
            return extendType(((GenericArrayType) type).getGenericComponentType());
        else
            return extendType(getAsClass().getComponentType());
    }

    public TypeUtil getSetElementType()
    {
        Type type = getAsType();
        if(type instanceof ParameterizedType)
            return extendType(((ParameterizedType) type).getActualTypeArguments()[0]);
        else if(type instanceof TypeVariable)
        {
            LinkedHashMap<String, TypeReference<?>> map = typeReference.typeMap.get(((TypeVariable<?>) type).getName()).typeMap;
            if(map.size() > 0)
                return extendType(map.entrySet().iterator().next().getValue().type);
        }
        return extendType(Object.class);
    }

    public TypeUtil getArrayElementRealType()
    {
        TypeUtil type = this;
        while (type.isArrayType())
            type = type.getArrayElementType();
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

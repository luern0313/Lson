package cn.luern0313.lson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.LinkedHashMap;

/**
 * 使用{@link TypeReference}来处理带有泛型的类。
 *
 * @author luern0313
 */

public class TypeReference<T>
{
    public Type type;
    public Class<?> rawType;

    public LinkedHashMap<String, TypeReference<?>> typeMap;

    public TypeReference()
    {
        Type superClass = getClass().getGenericSuperclass();

        if(superClass instanceof ParameterizedType)
        {
            type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
            handleType();
        }
    }

    public TypeReference(Type type)
    {
        this.type = type;
        handleType();
    }

    private void handleType()
    {
        if(type instanceof ParameterizedType)
        {
            rawType = (Class<?>) ((ParameterizedType) type).getRawType();

            typeMap = new LinkedHashMap<>();
            Type[] parameterizedTypes = ((ParameterizedType) type).getActualTypeArguments();
            TypeVariable<? extends Class<?>>[] typeVariables = rawType.getTypeParameters();
            for (int i = 0; i < parameterizedTypes.length; i++)
                typeMap.put(typeVariables[i].getName(), new TypeReference<>(parameterizedTypes[i]));
        }
        else if(type instanceof Class<?>)
            rawType = (Class<?>) type;
    }
}

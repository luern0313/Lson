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

    public LinkedHashMap<String, TypeParameterized> typeMap = new LinkedHashMap<>();

    public TypeReference()
    {
        Type superClass = getClass().getGenericSuperclass();
        type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        rawType = (Class<?>) ((ParameterizedType) type).getRawType();

        Type[] parameterizedTypes = ((ParameterizedType) type).getActualTypeArguments();
        TypeVariable<? extends Class<?>>[] typeVariables = rawType.getTypeParameters();
        for (int i = 0; i < parameterizedTypes.length; i++)
            typeMap.put(typeVariables[i].getName(), handleType(parameterizedTypes[i]));
    }

    private TypeParameterized handleType(Type parameterizedType)
    {
        if(parameterizedType instanceof ParameterizedType)
        {
            Class<?> nextClass = (Class<?>) ((ParameterizedType) parameterizedType).getRawType();
            TypeParameterized typeParameterized = new TypeParameterized(nextClass);

            Type[] parameterizedTypes = ((ParameterizedType) parameterizedType).getActualTypeArguments();
            TypeVariable<? extends Class<?>>[] typeParameterizedArray = nextClass.getTypeParameters();
            for (int j = 0; j < parameterizedTypes.length; j++)
                typeParameterized.map.put(typeParameterizedArray[j].getName(), handleType(parameterizedTypes[j]));
            return typeParameterized;
        }
        else
            return new TypeParameterized((Class<?>) parameterizedType);
    }

    public static class TypeParameterized
    {
        Class<?> clz;
        LinkedHashMap<String, TypeParameterized> map = new LinkedHashMap<>();

        public TypeParameterized(Class<?> clz)
        {
            this.clz = clz;
        }
    }
}

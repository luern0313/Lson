package cn.luern0313.lson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.luern0313.lson.annotation.LsonAddPrefix;
import cn.luern0313.lson.annotation.LsonAddSuffix;
import cn.luern0313.lson.annotation.LsonDateFormat;
import cn.luern0313.lson.annotation.LsonNumberFormat;
import cn.luern0313.lson.annotation.LsonPath;
import cn.luern0313.lson.annotation.LsonReplaceAll;
import cn.luern0313.lson.element.LsonArray;
import cn.luern0313.lson.element.LsonElement;
import cn.luern0313.lson.element.LsonPrimitive;
import cn.luern0313.lson.exception.LsonInstantiationException;
import cn.luern0313.lson.path.PathParser;
import cn.luern0313.lson.path.PathType;
import cn.luern0313.lson.util.DataProcessUtil;

/**
 * 被 luern0313 创建于 2020/7/28.
 */

public class LsonUtil
{
    private static LsonAnnotationListener lsonAnnotationListener;
    private static TypeReference<?> typeReference;

    private static ArrayList<String> parameterizedTypes = new ArrayList<>();

    /**
     * 将json反序列化为指定的实体类。
     *
     * @param json Lson解析过的json对象。
     * @param clz 要反序列化实体类的Class对象。
     * @param <T> 反序列化为的实体类。
     * @return 返回反序列化后的实体类。
     *
     * @author luern0313
     */
    public static <T> T fromJson(LsonElement json, Class<T> clz)
    {
        return fromJson(json, clz, null, new ArrayList<>());
    }

    /**
     * 将json反序列化为指定的实体类。
     *
     * @param json Lson解析过的json对象。
     * @param typeReference {@link TypeReference}类，用于泛型类的反序列化。
     * @param <T> 反序列化为的实体类。
     * @return 返回反序列化后的实体类。
     *
     * @author luern0313
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromJson(LsonElement json, TypeReference<T> typeReference)
    {
        LsonUtil.typeReference = typeReference;
        LsonUtil.parameterizedTypes.clear();
        return (T) fromJson(json, typeReference.type, null, new ArrayList<>());
    }

    @SuppressWarnings("unchecked")
    private static <T> T fromJson(LsonElement json, Class<T> clz, Object genericSuperclass, ArrayList<Object> rootJsonPath)
    {
        T t = null;
        try
        {
            Constructor<?> constructor1 = LsonUtil.getConstructor(clz);
            if(constructor1 != null)
            {
                constructor1.setAccessible(true);
                t = (T) constructor1.newInstance();
            }
            else
            {
                Constructor<?> constructor2 = LsonUtil.getConstructor(clz, genericSuperclass.getClass());
                constructor2.setAccessible(true);
                t = (T) constructor2.newInstance(genericSuperclass);
            }
        }
        catch (IllegalAccessException | InvocationTargetException | NullPointerException e)
        {
            e.printStackTrace();
        }
        catch (InstantiationException e)
        {
            throw new LsonInstantiationException();
        }

        Field[] fieldArray = clz.getDeclaredFields();
        for (Field field : fieldArray)
        {
            try
            {
                LsonPath path = field.getAnnotation(LsonPath.class);
                if(path != null)
                {
                    Object value = LsonUtil.getValue(json, path.value(), rootJsonPath, field, t);
                    if(value != null)
                    {
                        Annotation[] annotations = field.getAnnotations();
                        for (Annotation annotation : annotations)
                        {
                            LsonDefinedAnnotation lsonDefinedAnnotation = annotation.annotationType().getAnnotation(LsonDefinedAnnotation.class);
                            if(lsonDefinedAnnotation != null && !annotation.annotationType().getName().equals(LsonPath.class.getName()))
                                value = handleAnnotation(value, annotation, lsonDefinedAnnotation, field);
                        }
                        field.setAccessible(true);

                        if(LsonUtil.isArrayTypeClass(value.getClass()))
                        {
                            Object[] finalValue = (Object[]) doubleNumberHandle(value, field.getClass().getComponentType());
                            field.set(t, finalValue);
                        }
                        else if(LsonUtil.isListTypeClass(value.getClass()))
                        {
                            ArrayList<?> finalValue = (ArrayList<?>) doubleNumberHandle(value, field.getGenericType());
                            field.set(t, finalValue);
                        }
                        else
                        {
                            field.set(t, LsonUtil.doubleNumberHandle(value, field.getType()));
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return t;
    }

    private static Object getValue(LsonElement rootJson, String[] pathArray, ArrayList<Object> rootPath, Field field, Object t)
    {
        for (String pathString : pathArray)
        {
            ArrayList<Object> paths = PathParser.parse(pathString);
            Object value = getValue(rootJson, paths, rootPath, field, t);
            if(value != null)
                return value;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Object getValue(LsonElement rootJson, ArrayList<Object> paths, ArrayList<Object> rootPath, Field field, Object t)
    {
        Class<?> fieldType = null;
        if(field != null)
            fieldType = field.getType();
        try
        {
            ArrayList<Object> jsonPaths = (ArrayList<Object>) paths.clone();
            jsonPaths.addAll(0, rootPath);
            LsonElement json = deepCopy(rootJson);
            for (int i = 0; i < jsonPaths.size(); i++)
            {
                Object pathType = jsonPaths.get(i);
                if(pathType instanceof PathType.PathJsonRoot)
                {
                    json = deepCopy(rootJson);
                }
                else if(pathType instanceof PathType.PathPath)
                {
                    if(json.isLsonObject())
                        json = json.getAsLsonObject().get(((PathType.PathPath) pathType).path);
                    else if(json.isLsonArray())
                    {
                        LsonArray temp = new LsonArray();
                        for (int j = 0; j < json.getAsLsonArray().size(); j++)
                        {
                            LsonElement lsonElement = json.getAsLsonArray().get(j);
                            if(lsonElement.isLsonObject())
                                temp.add(lsonElement.getAsLsonObject().get(((PathType.PathPath) pathType).path));
                        }
                        json = temp;
                    }
                }
                else if(pathType instanceof PathType.PathIndex && json.isLsonArray())
                {
                    LsonArray temp = new LsonArray();
                    int start = ((PathType.PathIndex) pathType).start;
                    if(start < 0) start += json.getAsLsonArray().size();
                    int end = ((PathType.PathIndex) pathType).end;
                    if(end < 0) end += json.getAsLsonArray().size();
                    if(((PathType.PathIndex) pathType).step > 0 && end >= start)
                    {
                        for (int j = start; j < Math.min(end, json.getAsLsonArray().size()); j += ((PathType.PathIndex) pathType).step)
                            temp.add(json.getAsLsonArray().get(j));
                    }
                    json = temp;
                }
                else if(pathType instanceof PathType.PathIndexArray && json.isLsonArray())
                {
                    LsonArray temp = new LsonArray();
                    for (int j = 0; j < ((PathType.PathIndexArray) pathType).index.size(); j++)
                    {
                        int index = ((PathType.PathIndexArray) pathType).index.get(j);
                        if(index < 0) index += json.getAsLsonArray().size();
                        temp.add(json.getAsLsonArray().get(index));
                    }
                    json = temp;
                }
                else if(pathType instanceof PathType.PathFilter)
                {
                    if(json.isLsonArray())
                    {
                        PathType.PathFilter filter = (PathType.PathFilter) pathType;
                        LsonArray temp = new LsonArray();
                        ArrayList<Object> root = new ArrayList<>(jsonPaths.subList(0, i));
                        for (int j = 0; j < json.getAsLsonArray().size(); j++)
                        {
                            Object left = getFilterData(filter.left, j, rootJson, root, t);
                            Object right = getFilterData(filter.right, j, rootJson, root, t);
                            if(compare(left, filter.comparator, right))
                                temp.add(json.getAsLsonArray().get(j));
                        }
                        json = temp;
                    }
                }
            }

            if(fieldType == null || BASE_DATA_TYPES.contains(fieldType.getName()))
            {
                return LsonUtil.getJsonPrimitiveData(fieldType, json);
            }
            else if(LsonUtil.isMapTypeClass(fieldType))
            {
                while (json.isLsonArray() && ((LsonArray) json).size() > 0)
                    json = ((LsonArray) json).get(0);
                if(json.isLsonObject())
                {
                    Class<?> valueTypeArgument = LsonUtil.getMapType(field);
                    Map<String, Object> map = new HashMap<>();
                    String[] keys = json.getAsLsonObject().getKeys();
                    if(valueTypeArgument != null && BASE_DATA_TYPES.contains(valueTypeArgument.getName()))
                    {
                        for (String key : keys)
                            map.put(key, LsonUtil.getJsonPrimitiveData(valueTypeArgument, json.getAsLsonObject().get(key)));
                    }
                    else
                    {
                        for (String key : keys)
                        {
                            ArrayList<Object> tempPaths = (ArrayList<Object>) jsonPaths.clone();
                            tempPaths.add(new PathType.PathPath(key));
                            map.put(key, LsonUtil.getClassData(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[1], getMapType(field), rootJson, t, tempPaths));
                        }
                    }

                    for (Object object : map.values().toArray())
                        if(object != null)
                            return map;
                }
            }
            else if(LsonUtil.isArrayTypeClass(fieldType))
            {
                Object array = getArrayData(json, rootJson, field, fieldType, jsonPaths, t);
                for (int i = 0; i < Array.getLength(array); i++)
                    if(Array.get(array, i) != null)
                        return array;
            }
            else if(LsonUtil.isListTypeClass(fieldType))
            {
                List<?> list = (List<?>) getListData(json, rootJson, field, field.getGenericType(), jsonPaths, t);
                for (int i = 0; i < list.size(); i++)
                    if(list.get(i) != null)
                        return list;
            }
            else
            {
                return getClassData(field.getGenericType(), field.getType(), rootJson, t, jsonPaths);
            }
        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Object getArrayData(LsonElement json, LsonElement rootJson, Field field, Class<?> fieldType, ArrayList<Object> jsonPaths, Object t)
    {
        Class<?> actualTypeArgument = getArrayType(fieldType);
        Class<?> realTypeArgument = getArrayRealType(fieldType);
        Object array;
        if(!NUMBER_DATA_TYPES.contains(realTypeArgument.getName()))
            array = Array.newInstance(actualTypeArgument, json.isLsonArray() ? json.getAsLsonArray().size() : 1);
        else if(isArrayTypeClass(actualTypeArgument))
            array = Array.newInstance(double[].class, json.isLsonArray() ? json.getAsLsonArray().size() : 1);
        else
            array = Array.newInstance(double.class, json.isLsonArray() ? json.getAsLsonArray().size() : 1);

        if(BASE_DATA_TYPES.contains(actualTypeArgument.getName()))
        {
            if(json.isLsonArray())
            {
                for (int i = 0; i < json.getAsLsonArray().size(); i++)
                    Array.set(array, i, getJsonPrimitiveData(actualTypeArgument, json.getAsLsonArray().get(i)));
            }
            else
                Array.set(array, 0, getJsonPrimitiveData(actualTypeArgument, json));
        }
        else if(isArrayTypeClass(actualTypeArgument) && json.isLsonArray())
        {
            for (int i = 0; i < json.getAsLsonArray().size(); i++)
            {
                jsonPaths.add(new PathType.PathIndexArray(new ArrayList<>(Collections.singletonList(i))));
                Array.set(array, i, getArrayData(json.getAsLsonArray().get(i), rootJson, field, actualTypeArgument, jsonPaths, t));
                jsonPaths.remove(jsonPaths.size() - 1);
            }
        }
        else
        {
            if(json.isLsonArray())
            {
                for (int i = 0; i < json.getAsLsonArray().size(); i++)
                {
                    ArrayList<Object> tempPaths = (ArrayList<Object>) jsonPaths.clone();
                    tempPaths.add(new PathType.PathIndexArray(new ArrayList<>(Collections.singletonList(i))));
                    Array.set(array, i, LsonUtil.getClassData(((GenericArrayType) field.getGenericType()).getGenericComponentType(), actualTypeArgument, rootJson, t, tempPaths));
                }
            }
            else
                Array.set(array, 0, LsonUtil.getClassData(((GenericArrayType) field.getGenericType()).getGenericComponentType(), fieldType.getComponentType(), rootJson, t, jsonPaths));
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    private static Object getListData(LsonElement json, LsonElement rootJson, Field field, Type fieldType, ArrayList<Object> jsonPaths, Object t)
    {
        Type actualTypeArgument = getListType(fieldType);
        Class<?> actualTypeArgumentClass = getListTypeClass(fieldType);
        ArrayList<Object> list = new ArrayList<>();

        String[] className = (actualTypeArgument != null ? actualTypeArgument : "").toString().split(" ");
        if(actualTypeArgument != null && className.length == 2 && BASE_DATA_TYPES.contains(className[1]))
        {
            if(json.isLsonArray())
            {
                for (int i = 0; i < json.getAsLsonArray().size(); i++)
                    list.add(getJsonPrimitiveData(actualTypeArgumentClass, json.getAsLsonArray().get(i)));
            }
            else
                list.add(getJsonPrimitiveData(actualTypeArgumentClass, json));
        }
        else if(actualTypeArgumentClass != null && isListTypeClass(actualTypeArgumentClass) && json.isLsonArray())
        {
            for (int i = 0; i < json.getAsLsonArray().size(); i++)
            {
                jsonPaths.add(new PathType.PathIndexArray(new ArrayList<>(Collections.singletonList(i))));
                list.add(getListData(json.getAsLsonArray().get(i), rootJson, field, actualTypeArgument, jsonPaths, t));
                jsonPaths.remove(jsonPaths.size() - 1);
            }
        }
        else
        {
            if(json.isLsonArray())
            {
                for (int i = 0; i < json.getAsLsonArray().size(); i++)
                {
                    ArrayList<Object> tempPaths = (ArrayList<Object>) jsonPaths.clone();
                    tempPaths.add(new PathType.PathIndexArray(new ArrayList<>(Collections.singletonList(i))));
                    list.add(getClassData(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0], getListTypeClass(field), rootJson, t, tempPaths));
                }
            }
            else
                list.add(getClassData(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0], getListTypeClass(field), rootJson, t, jsonPaths));
        }
        return list;
    }

    private static Object getFilterData(PathType.PathFilter.PathFilterPart part, int index, LsonElement rootJson, ArrayList<Object> rootPath, Object t)
    {
        Object result = null;
        if(part.mode == PathType.PathFilter.PathFilterPart.FilterPartMode.PATH)
        {
            rootPath.add(new PathType.PathIndexArray(new ArrayList<>(Collections.singletonList(index))));
            result = getValue(rootJson, part.part, rootPath, null, t);
            rootPath.remove(rootPath.size() - 1);

            if(result instanceof Object[])
                result = ((Object[]) result)[0];
        }
        else if(part.mode == PathType.PathFilter.PathFilterPart.FilterPartMode.ARRAY)
            result = part.part;
        else if(part.mode == PathType.PathFilter.PathFilterPart.FilterPartMode.SINGLE)
            result = part.part.get(0);
        return result;
    }

    private static boolean compare(Object left, PathType.PathFilter.FilterComparator comparator, Object right)
    {
        if(comparator == PathType.PathFilter.FilterComparator.EXISTENCE)
        {
            if(left instanceof Boolean)
                return (boolean) left;
            else if(left instanceof String)
                return !((String) left).equals("");
            else if(left instanceof Number)
                return ((Number) left).doubleValue() != 0;
            return left != null;
        }
        if(left != null && right != null)
        {
            if(comparator == PathType.PathFilter.FilterComparator.EQUAL)
                return left == right || left.equals(right);
            else if(comparator == PathType.PathFilter.FilterComparator.NOT_EQUAL)
                return left != right;
            if(left instanceof Number && right instanceof Number)
            {
                if(comparator == PathType.PathFilter.FilterComparator.LESS)
                    return ((Number) left).doubleValue() < ((Number) right).doubleValue();
                else if(comparator == PathType.PathFilter.FilterComparator.LESS_EQUAL)
                    return ((Number) left).doubleValue() <= ((Number) right).doubleValue();
                else if(comparator == PathType.PathFilter.FilterComparator.GREATER)
                    return ((Number) left).doubleValue() > ((Number) right).doubleValue();
                else if(comparator == PathType.PathFilter.FilterComparator.GREATER_EQUAL)
                    return ((Number) left).doubleValue() >= ((Number) right).doubleValue();
            }
        }
        return false;
    }

    private static LsonElement deepCopy(LsonElement lsonElement)
    {
        if(lsonElement.isLsonObject())
            return lsonElement.getAsLsonObject().deepCopy();
        else if(lsonElement.isLsonArray())
            return lsonElement.getAsLsonArray().deepCopy();
        else if(lsonElement.isLsonPrimitive())
            return lsonElement.getAsLsonPrimitive().deepCopy();
        return lsonElement;
    }

    private static Object handleAnnotation(Object value, Annotation annotation, LsonDefinedAnnotation lsonDefinedAnnotation, Field field)
    {
        if(LsonUtil.isArrayTypeClass(value.getClass()) && !lsonDefinedAnnotation.isIgnoreArray())
        {
            for (int i = 0; i < Array.getLength(value); i++)
                Array.set(value, i, handleAnnotation(Array.get(value, i), annotation, lsonDefinedAnnotation, field));
        }
        else if(LsonUtil.isListTypeClass(value.getClass()) && !lsonDefinedAnnotation.isIgnoreArray())
        {
            for (int i = 0; i < ((List<?>) value).size(); i++)
                ((List) value).set(i, handleAnnotation(((List<?>) value).get(i), annotation, lsonDefinedAnnotation, field));
        }
        else
        {
            if(BUILT_IN_ANNOTATION.contains(annotation.annotationType().getName()))
                value = handleBuiltInAnnotation(value, annotation, field);
            else if(lsonAnnotationListener != null)
                value = lsonAnnotationListener.handleAnnotation(value, annotation,field);
        }
        return value;
    }

    private static Object handleBuiltInAnnotation(Object value, Annotation annotation, Field field)
    {
        if(LsonDateFormat.class.getName().equals(annotation.annotationType().getName()))
            return DataProcessUtil.getTime(Integer.parseInt((String) value), ((LsonDateFormat) annotation).value());
        else if(LsonAddPrefix.class.getName().equals(annotation.annotationType().getName()))
            return ((LsonAddPrefix) annotation).value() + value;
        else if(LsonAddSuffix.class.getName().equals(annotation.annotationType().getName()))
            return value + ((LsonAddSuffix) annotation).value();
        else if(LsonNumberFormat.class.getName().equals(annotation.annotationType().getName()))
            return DataProcessUtil.getNumberFormat(value, ((LsonNumberFormat) annotation).digit(), ((LsonNumberFormat) annotation).mode());
        else if(LsonReplaceAll.class.getName().equals(annotation.annotationType().getName()))
        {
            String[] regexArray = ((LsonReplaceAll) annotation).regex();
            String[] replacementArray = ((LsonReplaceAll) annotation).replacement();
            for (int i = 0; i < regexArray.length; i++)
                value = ((String) value).replaceAll(regexArray[i], replacementArray[i]);
            return value;
        }
        return value;
    }

    private static Object getJsonPrimitiveData(Class<?> c, LsonElement json)
    {
        while (json.isLsonArray())
        {
            if(json.getAsLsonArray().size() > 0)
                json = json.getAsLsonArray().get(0);
        }
        if(json.isLsonPrimitive())
            return getJsonPrimitiveData(c, json.getAsLsonPrimitive());
        return null;
    }

    private static Object getJsonPrimitiveData(Class<?> c, LsonPrimitive jsonPrimitive)
    {
        try
        {
            if(c == null)
            {
                if(jsonPrimitive.isBoolean())
                    return jsonPrimitive.getAsBoolean();
                else if(jsonPrimitive.isString())
                    return jsonPrimitive.getAsString();
                else if(jsonPrimitive.isNumber())
                    return jsonPrimitive.getAsDouble();
            }
            else if((c.getName().equals("boolean") || c.getName().equals("java.lang.Boolean")))
                return jsonPrimitive.getAsBoolean();
            else if(c.getName().equals("java.lang.String"))
                return jsonPrimitive.getAsString();
            return jsonPrimitive.getAsDouble();
        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static Object getClassData(Type genericType, Class<?> defType, LsonElement rootJson, Object t, ArrayList<Object> paths)
    {
        if(genericType instanceof TypeVariable)
        {
            parameterizedTypes.add(((TypeVariable<?>) genericType).getName());
            LinkedHashMap<String, TypeReference.TypeParameterized> typeParameterizedMap = (LinkedHashMap<String, TypeReference.TypeParameterized>) typeReference.typeMap.clone();
            for (int i = 0; i < parameterizedTypes.size() - 1; i++)
                typeParameterizedMap = typeParameterizedMap.get(parameterizedTypes.get(i)).map;

            Object result = fromJson(rootJson, typeParameterizedMap.get(parameterizedTypes.get(parameterizedTypes.size() - 1)).clz, t, paths);
            parameterizedTypes.remove(parameterizedTypes.size() - 1);
            return result;
        }
        return fromJson(rootJson, defType, t, paths);
    }

    private static Object doubleNumberHandle(Object value, Type fieldType)
    {
        if(isArrayTypeClass(value.getClass()))
        {
            Object finalValue = Array.newInstance((Class<?>) fieldType, Array.getLength(value));
            for (int i = 0; i < Array.getLength(value); i++)
                Array.set(finalValue, i, doubleNumberHandle(Array.get(value, i), fieldType));
            return finalValue;
        }
        else if(isListTypeClass(value.getClass()))
        {
            Type type = getListType(fieldType);
            ArrayList<Object> finalValue = new ArrayList<>();
            for (int i = 0; i < ((List<?>) value).size(); i++)
                finalValue.add(doubleNumberHandle(((List<?>) value).get(i), type));
            return finalValue;
        }
        else
        {
            String[] typeName = (fieldType != null ? fieldType : "").toString().split(" ");
            if(typeName.length == 2)
            {
                if(value instanceof Double)
                {
                    switch (typeName[1])
                    {
                        case "int":
                        case "java.lang.Integer":
                            return Double.valueOf(String.valueOf(value)).intValue();
                        case "short":
                        case "java.lang.Short":
                            return ((Double) value).shortValue();
                        case "long":
                        case "java.lang.Long":
                            return ((Double) value).longValue();
                        case "float":
                        case "java.lang.Float":
                            return ((Double) value).floatValue();
                    }
                }
                else if(value instanceof String)
                {
                    switch (typeName[1])
                    {
                        case "int":
                        case "java.lang.Integer":
                            return Double.valueOf((String) value).intValue();
                        case "short":
                        case "java.lang.Short":
                            return Double.valueOf((String) value).shortValue();
                        case "long":
                        case "java.lang.Long":
                            return Double.valueOf((String) value).longValue();
                        case "float":
                        case "java.lang.Float":
                            return Double.valueOf((String) value).floatValue();
                    }
                }
            }
        }
        return value;
    }

    /**
     * 程序开始时，通过此方法传入实现{@link LsonAnnotationListener}接口类的实例，自定义注解才可正常运行。
     *
     * @param lsonAnnotationListener 实现{@link LsonAnnotationListener}接口的实例。
     *
     * @author luern0313
     */
    public static void setLsonAnnotationListener(LsonAnnotationListener lsonAnnotationListener)
    {
        LsonUtil.lsonAnnotationListener = lsonAnnotationListener;
    }

    private static Constructor<?> getConstructor(Class<?> clz, Class<?>... parameterTypes)
    {
        try
        {
            return clz.getConstructor(parameterTypes);
        }
        catch (NoSuchMethodException e)
        {
            return null;
        }
    }

    private static boolean isMapTypeClass(Class<?> clz)
    {
        try
        {
            return Map.class.isAssignableFrom(clz) || clz.newInstance() instanceof Map;
        }
        catch (IllegalAccessException | LsonInstantiationException | InstantiationException e)
        {
            return false;
        }
    }

    private static boolean isListTypeClass(Class<?> clz)
    {
        try
        {
            return List.class.isAssignableFrom(clz) || clz.newInstance() instanceof List;
        }
        catch (IllegalAccessException | LsonInstantiationException | InstantiationException e)
        {
            return false;
        }
    }

    private static boolean isArrayTypeClass(Class<?> clz)
    {
        return clz.isArray();
    }

    private static Class<?> getMapType(Field field)
    {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType)
        {
            ParameterizedType pt = (ParameterizedType) genericType;
            if(!(pt.getActualTypeArguments()[1] instanceof TypeVariable))
                return (Class<?>) pt.getActualTypeArguments()[1];
        }
        return null;
    }

    private static Type getListType(Type type)
    {
        if (type instanceof ParameterizedType)
            return ((ParameterizedType) type).getActualTypeArguments()[0];
        return null;
    }

    private static Class<?> getListTypeClass(Field field)
    {
        return getListTypeClass(field.getGenericType());
    }

    private static Class<?> getListTypeClass(Type type)
    {
        if (type instanceof ParameterizedType)
        {
            Type listType = ((ParameterizedType) type).getActualTypeArguments()[0];
            if(listType instanceof ParameterizedType)
                return (Class<?>) ((ParameterizedType) listType).getRawType();
        }
        return null;
    }

    private static Class<?> getArrayType(Class<?> clz)
    {
        return clz.getComponentType();
    }

    private static Class<?> getArrayRealType(Class<?> clz)
    {
        while (isArrayTypeClass(clz))
            clz = getArrayType(clz);
        return clz;
    }

    /**
     * 处理自定义注解相关。
     *
     * @author luern0313
     */
    public interface LsonAnnotationListener
    {
        /**
         * 开发者可以通过重写这个方法处理自定义注解。
         *
         * @param value 处理前的值。
         * @param annotation 开发者自定义的注解实例。
         * @param field 要填充数据的目标变量，你可以获取该变量的类型等。
         * @return 处理完成的值。
         *
         * @author luern0313
         */
        Object handleAnnotation(Object value, Annotation annotation, Field field);
    }

    private static final ArrayList<String> BASE_DATA_TYPES = new ArrayList<String>()
    {{
        add("java.lang.String");
        add("java.lang.Boolean");
        add("java.lang.Integer");
        add("java.lang.Short");
        add("java.lang.Long");
        add("java.lang.Float");
        add("java.lang.Double");
        add("boolean");
        add("int");
        add("short");
        add("long");
        add("float");
        add("double");
    }};

    private static final ArrayList<String> NUMBER_DATA_TYPES = new ArrayList<String>()
    {{
        add("java.lang.Integer");
        add("java.lang.Short");
        add("java.lang.Long");
        add("java.lang.Float");
        add("java.lang.Double");
        add("int");
        add("short");
        add("long");
        add("float");
        add("double");
    }};

    private static final ArrayList<String> BUILT_IN_ANNOTATION = new ArrayList<String>()
    {{
        add(LsonAddPrefix.class.getName());
        add(LsonAddSuffix.class.getName());
        add(LsonDateFormat.class.getName());
        add(LsonNumberFormat.class.getName());
        add(LsonReplaceAll.class.getName());
    }};
}

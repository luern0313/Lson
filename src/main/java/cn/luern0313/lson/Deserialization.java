package cn.luern0313.lson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.luern0313.lson.annotation.LsonDefinedAnnotation;
import cn.luern0313.lson.annotation.field.LsonPath;
import cn.luern0313.lson.annotation.method.LsonCallMethod;
import cn.luern0313.lson.element.LsonArray;
import cn.luern0313.lson.element.LsonElement;
import cn.luern0313.lson.element.LsonNull;
import cn.luern0313.lson.element.LsonObject;
import cn.luern0313.lson.element.LsonPrimitive;
import cn.luern0313.lson.exception.InstantiationException;
import cn.luern0313.lson.path.PathParser;
import cn.luern0313.lson.path.PathType;
import cn.luern0313.lson.util.DataProcessUtil;
import cn.luern0313.lson.util.DeserializationValueUtil;
import cn.luern0313.lson.util.TypeUtil;

/**
 * Lson反序列化相关类。
 *
 * @author luern0313
 */

public class Deserialization
{
    protected static TypeReference<?> typeReference;

    protected static ArrayList<String> parameterizedTypes = new ArrayList<>();

    @SuppressWarnings("unchecked")
    protected static <T> T fromJson(LsonElement json, TypeUtil typeUtil, ArrayList<Object> rootJsonPath, Object genericSuperclass, Class<?>[] parameterTypes, Object[] parameters)
    {
        T t = null;
        try
        {
            Constructor<?> constructor1 = typeUtil.getConstructor(parameterTypes);
            if(constructor1 != null)
                t = (T) constructor1.newInstance(parameters);
            else
            {
                Constructor<?> constructor2 = typeUtil.getConstructor(genericSuperclass.getClass());
                t = (T) constructor2.newInstance(genericSuperclass);
            }
        }
        catch (IllegalAccessException | InvocationTargetException | NullPointerException | java.lang.InstantiationException ignored)
        {
        }

        if(t == null)
            throw new InstantiationException(typeUtil.getName());

        handleMethod(t, LsonCallMethod.CallMethodTiming.BEFORE_DESERIALIZATION);
        return deserialization(json, typeUtil, t, rootJsonPath);
    }

    protected static <T> T fromJson(LsonElement json, T t, ArrayList<Object> rootJsonPath)
    {
        handleMethod(t, LsonCallMethod.CallMethodTiming.BEFORE_DESERIALIZATION);
        return deserialization(json, new TypeUtil(t), t, rootJsonPath);
    }

    private static <T> T deserialization(LsonElement json, TypeUtil clz, T t, ArrayList<Object> rootJsonPath)
    {
        TypeUtil superClass = new TypeUtil(clz.getAsClass().getSuperclass());
        if(superClass.getAsClass() != Object.class)
            deserialization(json, superClass, t, rootJsonPath);

        Field[] fieldArray = clz.getAsClass().getDeclaredFields();
        for (Field field : fieldArray)
        {
            try
            {
                LsonPath path = field.getAnnotation(LsonPath.class);
                if(path != null)
                {
                    String[] pathArray = path.value();
                    if(pathArray.length == 1 && pathArray[0].equals(""))
                    {
                        pathArray[0] = field.getName();
                        String underScoreCase = DataProcessUtil.getUnderScoreCase(field.getName());
                        if(!field.getName().equals(underScoreCase))
                            pathArray = new String[]{field.getName(), underScoreCase};
                    }

                    Object value = getValue(json, pathArray, rootJsonPath, new TypeUtil(path.preClass() == Object.class ? field.getGenericType() : path.preClass()), t);
                    if(value != null && !(value instanceof LsonNull))
                    {
                        TypeUtil valueType = new TypeUtil(field.getGenericType());
                        Annotation[] annotations = field.getAnnotations();
                        for (Annotation annotation : annotations)
                        {
                            LsonDefinedAnnotation lsonDefinedAnnotation = annotation.annotationType().getAnnotation(LsonDefinedAnnotation.class);
                            if(lsonDefinedAnnotation != null && !annotation.annotationType().getName().equals(LsonPath.class.getName()))
                                value = handleAnnotation(value, annotation, lsonDefinedAnnotation, t);
                        }

                        value = finalValueHandle(value, valueType);
                        if(value != null)
                        {
                            field.setAccessible(true);
                            field.set(t, value);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        handleMethod(t, LsonCallMethod.CallMethodTiming.AFTER_DESERIALIZATION);
        return t;
    }

    public static Object getValue(LsonElement rootJson, String[] pathArray, ArrayList<Object> rootPath, TypeUtil fieldType, Object t)
    {
        for (String pathString : pathArray)
        {
            ArrayList<Object> paths = PathParser.parse(pathString);
            Object value = getValue(rootJson, paths, rootPath, fieldType, t);
            if(value != null)
                return value;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Object getValue(LsonElement rootJson, ArrayList<Object> paths, ArrayList<Object> rootPath, TypeUtil fieldType, Object t)
    {
        try
        {
            ArrayList<Object> jsonPaths = (ArrayList<Object>) paths.clone();
            jsonPaths.addAll(0, rootPath);

            LsonElement json = rootJson.deepCopy();
            for (int i = 0; i < jsonPaths.size(); i++)
            {
                Object pathType = jsonPaths.get(i);
                if(pathType instanceof PathType.PathJsonRoot)
                    json = rootJson.deepCopy();
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
                        for (int j = start; j < Math.min(end, json.getAsLsonArray().size()); j += ((PathType.PathIndex) pathType).step)
                            temp.add(json.getAsLsonArray().get(j));
                    if(temp.size() == 1)
                        json = temp.get(0);
                    else
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
                    if(temp.size() == 1)
                        json = temp.get(0);
                    else
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

            if(fieldType.isNull() || fieldType.isPrimitivePlus() || fieldType.getName().equals(Object.class.getName()))
                return getJsonPrimitiveData(json);
            else if(fieldType.isBuiltInClass())
            {
                Object data = getJsonPrimitiveData(json);
                if(data == null)
                    return new DeserializationValueUtil(handleBuiltInClass(json, fieldType), fieldType.getAsClass());
                else
                    return data;
            }
            else
                return getClassData(fieldType, json, rootJson, t, jsonPaths);
        }
        catch (RuntimeException ignored)
        {
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Object getMapData(LsonElement json, LsonElement rootJson, TypeUtil fieldType, ArrayList<Object> jsonPaths, Object t)
    {
        while (json.isLsonArray() && ((LsonArray) json).size() > 0)
            json = ((LsonArray) json).get(0);

        TypeUtil valueTypeArgument = fieldType.getMapType();
        Map<String, Object> map = new LinkedHashMap<>();
        if(json.isLsonObject())
        {
            String[] keys = json.getAsLsonObject().getKeys();
            for (String key : keys)
            {
                if(valueTypeArgument.isPrimitivePlus())
                    map.put(key, getJsonPrimitiveData(json.getAsLsonObject().get(key)));
                else
                {
                    ArrayList<Object> tempPaths = (ArrayList<Object>) jsonPaths.clone();
                    tempPaths.add(new PathType.PathPath(key));
                    map.put(key, getClassData(valueTypeArgument, json.getAsLsonObject().get(key), rootJson, t, tempPaths));
                }
            }
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private static Object getArrayData(LsonElement json, LsonElement rootJson, TypeUtil fieldType, ArrayList<Object> jsonPaths, Object t)
    {
        TypeUtil actualTypeArgument = fieldType.getArrayType();
        TypeUtil realTypeArgument = fieldType.getArrayRealType();
        Object array;
        if(!realTypeArgument.isNumber())
            array = Array.newInstance(actualTypeArgument.getAsClass(), json.isLsonArray() ? json.getAsLsonArray().size() : 1);
        else if(actualTypeArgument.isArrayTypeClass())
            array = Array.newInstance(double[].class, json.isLsonArray() ? json.getAsLsonArray().size() : 1);
        else
            array = Array.newInstance(double.class, json.isLsonArray() ? json.getAsLsonArray().size() : 1);

        if(json.isLsonArray())
        {
            for (int i = 0; i < json.getAsLsonArray().size(); i++)
            {
                LsonElement lsonElement = json.getAsLsonArray().get(i);
                if(actualTypeArgument.isPrimitivePlus())
                    Array.set(array, i, getJsonPrimitiveData(lsonElement));
                else
                {
                    ArrayList<Object> tempPaths = (ArrayList<Object>) jsonPaths.clone();
                    tempPaths.add(new PathType.PathIndexArray(new ArrayList<>(Collections.singletonList(i))));
                    Array.set(array, i, getClassData(actualTypeArgument, lsonElement, rootJson, t, tempPaths));
                }
            }
        }
        else
        {
            if(actualTypeArgument.isPrimitivePlus())
                Array.set(array, 0, getJsonPrimitiveData(json));
            else
                Array.set(array, 0, getClassData(actualTypeArgument, json, rootJson, t, jsonPaths));
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    private static Object getListData(LsonElement json, LsonElement rootJson, TypeUtil fieldType, ArrayList<Object> jsonPaths, Object t)
    {
        TypeUtil actualTypeArgument = fieldType.getListType();
        ArrayList<Object> list = new ArrayList<>();

        if(json.isLsonArray())
        {
            for (int i = 0; i < json.getAsLsonArray().size(); i++)
            {
                LsonElement lsonElement = json.getAsLsonArray().get(i);
                if(actualTypeArgument.isPrimitivePlus())
                    list.add(getJsonPrimitiveData(lsonElement));
                else
                {
                    ArrayList<Object> tempPaths = (ArrayList<Object>) jsonPaths.clone();
                    tempPaths.add(new PathType.PathIndexArray(new ArrayList<>(Collections.singletonList(i))));
                    list.add(getClassData(actualTypeArgument, lsonElement, rootJson, t, tempPaths));
                }
            }
        }
        else if(json.isLsonPrimitive())
            list.add(getJsonPrimitiveData(json));
        else
            list.add(getClassData(actualTypeArgument, json, rootJson, t, jsonPaths));
        return list;
    }

    private static Object getFilterData(PathType.PathFilter.PathFilterPart part, int index, LsonElement rootJson, ArrayList<Object> rootPath, Object t)
    {
        Object result = null;
        if(part.mode == PathType.PathFilter.PathFilterPart.FilterPartMode.PATH)
        {
            rootPath.add(new PathType.PathIndexArray(new ArrayList<>(Collections.singletonList(index))));
            result = finalValueHandle(getValue(rootJson, part.part, rootPath, TypeUtil.nullType(), t), TypeUtil.nullType());
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
                return !left.equals("");
            else if(left instanceof Number)
                return ((Number) left).doubleValue() != 0;
            return left != null;
        }
        if(left != null && right != null)
        {
            if(comparator == PathType.PathFilter.FilterComparator.EQUAL)
            {
                if(left instanceof Number && right instanceof Number)
                    return ((Number) left).doubleValue() == ((Number) right).doubleValue();
                return left == right || left.equals(right);
            }
            else if(comparator == PathType.PathFilter.FilterComparator.NOT_EQUAL)
            {
                if(left instanceof Number && right instanceof Number)
                    return ((Number) left).doubleValue() != ((Number) right).doubleValue();
                return left != right;
            }
            else if(left instanceof Number && right instanceof Number)
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

    @SuppressWarnings("unchecked")
    private static <T> Object handleAnnotation(Object value, Annotation annotation, LsonDefinedAnnotation lsonDefinedAnnotation, T t)
    {
        if(value == null) return null;

        TypeUtil valueClass = new TypeUtil(value.getClass());
        if(valueClass.isListTypeClass())
        {
            if(lsonDefinedAnnotation.isIgnoreList())
                value = handleAnnotation(finalValueHandle(value, TypeUtil.nullType()), annotation, lsonDefinedAnnotation, t);
            else
                for (int i = 0; i < ((List<?>) value).size(); i++)
                    ((List<Object>) value).set(i, handleAnnotation(((List<?>) value).get(i), annotation, lsonDefinedAnnotation, t));
        }
        else if(valueClass.isListTypeClass() && !lsonDefinedAnnotation.isIgnoreMap())
        {
            if(lsonDefinedAnnotation.isIgnoreMap())
                value = handleAnnotation(finalValueHandle(value, TypeUtil.nullType()), annotation, lsonDefinedAnnotation, t);
            else
            {
                Object[] keys = ((Map<?, ?>) value).keySet().toArray();
                for (Object key : keys)
                    ((Map<Object, Object>) value).put(key, handleAnnotation(((Map<?, ?>) value).get(key), annotation, lsonDefinedAnnotation, t));
            }
        }
        else
        {
            if(value instanceof DeserializationValueUtil)
            {
                Object object = handleAnnotationType((DeserializationValueUtil) value, lsonDefinedAnnotation.acceptableDeserializationType());
                ((DeserializationValueUtil) value).set(handleSingleAnnotation(object, annotation, lsonDefinedAnnotation, t));
            }
            else
                value = handleSingleAnnotation(value, annotation, lsonDefinedAnnotation, t);
        }
        return value;
    }

    private static Object handleAnnotationType(DeserializationValueUtil deserializationValueUtil, LsonDefinedAnnotation.AcceptableType acceptableType)
    {
        switch (acceptableType)
        {
            case STRING:
                return deserializationValueUtil.getAsStringBuilder();
            case NUMBER:
                return deserializationValueUtil.getAsNumber();
            case BOOLEAN:
                return deserializationValueUtil.getAsBoolean();
        }
        return deserializationValueUtil.get();
    }

    private static <T> Object handleSingleAnnotation(Object value, Annotation annotation, LsonDefinedAnnotation lsonDefinedAnnotation, T t)
    {
        try
        {
            Method method = lsonDefinedAnnotation.config().getDeclaredMethod("deserialization", Object.class, Annotation.class, Object.class);
            return method.invoke(lsonDefinedAnnotation.config().newInstance(), value, annotation, t);
        }
        catch (NoSuchMethodException | InvocationTargetException | java.lang.InstantiationException | IllegalAccessException ignored)
        {
        }
        return null;
    }

    private static void handleMethod(Object t, LsonCallMethod.CallMethodTiming callMethodTiming)
    {
        if(t != null)
        {
            Class<?> clz = t.getClass();
            Method[] methods = clz.getDeclaredMethods();
            for (Method method : methods)
            {
                try
                {
                    LsonCallMethod lsonCallMethod = method.getAnnotation(LsonCallMethod.class);
                    if(lsonCallMethod != null && DataProcessUtil.getIndex(callMethodTiming, lsonCallMethod.timing()) > -1)
                    {
                        method.setAccessible(true);
                        method.invoke(t);
                    }
                }
                catch (RuntimeException | IllegalAccessException | InvocationTargetException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private static DeserializationValueUtil getJsonPrimitiveData(LsonElement json)
    {
        while (json.isLsonArray())
            json = json.getAsLsonArray().get(0);
        if(json.isLsonPrimitive())
            return new DeserializationValueUtil(json.getAsLsonPrimitive().get(), json.getAsLsonPrimitive().getValueClass());
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Object getClassData(TypeUtil fieldType, LsonElement json, LsonElement rootJson, Object t, ArrayList<Object> paths)
    {
        if(fieldType == null) return null;

        if(fieldType.getAsType() instanceof TypeVariable)
        {
            parameterizedTypes.add(((TypeVariable<?>) fieldType.getAsType()).getName());
            LinkedHashMap<String, TypeReference.TypeParameterized> typeParameterizedMap = (LinkedHashMap<String, TypeReference.TypeParameterized>) typeReference.typeMap.clone();
            for (int i = 0; i < parameterizedTypes.size() - 1; i++)
                typeParameterizedMap = typeParameterizedMap.get(parameterizedTypes.get(i)).map;

            Object result = fromJson(rootJson, new TypeUtil(typeParameterizedMap.get(parameterizedTypes.get(parameterizedTypes.size() - 1)).clz), paths, t, null, null);
            parameterizedTypes.remove(parameterizedTypes.size() - 1);
            return result;
        }
        else if(fieldType.isMapTypeClass())
        {
            Map<String, ?> map = (Map<String, ?>) getMapData(json, rootJson, fieldType, paths, t);
            for (Object object : map.values().toArray())
                if(object != null)
                    return map;
        }
        else if(fieldType.isArrayTypeClass())
        {
            Object array = getArrayData(json, rootJson, fieldType, paths, t);
            for (int i = 0; i < Array.getLength(array); i++)
                if(Array.get(array, i) != null)
                    return array;
        }
        else if(fieldType.isListTypeClass())
        {
            List<?> list = (List<?>) getListData(json, rootJson, fieldType, paths, t);
            for (int i = 0; i < list.size(); i++)
                if(list.get(i) != null)
                    return list;
        }
        return fromJson(rootJson, fieldType, paths, t, null, null);
    }

    private static Object handleBuiltInClass(Object value, TypeUtil fieldType)
    {
        TypeUtil valueType = new TypeUtil(value);
        if(fieldType.getName().equals(StringBuilder.class.getName()))
            return new StringBuilder(value.toString());
        else if(fieldType.getName().equals(StringBuffer.class.getName()))
            return new StringBuffer(value.toString());
        else if(fieldType.getName().equals(java.util.Date.class.getName()))
        {
            if(valueType.isNumber())
                return new java.util.Date(((Number) value).longValue());
            else if(valueType.isString())
                return new java.util.Date(Long.parseLong(value.toString()));
        }
        else if(fieldType.getName().equals(java.sql.Date.class.getName()))
        {
            if(valueType.isNumber())
                return new java.sql.Date(((Number) value).longValue());
            else if(valueType.isString())
                return new java.sql.Date(Long.parseLong(value.toString()));
        }
        else if(fieldType.getName().equals(LsonElement.class.getName()))
            return value;
        else if(fieldType.getName().equals(LsonObject.class.getName()))
            return ((LsonElement) value).getAsLsonObject();
        else if(fieldType.getName().equals(LsonArray.class.getName()))
            return ((LsonElement) value).getAsLsonArray();
        else if(fieldType.getName().equals(LsonPrimitive.class.getName()))
            return ((LsonElement) value).getAsLsonPrimitive();
        return value;
    }

    public static Object finalValueHandle(Object value, TypeUtil fieldType)
    {
        try
        {
            if(value == null) return null;

            TypeUtil valueClass = new TypeUtil(value.getClass());
            if(valueClass.isArrayTypeClass())
            {
                Object finalValue = Array.newInstance(fieldType.getArrayType().getAsClass(), Array.getLength(value));
                for (int i = 0; i < Array.getLength(value); i++)
                    Array.set(finalValue, i, finalValueHandle(Array.get(value, i), fieldType.getArrayType()));
                return finalValue;
            }
            else if(valueClass.isListTypeClass())
            {
                TypeUtil type = fieldType.getListType();
                ArrayList<Object> finalValue = new ArrayList<>();
                for (int i = 0; i < ((List<?>) value).size(); i++)
                    finalValue.add(finalValueHandle(((List<?>) value).get(i), type));
                return finalValue;
            }
            else if(valueClass.isMapTypeClass())
            {
                TypeUtil type = fieldType.getMapType();
                Map<String, Object> finalValue = new LinkedHashMap<>();
                for (Object key : ((Map<?, ?>) value).keySet().toArray())
                    finalValue.put((String) key, finalValueHandle(((Map<?, ?>) value).get(key), type));
                return finalValue;
            }
            else if(fieldType.isBuiltInClass() && value instanceof DeserializationValueUtil)
                return handleBuiltInClass(((DeserializationValueUtil) value).get(), fieldType);
            else if(value instanceof DeserializationValueUtil)
            {
                if(((DeserializationValueUtil) value).getCurrentType() == Double.class)
                    return finalValueHandle((DeserializationValueUtil) value, fieldType);
                else if(((DeserializationValueUtil) value).getCurrentType() == StringBuilder.class)
                    return ((DeserializationValueUtil) value).get().toString();
                else if(((DeserializationValueUtil) value).getCurrentType() == Boolean.class)
                    return ((DeserializationValueUtil) value).get();
                else
                    return ((DeserializationValueUtil) value).get(fieldType);
            }
            else
                return value;
        }
        catch (RuntimeException ignored)
        {
        }
        return null;
    }

    private static Object finalValueHandle(DeserializationValueUtil value, TypeUtil fieldType)
    {
        if(!fieldType.isNull())
        {
            switch (fieldType.getName())
            {
                case "int":
                case "java.lang.Integer":
                    return ((Number) value.get()).intValue();
                case "short":
                case "java.lang.Short":
                    return ((Number) value.get()).shortValue();
                case "long":
                case "java.lang.Long":
                    return ((Number) value.get()).longValue();
                case "float":
                case "java.lang.Float":
                    return ((Number) value.get()).floatValue();
                case "java.lang.String":
                    return finalValueHandle(value, TypeUtil.nullType()).toString();
            }
        }
        else
        {
            switch (value.getType().getName())
            {
                case "int":
                case "java.lang.Integer":
                    return ((Number) value.get()).intValue();
                case "short":
                case "java.lang.Short":
                    return ((Number) value.get()).shortValue();
                case "long":
                case "java.lang.Long":
                    return ((Number) value.get()).longValue();
                case "float":
                case "java.lang.Float":
                    return ((Number) value.get()).floatValue();
                case "java.lang.String":
                    return value.get().toString();
            }
        }
        return value.get();
    }
}

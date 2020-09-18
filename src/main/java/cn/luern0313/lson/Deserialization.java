package cn.luern0313.lson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.luern0313.lson.annotation.field.LsonAddPrefix;
import cn.luern0313.lson.annotation.field.LsonAddSuffix;
import cn.luern0313.lson.annotation.field.LsonDateFormat;
import cn.luern0313.lson.annotation.field.LsonNumberFormat;
import cn.luern0313.lson.annotation.field.LsonPath;
import cn.luern0313.lson.annotation.field.LsonReplaceAll;
import cn.luern0313.lson.annotation.method.LsonCallMethod;
import cn.luern0313.lson.element.LsonArray;
import cn.luern0313.lson.element.LsonElement;
import cn.luern0313.lson.element.LsonObject;
import cn.luern0313.lson.element.LsonPrimitive;
import cn.luern0313.lson.exception.LsonInstantiationException;
import cn.luern0313.lson.path.PathParser;
import cn.luern0313.lson.path.PathType;
import cn.luern0313.lson.util.DataProcessUtil;
import cn.luern0313.lson.util.DeserializationStringUtil;
import cn.luern0313.lson.util.TypeUtil;

/**
 * Lson反序列化相关类。
 *
 * @author luern0313
 */

public class Deserialization
{
    protected static Deserialization.LsonAnnotationListener lsonAnnotationListener;
    protected static TypeReference<?> typeReference;

    protected static ArrayList<String> parameterizedTypes = new ArrayList<>();

    @SuppressWarnings("unchecked")
    protected static <T> T fromJson(LsonElement json, TypeUtil clz, Object genericSuperclass, ArrayList<Object> rootJsonPath)
    {
        T t = null;
        try
        {
            Constructor<?> constructor1 = getConstructor(clz);
            if(constructor1 != null)
                t = (T) constructor1.newInstance();
            else
            {
                Constructor<?> constructor2 = getConstructor(clz, genericSuperclass.getClass());
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

        handleMethod(t, LsonCallMethod.CallMethodTiming.BEFORE_DESERIALIZATION);
        return deserialization(json, clz, t, rootJsonPath);
    }

    @SuppressWarnings("unchecked")
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

                    Object value = getValue(json, pathArray, rootJsonPath, new TypeUtil(field.getGenericType()), t);
                    if(value != null)
                    {
                        TypeUtil valueType = new TypeUtil(field.getGenericType());
                        Annotation[] annotations = field.getAnnotations();
                        for (Annotation annotation : annotations)
                        {
                            LsonDefinedAnnotation lsonDefinedAnnotation = annotation.annotationType().getAnnotation(LsonDefinedAnnotation.class);
                            if(lsonDefinedAnnotation != null && !annotation.annotationType().getName().equals(LsonPath.class.getName()))
                                value = handleAnnotation(value, annotation, lsonDefinedAnnotation, valueType);
                        }

                        field.setAccessible(true);

                        if(isArrayTypeClass(valueType))
                        {
                            Object[] finalValue = (Object[]) finalValueHandle(value, valueType);
                            field.set(t, finalValue);
                        }
                        else if(isListTypeClass(valueType))
                        {
                            ArrayList<?> finalValue = (ArrayList<?>) finalValueHandle(value, valueType);
                            field.set(t, finalValue);
                        }
                        else if(isMapTypeClass(valueType))
                        {
                            Map<String, ?> finalValue = (Map<String, ?>) finalValueHandle(value, valueType);
                            field.set(t, finalValue);
                        }
                        else
                            field.set(t, finalValueHandle(value, valueType));
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

    private static Object getValue(LsonElement rootJson, String[] pathArray, ArrayList<Object> rootPath, TypeUtil fieldType, Object t)
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
            if(rootPath != null)
                jsonPaths.addAll(0, rootPath);
            LsonElement json = deepCopy(rootJson);
            for (int i = 0; i < jsonPaths.size(); i++)
            {
                Object pathType = jsonPaths.get(i);
                if(pathType instanceof PathType.PathJsonRoot)
                    json = deepCopy(rootJson);
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

            if(fieldType == null || BASE_DATA_TYPES.contains(fieldType.getName()) || fieldType.getName().equals(Object.class.getName()))
                return getJsonPrimitiveData(fieldType, json);
            else if(BUILT_IN_CLASS.contains(fieldType.getName()))
            {
                Object data = getJsonPrimitiveData(fieldType, json);
                if(data == null)
                    return handleBuiltInClass(json, fieldType);
                else
                    return data;
            }
            else
                return getClassData(fieldType, json, rootJson, t, jsonPaths);
        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Object getMapData(LsonElement json, LsonElement rootJson, TypeUtil fieldType, ArrayList<Object> jsonPaths, Object t)
    {
        while (json.isLsonArray() && ((LsonArray) json).size() > 0)
            json = ((LsonArray) json).get(0);

        if(json.isLsonObject())
        {
            TypeUtil valueTypeArgument = getMapType(fieldType);
            Map<String, Object> map = new LinkedHashMap<>();
            String[] keys = json.getAsLsonObject().getKeys();

            if(BASE_DATA_TYPES.contains(valueTypeArgument.getName()))
                for (String key : keys)
                    map.put(key, getJsonPrimitiveData(valueTypeArgument, json.getAsLsonObject().get(key)));
            else
            {
                for (String key : keys)
                {
                    ArrayList<Object> tempPaths = (ArrayList<Object>) jsonPaths.clone();
                    tempPaths.add(new PathType.PathPath(key));
                    map.put(key, getClassData(valueTypeArgument, json.getAsLsonObject().get(key), rootJson, t, tempPaths));
                }
            }
            return map;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Object getArrayData(LsonElement json, LsonElement rootJson, TypeUtil fieldType, ArrayList<Object> jsonPaths, Object t)
    {
        TypeUtil actualTypeArgument = getArrayType(fieldType);
        TypeUtil realTypeArgument = getArrayRealType(fieldType);
        Object array;
        if(!NUMBER_DATA_TYPES.contains(realTypeArgument.getName()))
            array = Array.newInstance(actualTypeArgument.getAsClass(), json.isLsonArray() ? json.getAsLsonArray().size() : 1);
        else if(isArrayTypeClass(actualTypeArgument))
            array = Array.newInstance(double[].class, json.isLsonArray() ? json.getAsLsonArray().size() : 1);
        else
            array = Array.newInstance(double.class, json.isLsonArray() ? json.getAsLsonArray().size() : 1);

        if(BASE_DATA_TYPES.contains(actualTypeArgument.getName()))
        {
            if(json.isLsonArray())
                for (int i = 0; i < json.getAsLsonArray().size(); i++)
                    Array.set(array, i, getJsonPrimitiveData(actualTypeArgument, json.getAsLsonArray().get(i)));
            else
                Array.set(array, 0, getJsonPrimitiveData(actualTypeArgument, json));
        }
        else
        {
            if(json.isLsonArray())
            {
                for (int i = 0; i < json.getAsLsonArray().size(); i++)
                {
                    ArrayList<Object> tempPaths = (ArrayList<Object>) jsonPaths.clone();
                    tempPaths.add(new PathType.PathIndexArray(new ArrayList<>(Collections.singletonList(i))));
                    Array.set(array, i, getClassData(actualTypeArgument, json.getAsLsonArray().get(i), rootJson, t, tempPaths));
                }
            }
            else
                Array.set(array, 0, getClassData(actualTypeArgument, json, rootJson, t, jsonPaths));
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    private static Object getListData(LsonElement json, LsonElement rootJson, TypeUtil fieldType, ArrayList<Object> jsonPaths, Object t)
    {
        TypeUtil actualTypeArgument = getListType(fieldType);
        ArrayList<Object> list = new ArrayList<>();

        if(BASE_DATA_TYPES.contains(actualTypeArgument.getName()))
        {
            if(json.isLsonArray())
                for (int i = 0; i < json.getAsLsonArray().size(); i++)
                    list.add(getJsonPrimitiveData(actualTypeArgument, json.getAsLsonArray().get(i)));
            else
                list.add(getJsonPrimitiveData(actualTypeArgument, json));
        }
        else
        {
            if(json.isLsonArray())
            {
                for (int i = 0; i < json.getAsLsonArray().size(); i++)
                {
                    ArrayList<Object> tempPaths = (ArrayList<Object>) jsonPaths.clone();
                    tempPaths.add(new PathType.PathIndexArray(new ArrayList<>(Collections.singletonList(i))));
                    list.add(getClassData(actualTypeArgument, json.getAsLsonArray().get(i), rootJson, t, tempPaths));
                }
            }
            else
                list.add(getClassData(actualTypeArgument, json, rootJson, t, jsonPaths));
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
                return !left.equals("");
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

    @SuppressWarnings("unchecked")
    private static Object handleAnnotation(Object value, Annotation annotation, LsonDefinedAnnotation lsonDefinedAnnotation, TypeUtil fieldClass)
    {
        TypeUtil valueClass = new TypeUtil(value.getClass());
        if(isArrayTypeClass(valueClass) && !lsonDefinedAnnotation.isIgnoreArray())
            for (int i = 0; i < Array.getLength(value); i++)
                Array.set(value, i, handleAnnotation(Array.get(value, i), annotation, lsonDefinedAnnotation, getArrayType(fieldClass)));
        else if(isListTypeClass(valueClass) && !lsonDefinedAnnotation.isIgnoreList())
            for (int i = 0; i < ((List<?>) value).size(); i++)
                ((List<Object>) value).set(i, handleAnnotation(((List<?>) value).get(i), annotation, lsonDefinedAnnotation, getListType(fieldClass)));
        else if(isMapTypeClass(valueClass) && !lsonDefinedAnnotation.isIgnoreMap())
        {
            Object[] keys = ((Map<?, ?>) value).keySet().toArray();
            for (Object key : keys)
                ((Map<Object, Object>) value).put(key, handleAnnotation(((Map<?, ?>) value).get(key), annotation, lsonDefinedAnnotation, getMapType(fieldClass)));
        }
        else
        {
            if((lsonDefinedAnnotation.applyTypeBlackList().length == 0 || DataProcessUtil.getIndex(fieldClass.getAsClass(), lsonDefinedAnnotation.applyTypeBlackList()) == -1) &&
                    (lsonDefinedAnnotation.applyTypeWhiteList().length == 0 || DataProcessUtil.getIndex(fieldClass.getAsClass(), lsonDefinedAnnotation.applyTypeWhiteList()) != -1))
            {
                if(BUILT_IN_ANNOTATION.contains(annotation.annotationType().getName()))
                    value = handleBuiltInAnnotation(value, annotation, fieldClass);
                else if(lsonAnnotationListener != null)
                    value = lsonAnnotationListener.handleAnnotation(value, annotation, fieldClass);
            }
        }
        return value;
    }

    private static Object handleBuiltInAnnotation(Object value, Annotation annotation, TypeUtil fieldType)
    {
        if(LsonDateFormat.class.getName().equals(annotation.annotationType().getName()))
            return new DeserializationStringUtil(DataProcessUtil.getTime(Long.parseLong(value.toString()) * (((LsonDateFormat) annotation).mode() == LsonDateFormat.LsonDateFormatMode.SECOND ? 1000 : 0), ((LsonDateFormat) annotation).value()));
        else if(LsonAddPrefix.class.getName().equals(annotation.annotationType().getName()))
        {
            ((DeserializationStringUtil) value).stringBuilder.insert(0, ((LsonAddPrefix) annotation).value());
            return value;
        }
        else if(LsonAddSuffix.class.getName().equals(annotation.annotationType().getName()))
        {
            ((DeserializationStringUtil) value).stringBuilder.append(((LsonAddSuffix) annotation).value());
            return value;
        }
        else if(LsonNumberFormat.class.getName().equals(annotation.annotationType().getName()))
            return DataProcessUtil.getNumberFormat(value, ((LsonNumberFormat) annotation).digit(), ((LsonNumberFormat) annotation).mode(), fieldType);
        else if(LsonReplaceAll.class.getName().equals(annotation.annotationType().getName()))
        {
            String[] regexArray = ((LsonReplaceAll) annotation).regex();
            String[] replacementArray = ((LsonReplaceAll) annotation).replacement();
            for (int i = 0; i < regexArray.length; i++)
                DataProcessUtil.replaceAll(((DeserializationStringUtil) value).stringBuilder, regexArray[i], replacementArray[i]);
            return value;
        }
        return value;
    }

    private static <T> void handleMethod(T t, LsonCallMethod.CallMethodTiming callMethodTiming)
    {
        Class<?> clz = t.getClass();
        Method[] methods = clz.getDeclaredMethods();
        for (Method method : methods)
        {
            try
            {
                LsonCallMethod lsonCallMethod = method.getAnnotation(LsonCallMethod.class);
                if(lsonCallMethod != null && DataProcessUtil.getIndex(callMethodTiming, lsonCallMethod.timing()) != -1)
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

    private static Object getJsonPrimitiveData(TypeUtil type, LsonElement json)
    {
        while (json.isLsonArray())
            if(json.getAsLsonArray().size() > 0)
                json = json.getAsLsonArray().get(0);
        if(json.isLsonPrimitive())
            return getJsonPrimitiveData(type, json.getAsLsonPrimitive());
        return null;
    }

    private static Object getJsonPrimitiveData(TypeUtil type, LsonPrimitive jsonPrimitive)
    {
        try
        {
            if(type == null)
            {
                if(jsonPrimitive.isBoolean())
                    return jsonPrimitive.getAsBoolean();
                else if(jsonPrimitive.isString())
                    return new DeserializationStringUtil(jsonPrimitive.getAsString(), jsonPrimitive.getValueClass());
                else if(jsonPrimitive.isNumber())
                    return jsonPrimitive.getAsDouble();
            }
            else if((type.getName().equals("boolean") || type.getName().equals("java.lang.Boolean")))
                return jsonPrimitive.getAsBoolean();
            else if(NUMBER_DATA_TYPES.contains(type.getName()))
                return jsonPrimitive.getAsDouble();
            else if(STRING_DATA_TYPES.contains(type.getName()))
                return new DeserializationStringUtil(jsonPrimitive.getAsString(), jsonPrimitive.getValueClass());
            else
                return getJsonPrimitiveData(null, jsonPrimitive);
        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Object getClassData(TypeUtil fieldType, LsonElement json, LsonElement rootJson, Object t, ArrayList<Object> paths)
    {
        if(fieldType.getAsType() instanceof TypeVariable)
        {
            parameterizedTypes.add(((TypeVariable<?>) fieldType.getAsType()).getName());
            LinkedHashMap<String, TypeReference.TypeParameterized> typeParameterizedMap = (LinkedHashMap<String, TypeReference.TypeParameterized>) typeReference.typeMap.clone();
            for (int i = 0; i < parameterizedTypes.size() - 1; i++)
                typeParameterizedMap = typeParameterizedMap.get(parameterizedTypes.get(i)).map;

            Object result = fromJson(rootJson, new TypeUtil(typeParameterizedMap.get(parameterizedTypes.get(parameterizedTypes.size() - 1)).clz), t, paths);
            parameterizedTypes.remove(parameterizedTypes.size() - 1);
            return result;
        }
        else if(isMapTypeClass(fieldType))
        {
            Map<String, ?> map = (Map<String, ?>) getMapData(json, rootJson, fieldType, paths, t);
            for (Object object : map.values().toArray())
                if(object != null)
                    return map;
        }
        else if(isArrayTypeClass(fieldType))
        {
            Object array = getArrayData(json, rootJson, fieldType, paths, t);
            for (int i = 0; i < Array.getLength(array); i++)
                if(Array.get(array, i) != null)
                    return array;
        }
        else if(isListTypeClass(fieldType))
        {
            List<?> list = (List<?>) getListData(json, rootJson, fieldType, paths, t);
            for (int i = 0; i < list.size(); i++)
                if(list.get(i) != null)
                    return list;
        }
        return fromJson(rootJson, fieldType, t, paths);
    }

    private static Object handleBuiltInClass(Object value, TypeUtil fieldType)
    {
        if(fieldType.getName().equals(StringBuilder.class.getName()))
            return new StringBuilder(value.toString());
        else if(fieldType.getName().equals(StringBuffer.class.getName()))
            return new StringBuffer(value.toString());
        else if(fieldType.getName().equals(java.util.Date.class.getName()))
        {
            if(value instanceof Number)
                return new java.util.Date(((Number) value).longValue());
            else if(STRING_DATA_TYPES.contains(value.getClass().getName()))
                return new java.util.Date(Long.parseLong(value.toString()));
        }
        else if(fieldType.getName().equals(java.sql.Date.class.getName()))
        {
            if(value instanceof Number)
                return new java.sql.Date(((Number) value).longValue());
            else if(STRING_DATA_TYPES.contains(value.getClass().getName()))
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

    private static Object finalValueHandle(Object value, TypeUtil fieldType)
    {
        TypeUtil valueClass = new TypeUtil(value.getClass());
        if(isArrayTypeClass(valueClass))
        {
            Object finalValue = Array.newInstance(getArrayType(fieldType).getAsClass(), Array.getLength(value));
            for (int i = 0; i < Array.getLength(value); i++)
                Array.set(finalValue, i, finalValueHandle(Array.get(value, i), getArrayType(fieldType)));
            return finalValue;
        }
        else if(isListTypeClass(valueClass))
        {
            TypeUtil type = getListType(fieldType);
            ArrayList<Object> finalValue = new ArrayList<>();
            for (int i = 0; i < ((List<?>) value).size(); i++)
                finalValue.add(finalValueHandle(((List<?>) value).get(i), type));
            return finalValue;
        }
        else if(isMapTypeClass(valueClass))
        {
            TypeUtil type = getMapType(fieldType);
            Map<String, Object> finalValue = new LinkedHashMap<>();
            for (Object key : ((Map<?, ?>) value).keySet().toArray())
                finalValue.put((String) key, finalValueHandle(((Map<?, ?>) value).get(key), type));
            return finalValue;
        }
        else if(BUILT_IN_CLASS.contains(fieldType.getName()))
            return handleBuiltInClass(value, fieldType);
        else
        {
            if(value instanceof Double)
                value = finalValueHandle((Double) value, fieldType);
            else if(value instanceof DeserializationStringUtil)
                value = value.toString();
        }
        return value;
    }

    private static Object finalValueHandle(Double number, TypeUtil fieldType)
    {
        switch (fieldType.getName())
        {
            case "int":
            case "java.lang.Integer":
                return number.intValue();
            case "short":
            case "java.lang.Short":
                return number.shortValue();
            case "long":
            case "java.lang.Long":
                return number.longValue();
            case "float":
            case "java.lang.Float":
                return number.floatValue();
            case "java.lang.String":
                return number.toString();
        }
        return number;
    }

    private static Constructor<?> getConstructor(TypeUtil clz, Class<?>... parameterTypes)
    {
        try
        {
            Constructor<?> constructor = clz.getAsClass().getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            return constructor;
        }
        catch (RuntimeException | NoSuchMethodException e)
        {
            return null;
        }
    }

    private static boolean isMapTypeClass(TypeUtil type)
    {
        try
        {
            return Map.class.isAssignableFrom(type.getAsClass()) || getConstructor(type).newInstance() instanceof Map;
        }
        catch (IllegalAccessException | LsonInstantiationException | InstantiationException | InvocationTargetException | NullPointerException | ClassCastException ignored)
        {
        }
        return false;
    }

    private static boolean isListTypeClass(TypeUtil type)
    {
        try
        {
            return List.class.isAssignableFrom(type.getAsClass()) || getConstructor(type).newInstance() instanceof List;
        }
        catch (IllegalAccessException | LsonInstantiationException | InstantiationException | InvocationTargetException | NullPointerException | ClassCastException ignored)
        {
        }
        return false;
    }

    private static boolean isArrayTypeClass(TypeUtil type)
    {
        if(type.isClass())
            return type.getAsClass().isArray();
        return type.getAsType() instanceof GenericArrayType;
    }

    private static TypeUtil getMapType(TypeUtil type)
    {
        Type t = type.getAsType();
        if (t instanceof ParameterizedType)
            t = ((ParameterizedType) t).getActualTypeArguments()[1];
        return new TypeUtil(t);
    }

    private static TypeUtil getListType(TypeUtil type)
    {
        Type t = type.getAsType();
        if(t instanceof ParameterizedType)
            t = ((ParameterizedType) t).getActualTypeArguments()[0];
        return new TypeUtil(t);
    }

    private static Class<?> getArrayType(Class<?> clz)
    {
        return clz.getComponentType();
    }

    private static TypeUtil getArrayType(TypeUtil type)
    {
        Type t = type.getAsType();
        if(type.isClass())
            t = getArrayType(type.getAsClass());
        else if(t instanceof GenericArrayType)
            t = ((GenericArrayType) t).getGenericComponentType();
        return new TypeUtil(t);
    }

    private static TypeUtil getArrayRealType(TypeUtil type)
    {
        while (isArrayTypeClass(type))
            type = getArrayType(type);
        return type;
    }

    /**
     * 自定义注解相关。
     *
     * @author luern0313
     */
    public interface LsonAnnotationListener
    {
        /**
         * 开发者可以通过重写这个方法在反序列化中处理自定义注解。
         *
         * @param value 处理前的值。
         * @param annotation 开发者自定义的注解实例。
         * @param fieldType 要填充数据的目标变量的类型。
         * @return 处理完成的值。
         *
         * @author luern0313
         */
        Object handleAnnotation(Object value, Annotation annotation, TypeUtil fieldType);
    }

    private static final ArrayList<String> BASE_DATA_TYPES = new ArrayList<String>()
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

    private static final ArrayList<String> NUMBER_DATA_TYPES = new ArrayList<String>()
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

    private static final ArrayList<String> STRING_DATA_TYPES = new ArrayList<String>()
    {{
        add(String.class.getName());
        add(StringBuilder.class.getName());
        add(StringBuffer.class.getName());
    }};

    private static final ArrayList<String> BUILT_IN_ANNOTATION = new ArrayList<String>()
    {{
        add(LsonAddPrefix.class.getName());
        add(LsonAddSuffix.class.getName());
        add(LsonDateFormat.class.getName());
        add(LsonNumberFormat.class.getName());
        add(LsonReplaceAll.class.getName());
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
}

package cn.luern0313.lson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.luern0313.lson.annotation.LsonDefinedAnnotation;
import cn.luern0313.lson.annotation.field.LsonPath;
import cn.luern0313.lson.annotation.method.LsonCallMethod;
import cn.luern0313.lson.element.LsonArray;
import cn.luern0313.lson.element.LsonElement;
import cn.luern0313.lson.element.LsonObject;
import cn.luern0313.lson.element.LsonPrimitive;
import cn.luern0313.lson.path.PathParser;
import cn.luern0313.lson.path.PathType;
import cn.luern0313.lson.util.DataProcessUtil;
import cn.luern0313.lson.util.DeserializationValueUtil;
import cn.luern0313.lson.util.TypeUtil;

/**
 * Lson序列化相关类。
 *
 *
 * @author luern0313
 */

public class Serialization
{
    @SuppressWarnings("unchecked")
    public static LsonElement toJson(Object object)
    {
        try
        {
            if(object == null) return null;

            TypeUtil typeUtil = new TypeUtil(object.getClass());
            if(typeUtil.isPrimitivePlus())
                return new LsonPrimitive(object);
            else if(typeUtil.isArrayType())
                return arrayToJson(object);
            else if(typeUtil.isListType())
                return listToJson((List<?>) object);
            else if(typeUtil.isMapType())
                return mapToJson((Map<String, ?>) object);
            else if(typeUtil.isSetType())
                return setToJson((Set<?>) object);
            else if(typeUtil.isBuiltInClass())
                return builtInClassToJson(object, typeUtil);
            return classToJson(object, typeUtil);
        }
        catch (RuntimeException ignored)
        {
        }
        return null;
    }

    private static LsonElement arrayToJson(Object array)
    {
        LsonArray lsonArray = new LsonArray();
        for (int i = 0; i < Array.getLength(array); i++)
            lsonArray.add(toJson(Array.get(array, i)));
        return lsonArray;
    }

    private static LsonElement listToJson(List<?> list)
    {
        LsonArray lsonArray = new LsonArray();
        for (int i = 0; i < list.size(); i++)
            lsonArray.add(toJson(list.get(i)));
        return lsonArray;
    }

    private static LsonElement mapToJson(Map<String, ?> map)
    {
        LsonObject lsonObject = new LsonObject();
        String[] keys = map.keySet().toArray(new String[0]);
        for (String key : keys)
            lsonObject.put(key, toJson(map.get(key)));
        return lsonObject;
    }

    private static LsonElement setToJson(Set<?> set)
    {
        LsonArray lsonArray = new LsonArray();
        for (Object o : set)
            lsonArray.add(toJson(o));
        return lsonArray;
    }

    private static LsonElement builtInClassToJson(Object object, TypeUtil typeUtil)
    {
        if(typeUtil.getName().equals(StringBuilder.class.getName()))
            return new LsonPrimitive(object.toString());
        else if(typeUtil.getName().equals(StringBuffer.class.getName()))
            return new LsonPrimitive(object.toString());
        else if(typeUtil.getName().equals(java.util.Date.class.getName()))
            return new LsonPrimitive(((java.util.Date) object).getTime());
        else if(typeUtil.getName().equals(java.sql.Date.class.getName()))
            return new LsonPrimitive(((java.util.Date) object).getTime());
        else if(typeUtil.getName().equals(LsonElement.class.getName()))
            return (LsonElement) object;
        else if(typeUtil.getName().equals(LsonObject.class.getName()))
            return ((LsonElement) object).getAsLsonObject();
        else if(typeUtil.getName().equals(LsonArray.class.getName()))
            return ((LsonElement) object).getAsLsonArray();
        else if(typeUtil.getName().equals(LsonPrimitive.class.getName()))
            return ((LsonElement) object).getAsLsonPrimitive();
        return null;
    }

    private static LsonElement classToJson(Object object, TypeUtil typeUtil)
    {
        handleMethod(object, LsonCallMethod.CallMethodTiming.AFTER_SERIALIZATION);
        LsonObject lsonObject = new LsonObject();
        Field[] fields = typeUtil.getAsClass().getDeclaredFields();
        for (Field field : fields)
        {
            try
            {
                LsonPath path = field.getAnnotation(LsonPath.class);
                if(path != null)
                {
                    String[] pathArray = path.value();
                    if(pathArray.length == 1 && pathArray[0].equals(""))
                        pathArray[0] = DataProcessUtil.getUnderScoreCase(field.getName());
                    field.setAccessible(true);
                    LsonElement fieldElement = toJson(field.get(object));
                    Object value = getDeserializationValue(fieldElement);

                    Annotation[] annotations = field.getAnnotations();
                    for (int i = annotations.length - 1; i >= 0; i--)
                    {
                        LsonDefinedAnnotation lsonDefinedAnnotation = annotations[i].annotationType().getAnnotation(LsonDefinedAnnotation.class);
                        if(lsonDefinedAnnotation != null && !annotations[i].annotationType().getName().equals(LsonPath.class.getName()))
                            value = handleAnnotation(value, annotations[i], lsonDefinedAnnotation, object);
                    }

                    value = finalValueHandle(value);
                    if(value != null)
                        setValue((LsonElement) value, pathArray[0], new ArrayList<>(), lsonObject);
                }
            }
            catch (RuntimeException | IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
        handleMethod(object, LsonCallMethod.CallMethodTiming.BEFORE_SERIALIZATION);
        return lsonObject;
    }

    private static Object getDeserializationValue(LsonElement lsonElement)
    {
        if(lsonElement != null && lsonElement.isLsonPrimitive())
            return new DeserializationValueUtil(lsonElement.getAsLsonPrimitive().get(), lsonElement.getAsLsonPrimitive().get().getClass());
        else if(lsonElement != null && lsonElement.isLsonArray())
        {
            ArrayList<Object> list = new ArrayList<>();
            for (int i = 0; i < lsonElement.getAsLsonArray().size(); i++)
                list.add(getDeserializationValue(lsonElement.getAsLsonArray().get(i)));
            return list;
        }
        else if(lsonElement != null && lsonElement.isLsonObject())
        {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            String[] keys = lsonElement.getAsLsonObject().getKeys();
            for (String key : keys)
                map.put(key, getDeserializationValue(lsonElement.getAsLsonObject().get(key)));
            return map;
        }
        return new DeserializationValueUtil();
    }

    @SuppressWarnings("unchecked")
    public static void setValue(LsonElement value, String pathString, ArrayList<String> rootPath, LsonElement rootJson)
    {
        ArrayList<Object> jsonPaths = PathParser.parse(pathString);
        jsonPaths.addAll(0, rootPath);

        ArrayList<LsonElement> jsonTempArrayList = new ArrayList<>(Collections.singletonList(rootJson));
        for (int i = 0; i < jsonPaths.size(); i++)
        {
            Object pathType = jsonPaths.get(i);
            ArrayList<LsonElement> jsonArrayList = (ArrayList<LsonElement>) jsonTempArrayList.clone();
            jsonTempArrayList.clear();
            for (int j = 0; j < jsonArrayList.size(); j++)
            {
                LsonElement json = jsonArrayList.get(j);
                if(json == null) continue;

                if(pathType instanceof PathType.PathJsonRoot)
                    jsonTempArrayList.add(j, rootJson);
                else if(pathType instanceof PathType.PathJsonCurrent)
                    jsonTempArrayList.add(j, rootJson);
                else if(pathType instanceof PathType.PathPath)
                {
                    if(i < jsonPaths.size() -1)
                    {
                        if(json.isLsonObject())
                        {
                            Object nextPath = jsonPaths.get(i + 1);
                            if(nextPath instanceof PathType.PathIndex || nextPath instanceof PathType.PathIndexArray || nextPath instanceof PathType.PathFilter)
                                jsonTempArrayList.add(json.getAsLsonObject().hasPut(((PathType.PathPath) pathType).path, LsonArray.class));
                            else
                                jsonTempArrayList.add(json.getAsLsonObject().hasPut(((PathType.PathPath) pathType).path, LsonObject.class));
                        }
                        else if(json.isLsonArray())
                            for (int k = 0; k < value.getAsLsonArray().size(); k++)
                                jsonTempArrayList.add(json.getAsLsonObject().hasPut(((PathType.PathPath) pathType).path, LsonObject.class));
                    }
                    else
                    {
                        if(jsonArrayList.size() > 1 && value.isLsonArray())
                            for (int k = 0; k < jsonArrayList.size(); k++)
                                jsonArrayList.get(k).getAsLsonObject().put(((PathType.PathPath) pathType).path, value.getAsLsonArray().get(k));
                        else if(value.isLsonArray() && value.getAsLsonArray().size() == 1)
                            jsonArrayList.get(0).getAsLsonObject().put(((PathType.PathPath) pathType).path, value.getAsLsonArray().get(0));
                        else
                            jsonArrayList.get(0).getAsLsonObject().put(((PathType.PathPath) pathType).path, value);
                    }
                }
                else if(pathType instanceof PathType.PathIndexArray && json.isLsonArray())
                {
                    if(i < jsonPaths.size() - 1)
                    {
                        for (int k = 0; k < ((PathType.PathIndexArray) pathType).index.size(); k++)
                            if(((PathType.PathIndexArray) pathType).index.get(k) instanceof Integer)
                                jsonTempArrayList.add(json.getAsLsonArray().hasSet((int) ((PathType.PathIndexArray) pathType).index.get(k), LsonObject.class));
                    }
                    else
                    {
                        for (int k = 0; k < jsonArrayList.size(); k++)
                        {
                            LsonElement lsonElement = jsonArrayList.get(k);
                            if(lsonElement.isLsonArray())
                                for (int l = 0; l < ((PathType.PathIndexArray) pathType).index.size(); l++)
                                    if(((PathType.PathIndexArray) pathType).index.get(l) instanceof Integer)
                                        lsonElement.getAsLsonArray().set((int) ((PathType.PathIndexArray) pathType).index.get(l), value);
                        }
                    }
                }
                else if(pathType instanceof PathType.PathIndex && json.isLsonArray())
                {
                    int start = ((PathType.PathIndex) pathType).start;
                    if(start < 0) start += json.getAsLsonArray().size();
                    int end = ((PathType.PathIndex) pathType).end;
                    if(end < 0) end += json.getAsLsonArray().size();
                    if(((PathType.PathIndex) pathType).step > 0 && end >= start)
                    {
                        for (int k = start; k < Math.min(end, start + value.getAsLsonArray().size()); k += ((PathType.PathIndex) pathType).step)
                        {
                            if(i < jsonPaths.size() - 1)
                                jsonTempArrayList.add(json.getAsLsonArray().hasSet(k, LsonObject.class));
                            else
                            {
                                for (int l = 0; l < jsonArrayList.size(); l++)
                                {
                                    LsonElement lsonElement = jsonArrayList.get(l);
                                    if(lsonElement.isLsonArray())
                                        lsonElement.getAsLsonArray().set(k, value);
                                }
                            }
                        }
                    }
                }
                else if(pathType instanceof PathType.PathFilter && json.isLsonArray())
                {
                    if(i < jsonPaths.size() - 1)
                        for (int k = 0; k < value.getAsLsonArray().size(); k++)
                            jsonTempArrayList.add(json.getAsLsonArray().add(new LsonObject()));
                    else
                    {
                        for (int k = 0; k < jsonArrayList.size(); k++)
                        {
                            LsonElement lsonElement = jsonArrayList.get(k);
                            if(lsonElement.isLsonArray())
                                lsonElement.getAsLsonArray().add(value);
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static Object handleAnnotation(Object value, Annotation annotation, LsonDefinedAnnotation lsonDefinedAnnotation, Object object)
    {
        if(value == null) return null;

        TypeUtil valueClass = new TypeUtil(value.getClass());
        if(valueClass.isArrayType() && !lsonDefinedAnnotation.isIgnoreArray())
            for (int i = 0; i < Array.getLength(value); i++)
                Array.set(value, i, handleAnnotation(Array.get(value, i), annotation, lsonDefinedAnnotation, object));
        else if(valueClass.isListType() && !lsonDefinedAnnotation.isIgnoreList())
            for (int i = 0; i < ((List<?>) value).size(); i++)
                ((List<Object>) value).set(i, handleAnnotation(((List<?>) value).get(i), annotation, lsonDefinedAnnotation, object));
        else if(valueClass.isListType() && !lsonDefinedAnnotation.isIgnoreMap())
        {
            Object[] keys = ((Map<?, ?>) value).keySet().toArray();
            for (Object key : keys)
                ((Map<Object, Object>) value).put(key, handleAnnotation(((Map<?, ?>) value).get(key), annotation, lsonDefinedAnnotation, object));
        }
        else if(value instanceof DeserializationValueUtil)
        {
            Object o = handleAnnotationType((DeserializationValueUtil) value, lsonDefinedAnnotation.acceptableSerializationType());
            if(o != null)
            {
                Object result = handleSingleAnnotation(o, annotation, lsonDefinedAnnotation, object);
                TypeUtil resultType = new TypeUtil(result);
                if(resultType.isPrimitivePlus() || resultType.isBuiltInClass())
                    ((DeserializationValueUtil) value).set(handleSingleAnnotation(o, annotation, lsonDefinedAnnotation, object));
                else
                    value = result;
            }

            if(value instanceof DeserializationValueUtil && !((DeserializationValueUtil) value).isNull())
                ((DeserializationValueUtil) value).set(handleAnnotationType((DeserializationValueUtil) value, lsonDefinedAnnotation.acceptableDeserializationType()));
            else if(value instanceof DeserializationValueUtil)
                return null;
        }
        else
            value = handleSingleAnnotation(value, annotation, lsonDefinedAnnotation, object);
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

    private static Object handleSingleAnnotation(Object value, Annotation annotation, LsonDefinedAnnotation lsonDefinedAnnotation, Object object)
    {
        try
        {
            Method method = lsonDefinedAnnotation.config().getDeclaredMethod("serialization", Object.class, Object.class, Object.class);
            return method.invoke(lsonDefinedAnnotation.config().newInstance(), value, annotation, object);
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

    private static LsonElement finalValueHandle(Object value)
    {
        try
        {
            if(value == null) return null;

            TypeUtil valueClass = new TypeUtil(value.getClass());
            if(valueClass.isArrayType())
            {
                LsonArray finalValue = new LsonArray();
                for (int i = 0; i < Array.getLength(value); i++)
                    finalValue.add(finalValueHandle(Array.get(value, i)));
                return finalValue;
            }
            else if(valueClass.isListType())
            {
                LsonArray finalValue = new LsonArray();
                for (int i = 0; i < ((List<?>) value).size(); i++)
                    finalValue.add(finalValueHandle(((List<?>) value).get(i)));
                return finalValue;
            }
            else if(valueClass.isMapType())
            {
                LsonObject finalValue = new LsonObject();
                for (Object key : ((Map<?, ?>) value).keySet().toArray())
                    finalValue.put((String) key, finalValueHandle(((Map<?, ?>) value).get(key)));
                return finalValue;
            }
            else if(value instanceof DeserializationValueUtil)
            {
                if(((DeserializationValueUtil) value).get() instanceof Double)
                    return new LsonPrimitive(finalValueHandle((DeserializationValueUtil) value));
                else if(((DeserializationValueUtil) value).get() instanceof StringBuilder)
                    return new LsonPrimitive(((DeserializationValueUtil) value).get().toString());
                else
                    return new LsonPrimitive(((DeserializationValueUtil) value).get());
            }
            else
                return new LsonPrimitive(value);
        }
        catch (RuntimeException ignored)
        {
        }
        return null;
    }

    private static Object finalValueHandle(DeserializationValueUtil value)
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
        }
        return value.get();
    }
}

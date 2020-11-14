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

import cn.luern0313.lson.annotation.LsonDefinedAnnotation;
import cn.luern0313.lson.annotation.field.LsonAddPrefix;
import cn.luern0313.lson.annotation.field.LsonAddSuffix;
import cn.luern0313.lson.annotation.field.LsonBooleanFormatAsNumber;
import cn.luern0313.lson.annotation.field.LsonBooleanFormatAsString;
import cn.luern0313.lson.annotation.field.LsonDateFormat;
import cn.luern0313.lson.annotation.field.LsonNumberFormat;
import cn.luern0313.lson.annotation.field.LsonPath;
import cn.luern0313.lson.annotation.field.LsonReplaceAll;
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
 * @author luern0313
 */

public class Serialization
{
    @SuppressWarnings("unchecked")
    public static LsonElement toJson(Object object)
    {
        TypeUtil typeUtil = new TypeUtil(object.getClass());
        if(typeUtil.isPrimitivePlus())
            return new LsonPrimitive(object);
        else if(typeUtil.isArrayTypeClass())
            return arrayToJson(object);
        else if(typeUtil.isListTypeClass())
            return listToJson((List<?>) object);
        else if(typeUtil.isMapTypeClass())
            return mapToJson((Map<String, ?>) object);
        else if(typeUtil.isBuiltInClass())
            return builtInClassToJson(object, typeUtil);
        return classToJson(object, typeUtil);
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

                    TypeUtil valueType = new TypeUtil(field.getGenericType());
                    Annotation[] annotations = field.getAnnotations();
                    for (int i = annotations.length - 1; i >= 0; i--)
                    {
                        LsonDefinedAnnotation lsonDefinedAnnotation = annotations[i].annotationType().getAnnotation(LsonDefinedAnnotation.class);
                        if(lsonDefinedAnnotation != null && !annotations[i].annotationType().getName().equals(LsonPath.class.getName()))
                            value = handleAnnotation(value, annotations[i], lsonDefinedAnnotation, valueType);
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
        if(lsonElement.isLsonPrimitive())
            return new DeserializationValueUtil(lsonElement.getAsLsonPrimitive().get(), lsonElement.getAsLsonPrimitive().get().getClass());
        else if(lsonElement.isLsonArray())
        {
            ArrayList<Object> list = new ArrayList<>();
            for (int i = 0; i < lsonElement.getAsLsonArray().size(); i++)
                list.add(getDeserializationValue(lsonElement.getAsLsonArray().get(i)));
            return list;
        }
        else if(lsonElement.isLsonObject())
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
        for (int i = 0; i < jsonPaths.size() - 1; i++)
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
                else if(pathType instanceof PathType.PathPath && json.isLsonObject())
                {
                    Object nextPath = jsonPaths.get(i + 1);
                    if(nextPath instanceof PathType.PathIndex || nextPath instanceof PathType.PathIndexArray || nextPath instanceof PathType.PathFilter)
                        jsonTempArrayList.add(json.getAsLsonObject().hasPut(((PathType.PathPath) pathType).path, LsonArray.class));
                    else
                        jsonTempArrayList.add(json.getAsLsonObject().hasPut(((PathType.PathPath) pathType).path, LsonObject.class));
                }
                else if(pathType instanceof PathType.PathPath && json.isLsonArray())
                    for (int k = 0; k < value.getAsLsonArray().size(); k++)
                        jsonTempArrayList.add(json.getAsLsonObject().hasPut(((PathType.PathPath) pathType).path, LsonObject.class));
                else if(pathType instanceof PathType.PathIndexArray && json.isLsonArray())
                    for (int k = 0; k < ((PathType.PathIndexArray) pathType).index.size(); k++)
                        jsonTempArrayList.add(json.getAsLsonArray().hasSet(((PathType.PathIndexArray) pathType).index.get(k), LsonObject.class));
                else if(pathType instanceof PathType.PathIndex && json.isLsonArray())
                {
                    int start = ((PathType.PathIndex) pathType).start;
                    if(start < 0) start += json.getAsLsonArray().size();
                    int end = ((PathType.PathIndex) pathType).end;
                    if(end < 0) end += json.getAsLsonArray().size();
                    if(((PathType.PathIndex) pathType).step > 0 && end >= start)
                        for (int k = start; k < Math.min(end, start + value.getAsLsonArray().size()); k += ((PathType.PathIndex) pathType).step)
                            jsonTempArrayList.add(json.getAsLsonArray().hasSet(k, LsonObject.class));
                }
                else if(pathType instanceof PathType.PathFilter && json.isLsonArray())
                    for (int k = 0; k < value.getAsLsonArray().size(); k++)
                        jsonTempArrayList.add(json.getAsLsonArray().add(new LsonObject()));
            }
        }

        if(jsonTempArrayList.size() > 1 && value.isLsonArray())
            for (int i = 0; i < jsonTempArrayList.size(); i++)
                jsonTempArrayList.get(i).getAsLsonObject().put(((PathType.PathPath) jsonPaths.get(jsonPaths.size() - 1)).path, value.getAsLsonArray().get(i));
        else if(value.isLsonArray() && value.getAsLsonArray().size() == 1)
            jsonTempArrayList.get(0).getAsLsonObject().put(((PathType.PathPath) jsonPaths.get(jsonPaths.size() - 1)).path, value.getAsLsonArray().get(0));
        else
            jsonTempArrayList.get(0).getAsLsonObject().put(((PathType.PathPath) jsonPaths.get(jsonPaths.size() - 1)).path, value);
    }

    @SuppressWarnings("unchecked")
    private static Object handleAnnotation(Object value, Annotation annotation, LsonDefinedAnnotation lsonDefinedAnnotation, TypeUtil fieldClass)
    {
        if(value == null) return null;

        TypeUtil valueClass = new TypeUtil(value.getClass());
        if(valueClass.isArrayTypeClass() && !lsonDefinedAnnotation.isIgnoreArray())
            for (int i = 0; i < Array.getLength(value); i++)
                Array.set(value, i, handleAnnotation(Array.get(value, i), annotation, lsonDefinedAnnotation, fieldClass.getArrayType()));
        else if(valueClass.isListTypeClass() && !lsonDefinedAnnotation.isIgnoreList())
            for (int i = 0; i < ((List<?>) value).size(); i++)
                ((List<Object>) value).set(i, handleAnnotation(((List<?>) value).get(i), annotation, lsonDefinedAnnotation, fieldClass.getListType()));
        else if(valueClass.isListTypeClass() && !lsonDefinedAnnotation.isIgnoreMap())
        {
            Object[] keys = ((Map<?, ?>) value).keySet().toArray();
            for (Object key : keys)
                ((Map<Object, Object>) value).put(key, handleAnnotation(((Map<?, ?>) value).get(key), annotation, lsonDefinedAnnotation, fieldClass.getMapType()));
        }
        else
        {
            if(value instanceof DeserializationValueUtil)
            {
                Object object = handleAnnotationType((DeserializationValueUtil) value, lsonDefinedAnnotation.acceptableSerializationType());
                if(object != null && LsonUtil.BUILT_IN_ANNOTATION.contains(annotation.annotationType().getName()))
                    ((DeserializationValueUtil) value).set(handleBuiltInAnnotation(object, annotation, fieldClass));
                else if(object != null)
                    ((DeserializationValueUtil) value).set(LsonUtil.lsonAnnotationListener.handleDeserializationAnnotation(object, annotation, fieldClass));

                if(!((DeserializationValueUtil) value).isNull())
                    ((DeserializationValueUtil) value).set(handleAnnotationType((DeserializationValueUtil) value, lsonDefinedAnnotation.acceptableDeserializationType()));
                else
                    return null;
            }
            else if(LsonUtil.BUILT_IN_ANNOTATION.contains(annotation.annotationType().getName()))
                value = handleBuiltInAnnotation(value, annotation, fieldClass);
            else if(LsonUtil.lsonAnnotationListener != null)
                value = LsonUtil.lsonAnnotationListener.handleDeserializationAnnotation(value, annotation, fieldClass);
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

    private static Object handleBuiltInAnnotation(Object value, Annotation annotation, TypeUtil fieldType)
    {
        try
        {
            if(LsonDateFormat.class.getName().equals(annotation.annotationType().getName()))
                return DataProcessUtil.getTimeStamp(value.toString(), ((LsonDateFormat) annotation).value(), ((LsonDateFormat) annotation).mode());
            else if(LsonAddPrefix.class.getName().equals(annotation.annotationType().getName()))
            {
                if(((StringBuilder) value).indexOf(((LsonAddPrefix) annotation).value()) == 0)
                    return ((StringBuilder) value).delete(0, ((LsonAddPrefix) annotation).value().length());
            }
            else if(LsonAddSuffix.class.getName().equals(annotation.annotationType().getName()))
            {
                if(((StringBuilder) value).lastIndexOf(((LsonAddSuffix) annotation).value()) == ((StringBuilder) value).length() - ((LsonAddSuffix) annotation).value().length())
                    return ((StringBuilder) value).delete(((StringBuilder) value).length() - ((LsonAddSuffix) annotation).value().length(), ((StringBuilder) value).length());
            }
            else if(LsonNumberFormat.class.getName().equals(annotation.annotationType().getName()))
                return value;
            else if(LsonReplaceAll.class.getName().equals(annotation.annotationType().getName()))
            {
                String[] regexArray = ((LsonReplaceAll) annotation).regex();
                String[] replacementArray = ((LsonReplaceAll) annotation).replacement();
                for (int i = 0; i < regexArray.length; i++)
                    DataProcessUtil.replaceAll((StringBuilder) value, replacementArray[i], regexArray[i]);
                return value;
            }
            else if(LsonBooleanFormatAsNumber.class.getName().equals(annotation.annotationType().getName()))
                return null;
            else if(LsonBooleanFormatAsString.class.getName().equals(annotation.annotationType().getName()))
                return null;
        }
        catch (RuntimeException ignored)
        {
        }
        return value;
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
            if(valueClass.isListTypeClass())
            {
                LsonArray finalValue = new LsonArray();
                for (int i = 0; i < ((List<?>) value).size(); i++)
                    finalValue.add(finalValueHandle(((List<?>) value).get(i)));
                return finalValue;
            }
            else if(valueClass.isMapTypeClass())
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

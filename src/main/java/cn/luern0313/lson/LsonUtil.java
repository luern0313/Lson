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
    private static TypeReference typeReference;

    private static ArrayList<String> parameterizedTypes = new ArrayList<>();

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

    private static final ArrayList<String> BUILT_IN_ANNOTATION = new ArrayList<String>()
    {{
        add(LsonAddPrefix.class.getName());
        add(LsonAddSuffix.class.getName());
        add(LsonDateFormat.class.getName());
        add(LsonNumberFormat.class.getName());
        add(LsonReplaceAll.class.getName());
    }};

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
    public static  <T> T fromJson(LsonObjectUtil json, Class<T> clz)
    {
        return fromJson(json, clz, null, new ArrayList<>());
    }

    /**
     * 将json反序列化为指定的实体类。
     *
     * @param json Lson解析过的json对象。
     * @param typeReference 要反序列化实体类的Class对象。
     * @param <T> 反序列化为的实体类。
     * @return 返回反序列化后的实体类。
     *
     * @author luern0313
     */
    public static <T> T fromJson(LsonObjectUtil json, TypeReference<T> typeReference)
    {
        LsonUtil.typeReference = typeReference;
        LsonUtil.parameterizedTypes.clear();
        return (T) fromJson(json, typeReference.type, null, new ArrayList<>());
    }

    private static  <T> T fromJson(Object json, Class<T> clz, Object genericSuperclass, ArrayList<Object> rootJsonPath)
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
                            {
                                if(LsonUtil.isArrayTypeClass(value.getClass()) && !lsonDefinedAnnotation.isIgnoreArray())
                                {
                                    for (int i = 0; i < ((Object[]) value).length; i++)
                                    {
                                        if(BUILT_IN_ANNOTATION.contains(annotation.annotationType().getName()))
                                            ((Object[]) value)[i] = handleBuiltInAnnotation(((Object[]) value)[i], annotation, field);
                                        else if(lsonAnnotationListener != null)
                                            ((Object[]) value)[i] = lsonAnnotationListener.handleAnnotation(((Object[]) value)[i], annotation, field);
                                    }
                                }
                                else if(LsonUtil.isListTypeClass(value.getClass()) && !lsonDefinedAnnotation.isIgnoreArray())
                                {
                                    for (int i = 0; i < ((List<Object>) value).size(); i++)
                                    {
                                        if(BUILT_IN_ANNOTATION.contains(annotation.annotationType().getName()))
                                            ((List<Object>) value).set(i, handleBuiltInAnnotation(((List<Object>) value).get(i), annotation, field));
                                        else if(lsonAnnotationListener != null)
                                            ((List<Object>) value).set(i, lsonAnnotationListener.handleAnnotation(((List<Object>) value).get(i), annotation, field));
                                    }
                                }
                                else
                                {
                                    if(BUILT_IN_ANNOTATION.contains(annotation.annotationType().getName()))
                                        value = handleBuiltInAnnotation(value, annotation, field);
                                    else if(lsonAnnotationListener != null)
                                        value = lsonAnnotationListener.handleAnnotation(value, annotation,field);
                                }
                            }
                        }
                        field.setAccessible(true);

                        if(LsonUtil.isArrayTypeClass(value.getClass()))
                        {
                            Object[] finalValue;
                            finalValue = (Object[]) Array.newInstance(field.getType().getComponentType(), ((Object[]) value).length);
                            for (int i = 0; i < ((Object[]) value).length; i++)
                                finalValue[i] = LsonUtil.doubleNumberHandle(((Object[]) value)[i], LsonUtil.getArrayType(field.getType()));
                            field.set(t, finalValue);
                        }
                        else if(LsonUtil.isListTypeClass(value.getClass()))
                        {
                            ArrayList<Object> finalValue = new ArrayList<>();
                            for (int i = 0; i < ((List<Object>) value).size(); i++)
                                finalValue.add(i, LsonUtil.doubleNumberHandle(((List<Object>) value).get(i), LsonUtil.getListType(field)));
                            field.set(t, finalValue);
                        }
                        else
                        {
                            field.set(t, LsonUtil.doubleNumberHandle(value, field.getType()));
                        }
                    }
                }
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
        return t;
    }

    private static Object getValue(Object rootJson, String[] pathArray, ArrayList<Object> rootPath, Field field, Object t)
    {
        Class<?> fieldType = field.getType();
        for (String pathString : pathArray)
        {
            try
            {
                Object json = deepCopy(rootJson);
                ArrayList<Object> paths = PathParser.parse(pathString);
                paths.addAll(0, rootPath);

                for (int i = 0; i < paths.size(); i++)
                {
                    Object pathType = paths.get(i);
                    if(pathType instanceof PathType.PathJsonRoot)
                    {
                        json = deepCopy(rootJson);
                    }
                    else if(pathType instanceof PathType.PathPath)
                    {
                        if(json instanceof LsonObjectUtil)
                            json = ((LsonObjectUtil) json).get(((PathType.PathPath) pathType).path);
                        else if(json instanceof LsonArrayUtil)
                        {
                            LsonArrayUtil temp = new LsonArrayUtil();
                            for (int j = 0; j < ((LsonArrayUtil) json).size(); j++)
                            {
                                Object object = ((LsonArrayUtil) json).get(j);
                                if(object instanceof LsonObjectUtil)
                                    temp.add(((LsonObjectUtil) object).get(((PathType.PathPath) pathType).path));
                            }
                            json = temp;
                        }
                    }
                    else if(pathType instanceof PathType.PathIndex && json instanceof LsonArrayUtil)
                    {
                        LsonArrayUtil temp = new LsonArrayUtil();
                        int start = ((PathType.PathIndex) pathType).start;
                        if(start < 0) start += ((LsonArrayUtil) json).size();
                        int end = ((PathType.PathIndex) pathType).end;
                        if(end < 0) end += ((LsonArrayUtil) json).size();
                        if(((PathType.PathIndex) pathType).step > 0 && end >= start)
                        {
                            for (int j = start; j < Math.min(end, ((LsonArrayUtil) json).size()); j += ((PathType.PathIndex) pathType).step)
                                temp.add(((LsonArrayUtil) json).get(j));
                        }
                        json = temp;
                    }
                    else if(pathType instanceof PathType.PathIndexArray && json instanceof LsonArrayUtil)
                    {
                        LsonArrayUtil temp = new LsonArrayUtil();
                        for (int j = 0; j < ((PathType.PathIndexArray) pathType).index.size(); j++)
                        {
                            int index = ((PathType.PathIndexArray) pathType).index.get(j);
                            if(index < 0) index += ((LsonArrayUtil) json).size();
                            temp.add(((LsonArrayUtil) json).get(index));
                        }
                        json = temp;
                    }
                }

                if(BASE_DATA_TYPES.contains(fieldType.getName()))
                {
                    return LsonUtil.getJsonPrimitiveData(fieldType, json);
                }
                else if(LsonUtil.isMapTypeClass(fieldType))
                {
                    while (json instanceof LsonArrayUtil && ((LsonArrayUtil) json).size() > 0)
                        json = ((LsonArrayUtil) json).get(0);
                    if(json instanceof LsonObjectUtil)
                    {
                        Class<?> valueTypeArgument = LsonUtil.getMapType(field);
                        Map<String, Object> map = new HashMap<>();
                        String[] keys = ((LsonObjectUtil) json).getKeys();
                        if(valueTypeArgument != null && BASE_DATA_TYPES.contains(valueTypeArgument.getName()))
                        {
                            for (String key : keys)
                                map.put(key, LsonUtil.getJsonPrimitiveData(valueTypeArgument, ((LsonObjectUtil) json).get(key)));
                        }
                        else
                        {
                            for (String key : keys)
                            {
                                ArrayList<Object> tempPaths = (ArrayList<Object>) paths.clone();
                                tempPaths.add(new PathType.PathPath(key));
                                map.put(key, LsonUtil.getClassData(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[1], getMapType(field), valueTypeArgument, t, tempPaths));
                            }
                        }

                        for (Object object : map.values().toArray())
                            if(object != null)
                                return map;
                    }
                }
                else if(LsonUtil.isArrayTypeClass(fieldType))
                {
                    Object[] array;
                    if(json instanceof LsonArrayUtil)
                        array = (Object[]) Array.newInstance(Object.class, ((LsonArrayUtil) json).size());
                    else
                        array = (Object[]) Array.newInstance(Object.class, 1);
                    if(BASE_DATA_TYPES.contains(LsonUtil.getArrayType(fieldType).getName()))
                    {
                        if(json instanceof LsonArrayUtil)
                        {
                            for (int i = 0; i < ((LsonArrayUtil) json).size(); i++)
                                array[i] = LsonUtil.getJsonPrimitiveData(LsonUtil.getArrayType(fieldType), ((LsonArrayUtil) json).get(i));
                        }
                        else
                            array[0] = LsonUtil.getJsonPrimitiveData(LsonUtil.getArrayType(fieldType), json);
                    }
                    else
                    {
                        if(json instanceof LsonArrayUtil)
                        {
                            for (int i = 0; i < ((LsonArrayUtil) json).size(); i++)
                            {
                                ArrayList<Object> tempPaths = (ArrayList<Object>) paths.clone();
                                tempPaths.add(new PathType.PathIndexArray(new ArrayList<>(Collections.singletonList(i))));
                                array[i] = LsonUtil.getClassData(((GenericArrayType) field.getGenericType()).getGenericComponentType(), getArrayType(fieldType), rootJson, t, tempPaths);
                            }
                        }
                        else
                        {
                            array[0] = LsonUtil.getClassData(((GenericArrayType) field.getGenericType()).getGenericComponentType(), fieldType.getComponentType(), rootJson, t, paths);
                        }
                    }

                    for (Object o : array)
                        if(o != null)
                            return array;
                }
                else if(LsonUtil.isListTypeClass(fieldType))
                {
                    Class<?> actualTypeArgument = LsonUtil.getListType(field);
                    List<Object> list = new ArrayList<>();
                    if(actualTypeArgument != null && BASE_DATA_TYPES.contains(actualTypeArgument.getName()))
                    {
                        if(json instanceof LsonArrayUtil)
                        {
                            for (int i = 0; i < ((LsonArrayUtil) json).size(); i++)
                                list.add(LsonUtil.getJsonPrimitiveData(actualTypeArgument, ((LsonArrayUtil) json).get(i)));
                        }
                        else
                        {
                            list.add(LsonUtil.getJsonPrimitiveData(actualTypeArgument, json));
                        }
                    }
                    else
                    {
                        if(json instanceof LsonArrayUtil)
                        {
                            for (int i = 0; i < ((LsonArrayUtil) json).size(); i++)
                            {
                                ArrayList<Object> tempPaths = (ArrayList<Object>) paths.clone();
                                tempPaths.add(new PathType.PathIndexArray(new ArrayList<>(Collections.singletonList(i))));
                                list.add(getClassData(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0], getListType(field), rootJson, t, tempPaths));
                            }
                        }
                        else
                        {
                            list.add(getClassData(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0], getListType(field), rootJson, t, paths));
                        }
                    }
                    for (int i = 0; i < list.size(); i++)
                        if(list.get(i) != null)
                            return list;
                }
                else
                {
                    return getClassData(field.getGenericType(), field.getType(), rootJson, t, paths);
                }
            }
            catch (RuntimeException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static Object deepCopy(Object object)
    {
        if(object instanceof LsonObjectUtil)
            return new LsonObjectUtil(((LsonObjectUtil) object).getJsonObject().deepCopy());
        else if(object instanceof LsonArrayUtil)
            return new LsonArrayUtil(((LsonArrayUtil) object).getJsonArray().deepCopy());
        else if(object instanceof LsonPrimitiveUtil)
            return new LsonPrimitiveUtil(((LsonPrimitiveUtil) object).getJsonElement().deepCopy());
        return object;
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

    private static Object getJsonPrimitiveData(Class<?> c, Object json)
    {
        while (json instanceof LsonArrayUtil)
        {
            if(((LsonArrayUtil) json).size() > 0)
                json = ((LsonArrayUtil) json).get(0);
        }
        if(json instanceof LsonPrimitiveUtil)
            return getJsonPrimitiveData(c, (LsonPrimitiveUtil) json);
        return null;
    }

    private static Object getJsonPrimitiveData(Class<?> c, LsonPrimitiveUtil jsonPrimitive)
    {
        if((c.getName().equals("boolean") || c.getName().equals("java.lang.Boolean")) && jsonPrimitive.isBoolean())
            return jsonPrimitive.getAsBoolean();
        else if(c.getName().equals("java.lang.String"))
            return jsonPrimitive.getAsString();
        else if(jsonPrimitive.isNumber())
            return jsonPrimitive.getAsDouble();
        return null;
    }

    private static Object getClassData(Type genericType, Class<?> defType, Object rootJson, Object t, ArrayList<Object> paths)
    {
        if(genericType instanceof TypeVariable)
        {
            parameterizedTypes.add(((TypeVariable) genericType).getName());
            LinkedHashMap<String, TypeReference.TypeParameterized> typeParameterizedMap = (LinkedHashMap<String, TypeReference.TypeParameterized>) typeReference.typeMap.clone();
            for (int i = 0; i < parameterizedTypes.size() - 1; i++)
                typeParameterizedMap = typeParameterizedMap.get(parameterizedTypes.get(i)).map;

            Object result = fromJson(rootJson, typeParameterizedMap.get(parameterizedTypes.get(parameterizedTypes.size() - 1)).clz, t, paths);
            parameterizedTypes.remove(parameterizedTypes.size() - 1);
            return result;
        }
        return fromJson(rootJson, defType, t, paths);
    }

    private static Object doubleNumberHandle(Object number, Class<?> clz)
    {
        if(number instanceof Double)
        {
            switch (clz.getName())
            {
                case "int":
                case "java.lang.Integer":
                    return ((Double) number).intValue();
                case "short":
                case "java.lang.Short":
                    return ((Double) number).shortValue();
                case "long":
                case "java.lang.Long":
                    return ((Double) number).longValue();
                case "float":
                case "java.lang.Float":
                    return ((Double) number).floatValue();
            }
        }
        return number;
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

    private static Class<?> getListType(Field field)
    {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType)
        {
            ParameterizedType pt = (ParameterizedType) genericType;
            if(!(pt.getActualTypeArguments()[0] instanceof TypeVariable))
                return (Class<?>) pt.getActualTypeArguments()[0];
        }
        return null;
    }

    private static Class<?> getArrayType(Class<?> clz)
    {
        return clz.getComponentType();
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
}

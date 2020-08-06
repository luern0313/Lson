package cn.luern0313.lson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.luern0313.lson.annotation.LsonAddPrefix;
import cn.luern0313.lson.annotation.LsonAddSuffix;
import cn.luern0313.lson.annotation.LsonDateFormat;
import cn.luern0313.lson.annotation.LsonPath;
import cn.luern0313.lson.annotation.LsonReplaceAll;
import cn.luern0313.lson.util.DataProcessUtil;

/**
 * 被 luern0313 创建于 2020/7/28.
 */

public class LsonUtil
{
    private static LsonAnnotationListener lsonAnnotationListener;

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
    public static <T> T fromJson(LsonObjectUtil json, Class<T> clz)
    {
        return LsonUtil.fromJson(json, clz, null);
    }

    private static <T> T fromJson(LsonObjectUtil json, Class<T> clz, Object genericSuperclass)
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
        catch (IllegalAccessException | InstantiationException | InvocationTargetException | NullPointerException e)
        {
            e.printStackTrace();
        }

        Field[] fieldArray = clz.getDeclaredFields();
        for (Field field : fieldArray)
        {
            try
            {
                LsonPath path = field.getAnnotation(LsonPath.class);
                if(path != null)
                {
                    Object value = getValue(json, path.value(), field, t);
                    if(value != null)
                    {
                        Annotation[] annotations = field.getAnnotations();
                        for (Annotation annotation : annotations)
                        {
                            LsonDefinedAnnotation lsonDefinedAnnotation = annotation.annotationType().getAnnotation(LsonDefinedAnnotation.class);
                            if(lsonDefinedAnnotation != null)
                            {
                                if(isArrayTypeClass(value.getClass()) && !lsonDefinedAnnotation.isIgnoreArray())
                                {
                                    for (int i = 0; i < ((Object[]) value).length; i++)
                                    {
                                        if(BUILT_IN_ANNOTATION.contains(annotation.getClass().getName()))
                                            ((Object[]) value)[i] = handleBuiltInAnnotation(((Object[]) value)[i], annotation);
                                        else if(lsonAnnotationListener != null)
                                            ((Object[]) value)[i] = lsonAnnotationListener.handleAnnotation(((Object[]) value)[i], annotation);
                                    }
                                }
                                else if(isListTypeClass(value.getClass()) && !lsonDefinedAnnotation.isIgnoreArray())
                                {
                                    for (int i = 0; i < ((List<Object>) value).size(); i++)
                                    {
                                        if(BUILT_IN_ANNOTATION.contains(annotation.getClass().getName()))
                                            ((List<Object>) value).set(i, handleBuiltInAnnotation(((List<Object>) value).get(i), annotation));
                                        else if(lsonAnnotationListener != null)
                                            ((List<Object>) value).set(i, lsonAnnotationListener.handleAnnotation(((List<Object>) value).get(i), annotation));
                                    }
                                }
                                else
                                {
                                    if(BUILT_IN_ANNOTATION.contains(annotation.getClass().getName()))
                                        value = handleBuiltInAnnotation(value, annotation);
                                    else if(lsonDefinedAnnotation != null && lsonAnnotationListener != null)
                                        value = lsonAnnotationListener.handleAnnotation(value, annotation);
                                }
                            }
                        }

                        field.setAccessible(true);
                        field.set(t, value);
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

    private static ArrayList<Object> getPath(String path)
    {
        String[] pathArray = path.split("\\.");
        ArrayList<Object> pathArrayList = new ArrayList<>();
        for (String p : pathArray)
        {
            String[] paths = p.split("\\[");
            for (int j = 0; j < paths.length; j++)
            {
                if(j == 0)
                    pathArrayList.add(paths[0]);
                else
                    pathArrayList.add(Integer.parseInt(paths[j].substring(0, paths[j].length() - 1)));
            }
        }
        return pathArrayList;
    }

    private static Object getValue(Object json, String[] pathArray, Field field, Object t)
    {
        Class<?> c = field.getType();
        for (String path : pathArray)
        {
            try
            {
                ArrayList<Object> paths = getPath(path);
                for (int i = 0; i < paths.size() - 1; i++)
                {
                    if(json instanceof LsonObjectUtil && paths.get(i) instanceof String)
                    {
                        if(i < paths.size() - 1 && paths.get(i + 1) instanceof String)
                            json = ((LsonObjectUtil) json).getAsJsonObject((String) paths.get(i));
                        else if(i < paths.size() - 1 && paths.get(i + 1) instanceof Integer)
                            json = ((LsonObjectUtil) json).getAsJsonArray((String) paths.get(i));
                    }
                    else if(json instanceof LsonArrayUtil && paths.get(i) instanceof Integer)
                    {
                        if(i < paths.size() - 1 && paths.get(i + 1) instanceof String)
                            json = ((LsonArrayUtil) json).getAsJsonObject((Integer) paths.get(i));
                        else if(i < paths.size() - 1 && paths.get(i + 1) instanceof Integer)
                            json = ((LsonArrayUtil) json).getAsJsonArray((Integer) paths.get(i));
                    }
                }

                JsonElement jsonElement = null;
                if(json instanceof LsonObjectUtil)
                    jsonElement = ((LsonObjectUtil) json).get((String) paths.get(paths.size() - 1));
                else if(json instanceof LsonArrayUtil)
                    jsonElement = ((LsonArrayUtil) json).get((int) paths.get(paths.size() - 1));

                if(jsonElement.isJsonPrimitive())
                {
                    JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
                    Object value = getJsonPrimitiveData(c, jsonPrimitive);
                    if(value != null)
                        return getJsonPrimitiveData(c, jsonPrimitive);
                }
                else if(isMapTypeClass(c) && jsonElement instanceof JsonObject)
                {
                    LsonObjectUtil gsonObjectUtil = new LsonObjectUtil(jsonElement);
                    Type genericType = field.getGenericType();
                    if (genericType instanceof ParameterizedType)
                    {
                        ParameterizedType pt = (ParameterizedType) genericType;
                        Class<?> valueTypeArgument = (Class<?>) pt.getActualTypeArguments()[1];
                        Map<String, Object> map = new HashMap<>();
                        String[] keys = gsonObjectUtil.getKeys();
                        if(BASE_DATA_TYPES.contains(valueTypeArgument.getName()))
                        {
                            for (String key : keys)
                                map.put(key, getJsonPrimitiveData(valueTypeArgument, (JsonPrimitive) gsonObjectUtil.get(key)));
                        }
                        else
                        {
                            for (String key : keys)
                                map.put(key, LsonUtil.fromJson(gsonObjectUtil.getAsJsonObject(key), valueTypeArgument, t));
                        }
                        for (Object object : map.values().toArray())
                            if(object != null)
                                return map;
                    }
                }
                else if(isArrayTypeClass(c) && jsonElement instanceof JsonArray)
                {
                    LsonArrayUtil gsonArrayUtil = new LsonArrayUtil(jsonElement);
                    Object[] array = (Object[]) Array.newInstance(c.getComponentType(), gsonArrayUtil.size());
                    if(BASE_DATA_TYPES.contains(c.getComponentType().getName()))
                    {
                        for (int i = 0; i < gsonArrayUtil.size(); i++)
                            array[i] = getJsonPrimitiveData(c.getComponentType(), (JsonPrimitive) gsonArrayUtil.get(i));
                    }
                    else
                    {
                        for (int i = 0; i < gsonArrayUtil.size(); i++)
                            array[i] = LsonUtil.fromJson(gsonArrayUtil.getAsJsonObject(i), c.getComponentType(), t);
                    }

                    for (Object o : array)
                        if(o != null)
                            return array;
                }
                else if(isListTypeClass(c) && jsonElement instanceof JsonArray)
                {
                    LsonArrayUtil gsonArrayUtil = new LsonArrayUtil(jsonElement);
                    Type genericType = field.getGenericType();
                    if (genericType instanceof ParameterizedType)
                    {
                        ParameterizedType pt = (ParameterizedType) genericType;
                        Class<?> actualTypeArgument = (Class<?>) pt.getActualTypeArguments()[0];
                        List<Object> list = new ArrayList<>();
                        if(BASE_DATA_TYPES.contains(actualTypeArgument.getName()))
                        {
                            for (int i = 0; i < gsonArrayUtil.size(); i++)
                                list.add(getJsonPrimitiveData(actualTypeArgument, (JsonPrimitive) gsonArrayUtil.get(i)));
                        }
                        else
                        {
                            for (int i = 0; i < gsonArrayUtil.size(); i++)
                                list.add(LsonUtil.fromJson(gsonArrayUtil.getAsJsonObject(i), actualTypeArgument, t));
                        }

                        for (int i = 0; i < list.size(); i++)
                            if(list.get(i) != null)
                                return list;
                    }
                }
            }
            catch (RuntimeException e)
            {
                e.printStackTrace();
            }
        }

        return null;
    }

    private static Object handleBuiltInAnnotation(Object value, Annotation annotation)
    {
        if(LsonDateFormat.class.getName().equals(annotation.getClass().getName()))
            return DataProcessUtil.getTime(Integer.parseInt((String) value), ((LsonDateFormat) annotation).value());
        else if(LsonAddPrefix.class.getName().equals(annotation.getClass().getName()))
            return ((LsonAddPrefix) annotation).value() + value;
        else if(LsonAddSuffix.class.getName().equals(annotation.getClass().getName()))
            return value + ((LsonAddSuffix) annotation).value();
        else if(LsonReplaceAll.class.getName().equals(annotation.getClass().getName()))
        {
            String[] regexArray = ((LsonReplaceAll) annotation).regex();
            String[] replacementArray = ((LsonReplaceAll) annotation).replacement();
            for (int i = 0; i < regexArray.length; i++)
                value = ((String) value).replaceAll(regexArray[i], replacementArray[i]);
        }
        return null;
    }

    private static Object getJsonPrimitiveData(Class<?> c, JsonPrimitive jsonPrimitive)
    {
        if((c.getName().equals("boolean") || c.getName().equals("java.lang.Boolean")) && jsonPrimitive.isBoolean())
            return jsonPrimitive.getAsBoolean();
        else if(c.getName().equals("java.lang.String"))
            return jsonPrimitive.getAsString();
        else if(jsonPrimitive.isNumber())
        {
            switch (c.getName())
            {
                case "int":
                case "java.lang.Integer":
                    return jsonPrimitive.getAsInt();
                case "short":
                case "java.lang.Short":
                    return jsonPrimitive.getAsShort();
                case "long":
                case "java.lang.Long":
                    return jsonPrimitive.getAsLong();
                case "float":
                case "java.lang.Float":
                    return jsonPrimitive.getAsFloat();
                case "double":
                case "java.lang.Double":
                    return jsonPrimitive.getAsDouble();
            }
        }
        return null;
    }

    /**
     * 程序运行时，通过此方法传入实现{@link LsonAnnotationListener}接口类的实例，自定义注解才可正常运行。
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
            e.printStackTrace();
        }
        return null;
    }

    private static boolean isMapTypeClass(Class<?> clz)
    {
        try
        {
            return Map.class.isAssignableFrom(clz) || clz.newInstance() instanceof Map;
        }
        catch (IllegalAccessException | InstantiationException e)
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
        catch (IllegalAccessException | InstantiationException e)
        {
            return false;
        }
    }

    private static boolean isArrayTypeClass(Class<?> clz)
    {
        return clz.isArray();
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
         * @return 处理完成的值。
         *
         * @author luern0313
         */
        Object handleAnnotation(Object value, Annotation annotation);
    }
}

package cn.luern0313.lson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.luern0313.lson.annotation.LsonDefinedAnnotation;
import cn.luern0313.lson.annotation.method.LsonCallMethod;
import cn.luern0313.lson.annotation.other.AnnotationOrder;
import cn.luern0313.lson.annotation.field.LsonPath;
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

public class Deserialization {
    private final Lson lson;

    public Deserialization(Lson lson) {
        this.lson = lson;
    }

    @SuppressWarnings("unchecked")
    protected <T> T fromJson(LsonElement json, TypeUtil typeUtil, Object genericSuperObject, ArrayList<Object> rootJsonPath) {
        return (T) finalValueHandle(getClassData(typeUtil, json, json, genericSuperObject, rootJsonPath), typeUtil);
    }

    @SuppressWarnings("unchecked")
    private <T> T create(TypeUtil clz, Object genericSuperObject) {
        return (T) lson.getClassConstructor().create(clz, genericSuperObject);
    }

    private <T> T deserialization(LsonElement json, TypeUtil clz, T t, ArrayList<Object> rootJsonPath) {
        handleMethod(clz, t, LsonCallMethod.CallMethodTiming.BEFORE_DESERIALIZATION);

        TypeUtil superClass = new TypeUtil(clz.getAsClass().getSuperclass());
        if (superClass.getAsClass() != Object.class)
            deserialization(json, superClass, t, rootJsonPath);

        Field[] fieldArray = clz.getAsClass().getDeclaredFields();
        for (Field field : fieldArray) {
            try {
                LsonPath path = field.getAnnotation(LsonPath.class);
                if (path == null)
                    continue;
                String[] pathArray = path.value();
                if (pathArray.length == 1 && pathArray[0].equals("")) {
                    pathArray[0] = field.getName();
                    String underScoreCase = DataProcessUtil.getUnderScoreCase(field.getName());
                    if (!field.getName().equals(underScoreCase))
                        pathArray = new String[]{field.getName(), underScoreCase};
                }

                Type type = field.getGenericType();
                TypeUtil fieldType, targetType;
                if (clz.getAsType() instanceof TypeVariable)
                    fieldType = new TypeUtil(type, clz.getTypeReference().typeMap.get(((TypeVariable<?>) clz.getAsType()).getName()));
                else
                    fieldType = new TypeUtil(type, clz.getTypeReference());
                targetType = new TypeUtil(path.preClass());
                Object value = getValue(json, pathArray, rootJsonPath, targetType.getAsClass() == Object.class ? fieldType : targetType, t);
                if (value == null)
                    continue;

                List<Annotation> annotations = sortAnnotation(field.getAnnotations());
                for (Annotation annotation : annotations) {
                    LsonDefinedAnnotation lsonDefinedAnnotation = annotation.annotationType().getAnnotation(LsonDefinedAnnotation.class);
                    if (lsonDefinedAnnotation != null && !annotation.annotationType().getName().equals(LsonPath.class.getName()))
                        value = handleAnnotation(value, annotation, lsonDefinedAnnotation, t);
                }

                value = finalValueHandle(value, fieldType);
                if (value != null) {
                    field.setAccessible(true);
                    field.set(t, value);
                }

            } catch (Exception ignored) {
            }
        }
        handleMethod(clz, t, LsonCallMethod.CallMethodTiming.AFTER_DESERIALIZATION);
        return t;
    }

    @SuppressWarnings("Java8ListSort")
    private List<Annotation> sortAnnotation(Annotation[] annotations) {
        List<Annotation> annotationList = new ArrayList<>(Arrays.asList(annotations));
        Collections.sort(annotationList, (o1, o2) -> getAnnotationOrder(o1) - getAnnotationOrder(o2));
        return annotationList;
    }

    private int getAnnotationOrder(Annotation annotation) {
        try {
            Method[] methods = annotation.annotationType().getDeclaredMethods();
            for (Method method : methods) {
                if (method.getAnnotation(AnnotationOrder.class) != null) {
                    Type type = method.getGenericReturnType();
                    if (type instanceof Class && ((Class<?>) type).getName().equals("int"))
                        return (int) method.invoke(annotation);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return Integer.MAX_VALUE;
    }

    public Object getValue(LsonElement rootJson, String[] pathArray, ArrayList<Object> rootPath, TypeUtil fieldType, Object t) {
        for (String pathString : pathArray) {
            ArrayList<Object> paths = PathParser.parse(pathString);
            Object value = getValue(rootJson, paths, rootPath, fieldType, t);
            if (value != null)
                return value;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Object getValue(LsonElement rootJson, ArrayList<Object> paths, ArrayList<Object> rootPath, TypeUtil fieldType, Object t) {
        try {
            ArrayList<Object> jsonPaths = (ArrayList<Object>) paths.clone();
            jsonPaths.addAll(0, rootPath);

            LsonElement json = rootJson;
            for (int i = 0; i < jsonPaths.size(); i++) {
                Object pathType = jsonPaths.get(i);
                if (pathType instanceof PathType.PathJsonRoot)
                    json = rootJson;
                else if (pathType instanceof PathType.PathPath) {
                    if (json.isLsonObject())
                        json = json.getAsLsonObject().get(((PathType.PathPath) pathType).path);
                    else if (json.isLsonArray()) {
                        LsonArray temp = new LsonArray();
                        for (int j = 0; j < json.getAsLsonArray().size(); j++) {
                            LsonElement lsonElement = json.getAsLsonArray().get(j);
                            if (lsonElement.isLsonObject())
                                temp.add(lsonElement.getAsLsonObject().get(((PathType.PathPath) pathType).path));
                        }
                        json = temp;
                    }
                } else if (pathType instanceof PathType.PathIndex && json.isLsonArray()) {
                    LsonArray temp = new LsonArray();
                    int start = ((PathType.PathIndex) pathType).start;
                    if (start < 0) start += json.getAsLsonArray().size();
                    int end = ((PathType.PathIndex) pathType).end;
                    if (end < 0) end += json.getAsLsonArray().size();
                    if (((PathType.PathIndex) pathType).step > 0 && end >= start)
                        for (int j = start; j < Math.min(end, json.getAsLsonArray().size()); j += ((PathType.PathIndex) pathType).step)
                            temp.add(json.getAsLsonArray().get(j));
                    if (temp.size() == 1)
                        json = temp.get(0);
                    else
                        json = temp;
                } else if (pathType instanceof PathType.PathIndexArray && json.isLsonArray()) {
                    LsonArray temp = new LsonArray();
                    for (int j = 0; j < ((PathType.PathIndexArray) pathType).index.size(); j++) {
                        int index = (int) ((PathType.PathIndexArray) pathType).index.get(j);
                        if (index < 0) index += json.getAsLsonArray().size();
                        temp.add(json.getAsLsonArray().get(index));
                    }
                    if (temp.size() == 1)
                        json = temp.get(0);
                    else
                        json = temp;
                } else if (pathType instanceof PathType.PathFilter) {
                    if (json.isLsonArray()) {
                        PathType.PathFilter filter = (PathType.PathFilter) pathType;
                        LsonArray temp = new LsonArray();
                        ArrayList<Object> root = new ArrayList<>(jsonPaths.subList(0, i));
                        for (int j = 0; j < json.getAsLsonArray().size(); j++) {
                            Object left = getFilterData(filter.left, j, rootJson, root, t);
                            Object right = getFilterData(filter.right, j, rootJson, root, t);
                            if (compare(left, filter.comparator, right))
                                temp.add(json.getAsLsonArray().get(j));
                        }
                        json = temp;
                    }
                }
            }

            if (fieldType.isNull() || fieldType.isPrimitivePlus() || fieldType.getName().equals(Object.class.getName()))
                return getJsonPrimitiveData(json);
            else if (json != null)
                return getClassData(fieldType, json, rootJson, t, jsonPaths);
        } catch (RuntimeException ignored) {
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Object getMapData(LsonElement json, LsonElement rootJson, TypeUtil fieldType, ArrayList<Object> jsonPaths, Object t) throws IllegalAccessException, java.lang.InstantiationException {
        while (json.isLsonArray() && ((LsonArray) json).size() > 0)
            json = ((LsonArray) json).get(0);

        TypeUtil valueTypeArgument = fieldType.getMapElementType();
        Map<String, Object> map = (Map<String, Object>) fieldType.getMapType().newInstance();
        if (json.isLsonObject()) {
            String[] keys = json.getAsLsonObject().getKeys();
            for (String key : keys) {
                if (valueTypeArgument.isPrimitivePlus())
                    map.put(key, getJsonPrimitiveData(json.getAsLsonObject().get(key)));
                else {
                    ArrayList<Object> tempPaths = (ArrayList<Object>) jsonPaths.clone();
                    tempPaths.add(new PathType.PathPath(key));
                    map.put(key, getClassData(valueTypeArgument, json.getAsLsonObject().get(key), rootJson, t, tempPaths));
                }
            }
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private Object getArrayData(LsonElement json, LsonElement rootJson, TypeUtil fieldType, ArrayList<Object> jsonPaths, Object t) {
        TypeUtil actualTypeArgument = fieldType.getArrayElementType();
        Object array;
        if (actualTypeArgument.isPrimitivePlus())
            array = Array.newInstance(DeserializationValueUtil.class, json.isLsonArray() ? json.getAsLsonArray().size() : 1);
        else
            array = Array.newInstance(actualTypeArgument.getAsClass(), json.isLsonArray() ? json.getAsLsonArray().size() : 1);

        if (json.isLsonArray()) {
            for (int i = 0; i < json.getAsLsonArray().size(); i++) {
                LsonElement lsonElement = json.getAsLsonArray().get(i);
                if (actualTypeArgument.isPrimitivePlus())
                    Array.set(array, i, getJsonPrimitiveData(lsonElement));
                else {
                    ArrayList<Object> tempPaths = (ArrayList<Object>) jsonPaths.clone();
                    tempPaths.add(new PathType.PathIndexArray(new ArrayList<>(Collections.singletonList(i))));
                    Array.set(array, i, getClassData(actualTypeArgument, lsonElement, rootJson, t, tempPaths));
                }
            }
        } else {
            if (actualTypeArgument.isPrimitivePlus())
                Array.set(array, 0, getJsonPrimitiveData(json));
            else
                Array.set(array, 0, getClassData(actualTypeArgument, json, rootJson, t, jsonPaths));
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    private Object getListData(LsonElement json, LsonElement rootJson, TypeUtil fieldType, ArrayList<Object> jsonPaths, Object t) throws IllegalAccessException, java.lang.InstantiationException {
        TypeUtil actualTypeArgument = fieldType.getListElementType();
        List<Object> list = (List<Object>) fieldType.getListType().newInstance();

        if (json.isLsonArray()) {
            for (int i = 0; i < json.getAsLsonArray().size(); i++) {
                LsonElement lsonElement = json.getAsLsonArray().get(i);
                if (actualTypeArgument.isPrimitivePlus())
                    list.add(getJsonPrimitiveData(lsonElement));
                else {
                    ArrayList<Object> tempPaths = (ArrayList<Object>) jsonPaths.clone();
                    tempPaths.add(new PathType.PathIndexArray(new ArrayList<>(Collections.singletonList(i))));
                    list.add(getClassData(actualTypeArgument, lsonElement, rootJson, t, tempPaths));
                }
            }
        } else if (json.isLsonPrimitive())
            list.add(getJsonPrimitiveData(json));
        else
            list.add(getClassData(actualTypeArgument, json, rootJson, t, jsonPaths));
        return list;
    }

    @SuppressWarnings("unchecked")
    private Object getSetData(LsonElement json, LsonElement rootJson, TypeUtil fieldType, ArrayList<Object> jsonPaths, Object t) throws IllegalAccessException, java.lang.InstantiationException {
        TypeUtil actualTypeArgument = fieldType.getSetElementType();
        Set<Object> set = (Set<Object>) fieldType.getSetType().newInstance();

        if (json.isLsonArray()) {
            for (int i = 0; i < json.getAsLsonArray().size(); i++) {
                LsonElement lsonElement = json.getAsLsonArray().get(i);
                if (actualTypeArgument.isPrimitivePlus())
                    set.add(getJsonPrimitiveData(lsonElement));
                else {
                    ArrayList<Object> tempPaths = (ArrayList<Object>) jsonPaths.clone();
                    tempPaths.add(new PathType.PathIndexArray(new ArrayList<>(Collections.singletonList(i))));
                    set.add(getClassData(actualTypeArgument, lsonElement, rootJson, t, tempPaths));
                }
            }
        } else if (json.isLsonPrimitive())
            set.add(getJsonPrimitiveData(json));
        else
            set.add(getClassData(actualTypeArgument, json, rootJson, t, jsonPaths));
        return set;
    }

    private Object getFilterData(PathType.PathFilter.PathFilterPart part, int index, LsonElement rootJson, ArrayList<Object> rootPath, Object t) {
        Object result = null;
        if (part.mode == PathType.PathFilter.PathFilterPart.FilterPartMode.PATH) {
            rootPath.add(new PathType.PathIndexArray(new ArrayList<>(Collections.singletonList(index))));
            result = finalValueHandle(getValue(rootJson, part.part, rootPath, TypeUtil.nullType(), t), TypeUtil.nullType());
            rootPath.remove(rootPath.size() - 1);

            if (result instanceof Object[])
                result = ((Object[]) result)[0];
        } else if (part.mode == PathType.PathFilter.PathFilterPart.FilterPartMode.ARRAY)
            result = part.part;
        else if (part.mode == PathType.PathFilter.PathFilterPart.FilterPartMode.SINGLE)
            result = part.part.get(0);
        return result;
    }

    private boolean compare(Object left, PathType.PathFilter.FilterComparator comparator, Object right) {
        if (comparator == PathType.PathFilter.FilterComparator.EXISTENCE) {
            if (left instanceof Boolean)
                return (boolean) left;
            else if (left instanceof String)
                return !left.equals("");
            else if (left instanceof Number)
                return ((Number) left).doubleValue() != 0;
            return left != null;
        }
        if (left != null && right != null) {
            if (comparator == PathType.PathFilter.FilterComparator.EQUAL) {
                if (left instanceof Number && right instanceof Number)
                    return ((Number) left).doubleValue() == ((Number) right).doubleValue();
                return left == right || left.equals(right);
            } else if (comparator == PathType.PathFilter.FilterComparator.NOT_EQUAL) {
                if (left instanceof Number && right instanceof Number)
                    return ((Number) left).doubleValue() != ((Number) right).doubleValue();
                return left != right;
            } else if (left instanceof Number && right instanceof Number) {
                if (comparator == PathType.PathFilter.FilterComparator.LESS)
                    return ((Number) left).doubleValue() < ((Number) right).doubleValue();
                else if (comparator == PathType.PathFilter.FilterComparator.LESS_EQUAL)
                    return ((Number) left).doubleValue() <= ((Number) right).doubleValue();
                else if (comparator == PathType.PathFilter.FilterComparator.GREATER)
                    return ((Number) left).doubleValue() > ((Number) right).doubleValue();
                else if (comparator == PathType.PathFilter.FilterComparator.GREATER_EQUAL)
                    return ((Number) left).doubleValue() >= ((Number) right).doubleValue();
            } else if (comparator == PathType.PathFilter.FilterComparator.IN && right instanceof ArrayList)
                return DataProcessUtil.getIndex(left, (ArrayList<?>) right) > -1;
            else if (comparator == PathType.PathFilter.FilterComparator.NOT_IN && right instanceof ArrayList)
                return DataProcessUtil.getIndex(left, (ArrayList<?>) right) == -1;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private <T> Object handleAnnotation(Object value, Annotation annotation, LsonDefinedAnnotation lsonDefinedAnnotation, T t) {
        if (value == null) return null;

        TypeUtil valueClass = new TypeUtil(value.getClass());
        if (valueClass.isArrayType()) {
            if (lsonDefinedAnnotation.isIgnoreArray())
                value = handleSingleAnnotation(finalValueHandle(value, valueClass), annotation, lsonDefinedAnnotation, t);
            else
                for (int i = 0; i < Array.getLength(value); i++)
                    Array.set(value, i, handleAnnotation(Array.get(value, i), annotation, lsonDefinedAnnotation, t));
        } else if (valueClass.isListType()) {
            if (lsonDefinedAnnotation.isIgnoreList())
                value = handleSingleAnnotation(finalValueHandle(value, valueClass), annotation, lsonDefinedAnnotation, t);
            else
                for (int i = 0; i < ((List<?>) value).size(); i++)
                    ((List<Object>) value).set(i, handleAnnotation(((List<?>) value).get(i), annotation, lsonDefinedAnnotation, t));
        } else if (valueClass.isMapType()) {
            if (lsonDefinedAnnotation.isIgnoreMap())
                value = handleSingleAnnotation(finalValueHandle(value, valueClass), annotation, lsonDefinedAnnotation, t);
            else {
                Object[] keys = ((Map<?, ?>) value).keySet().toArray();
                for (Object key : keys)
                    ((Map<Object, Object>) value).put(key, handleAnnotation(((Map<?, ?>) value).get(key), annotation, lsonDefinedAnnotation, t));
            }
        } else if (valueClass.isSetType()) {
            if (lsonDefinedAnnotation.isIgnoreSet())
                value = handleSingleAnnotation(finalValueHandle(value, valueClass), annotation, lsonDefinedAnnotation, t);
            else {
                Object[] objects = ((Set<?>) value).toArray();
                for (Object o : objects) {
                    ((Set<Object>) value).remove(o);
                    ((Set<Object>) value).add(handleAnnotation(o, annotation, lsonDefinedAnnotation, t));
                }
            }
        } else
            value = handleSingleAnnotation(value, annotation, lsonDefinedAnnotation, t);
        return value;
    }

    private Object handleAnnotationType(DeserializationValueUtil deserializationValueUtil, LsonDefinedAnnotation.AcceptableType acceptableType) {
        switch (acceptableType) {
            case STRING:
                return deserializationValueUtil.getAsStringBuilder();
            case NUMBER:
                return deserializationValueUtil.getAsNumber();
            case BOOLEAN:
                return deserializationValueUtil.getAsBoolean();
        }
        return deserializationValueUtil.get();
    }

    private <T> Object handleSingleAnnotation(Object value, Annotation annotation, LsonDefinedAnnotation lsonDefinedAnnotation, T t) {
        try {
            Object o = value;
            if (o instanceof DeserializationValueUtil)
                o = handleAnnotationType((DeserializationValueUtil) value, lsonDefinedAnnotation.acceptableDeserializationType());

            Method method = lsonDefinedAnnotation.config().getDeclaredMethod("deserialization", Object.class, Object.class, Object.class);
            Object object = method.invoke(lsonDefinedAnnotation.config().newInstance(), o, annotation, t);

            TypeUtil typeUtil = new TypeUtil(object);
            if (value instanceof DeserializationValueUtil && !typeUtil.isPrimitivePlus())
                return ((DeserializationValueUtil) value).set(object);
            else if (typeUtil.isPrimitivePlus())
                return new DeserializationValueUtil(object);
            return object;
        } catch (NoSuchMethodException | InvocationTargetException | java.lang.InstantiationException | IllegalAccessException ignored) {
        }
        return null;
    }

    private void handleMethod(TypeUtil typeUtil, Object t, LsonCallMethod.CallMethodTiming callMethodTiming) {
        if (t == null)
            return;
        Method[] methods = typeUtil.getAsClass().getDeclaredMethods();
        for (Method method : methods) {
            try {
                LsonCallMethod lsonCallMethod = method.getAnnotation(LsonCallMethod.class);
                if (lsonCallMethod != null && DataProcessUtil.getIndex(callMethodTiming, lsonCallMethod.timing()) > -1) {
                    method.setAccessible(true);
                    method.invoke(t);
                }
            } catch (RuntimeException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private DeserializationValueUtil getJsonPrimitiveData(LsonElement json) {
        while (json.isLsonArray())
            json = json.getAsLsonArray().get(0);
        if (json.isLsonPrimitive())
            return new DeserializationValueUtil(json.getAsLsonPrimitive().get(), json.getAsLsonPrimitive().getValueClass());
        return null;
    }

    private Object getClassData(TypeUtil fieldType, LsonElement json, LsonElement rootJson, Object t, ArrayList<Object> paths) {
        try {
            if (fieldType == null) return null;

            if (fieldType.isMapType())
                return getMapData(json, rootJson, fieldType, paths, t);
            else if (fieldType.isArrayType())
                return getArrayData(json, rootJson, fieldType, paths, t);
            else if (fieldType.isListType())
                return getListData(json, rootJson, fieldType, paths, t);
            else if (fieldType.isSetType())
                return getSetData(json, rootJson, fieldType, paths, t);
            else if (fieldType.getName().equals(Object.class.getName()))
                return getJsonPrimitiveData(json);
            else if (lson.getTypeAdapterList().has(fieldType))
                return lson.getTypeAdapterList().get(fieldType.getAsClass()).deserialization(json);
            return deserialization(rootJson, fieldType, create(fieldType, t), paths);
        } catch (RuntimeException | java.lang.InstantiationException | IllegalAccessException ignored) {
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <E1, E2> E1 finalValueHandle(E1 value, TypeUtil fieldType) {
        try {
            if (value == null) return null;

            TypeUtil valueClass = new TypeUtil(value.getClass());
            if (valueClass.isArrayType()) {
                Object finalValue;
                if (fieldType.getArrayElementType().getAsClass().equals(DeserializationValueUtil.class))
                    finalValue = Array.newInstance(((DeserializationValueUtil) Array.get(value, 0)).getType(), Array.getLength(value));
                else
                    finalValue = Array.newInstance(fieldType.getArrayElementType().getAsClass(), Array.getLength(value));

                for (int i = 0; i < Array.getLength(value); i++)
                    Array.set(finalValue, i, finalValueHandle(Array.get(value, i), fieldType.getArrayElementType()));
                return (E1) finalValue;
            } else if (valueClass.isListType()) {
                TypeUtil type = fieldType.getListElementType();
                for (int i = 0; i < ((List<E2>) value).size(); i++)
                    ((List<E2>) value).set(i, finalValueHandle(((List<E2>) value).get(i), type));
                return value;
            } else if (valueClass.isMapType()) {
                TypeUtil type = fieldType.getMapElementType();
                for (Object key : ((Map<String, E2>) value).keySet().toArray())
                    ((Map<String, E2>) value).put((String) key, finalValueHandle(((Map<String, E2>) value).get(key), type));
                return value;
            } else if (valueClass.isSetType()) {
                TypeUtil type = fieldType.getSetElementType();
                E2[] objects = (E2[]) ((Set<?>) value).toArray();
                for (E2 o : objects) {
                    ((Set<E2>) value).remove(o);
                    ((Set<E2>) value).add(finalValueHandle(o, type));
                }
                return value;
            } else if (value instanceof DeserializationValueUtil) {
                if (((DeserializationValueUtil) value).getCurrentType() == Double.class)
                    return (E1) finalValueHandle((DeserializationValueUtil) value, fieldType);
                else if (((DeserializationValueUtil) value).getCurrentType() == StringBuilder.class)
                    return (E1) ((DeserializationValueUtil) value).get().toString();
                else if (((DeserializationValueUtil) value).getCurrentType() == Boolean.class)
                    return (E1) ((DeserializationValueUtil) value).get();
                else
                    return (E1) ((DeserializationValueUtil) value).get(fieldType);
            } else return value;
        } catch (RuntimeException ignored) {
        }
        return null;
    }

    private Object finalValueHandle(DeserializationValueUtil value, TypeUtil fieldType) {
        if (!fieldType.isNull()) {
            switch (fieldType.getName()) {
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
        } else {
            switch (value.getType().getName()) {
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

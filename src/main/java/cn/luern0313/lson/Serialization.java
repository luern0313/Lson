package cn.luern0313.lson;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cn.luern0313.lson.annotation.field.LsonPath;
import cn.luern0313.lson.element.LsonArray;
import cn.luern0313.lson.element.LsonElement;
import cn.luern0313.lson.element.LsonObject;
import cn.luern0313.lson.element.LsonPrimitive;
import cn.luern0313.lson.path.PathParser;
import cn.luern0313.lson.path.PathType;
import cn.luern0313.lson.util.DataProcessUtil;
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
                    getValue(toJson(field.get(object)), pathArray[0], new ArrayList<>(), lsonObject);
                }
            }
            catch (RuntimeException | IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
        return lsonObject;
    }

    @SuppressWarnings("unchecked")
    private static void getValue(LsonElement value, String pathString, ArrayList<String> rootPath, LsonElement rootJson)
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
}

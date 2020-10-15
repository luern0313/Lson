package cn.luern0313.lson;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
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

    private static void getValue(LsonElement lsonElement, String pathString, ArrayList<String> rootPath, LsonElement rootJson)
    {
        ArrayList<Object> jsonPaths = PathParser.parse(pathString);
        jsonPaths.addAll(0, rootPath);

        LsonElement json = rootJson;
        for (int i = 0; i < jsonPaths.size() - 1; i++)
        {
            Object pathType = jsonPaths.get(i);
            if(pathType instanceof PathType.PathJsonRoot)
                json = rootJson;
            else if(pathType instanceof PathType.PathPath && json.isLsonObject())
                json = json.getAsLsonObject().hasPut(((PathType.PathPath) pathType).path, LsonObject.class);
        }

        json.getAsLsonObject().put(((PathType.PathPath) jsonPaths.get(jsonPaths.size() - 1)).path, lsonElement);
    }
}

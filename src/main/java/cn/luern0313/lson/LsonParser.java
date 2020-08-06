package cn.luern0313.lson;

import com.google.gson.JsonParser;

/**
 * 被 luern0313 创建于 2020/8/4.
 */

public class LsonParser
{
    public static LsonObjectUtil parseString(String jsonString)
    {
        return new LsonObjectUtil(JsonParser.parseString(jsonString));
    }
}

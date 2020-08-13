package cn.luern0313.lson.path;

import java.util.ArrayList;

/**
 * 被 luern0313 创建于 2020/8/10.
 */

class StackValue
{
    static final int TYPE_EXPRESSION = 0;

    final int type;
    final Object value;

    private StackValue(int type, Object value)
    {
        this.type = type;
        this.value = value;
    }

    static StackValue newExpression()
    {
        return new StackValue(TYPE_EXPRESSION, new Expression());
    }

    String valueAsString()
    {
        return (String) value;
    }

    int valueAsInt()
    {
        return (int) value;
    }

    @SuppressWarnings("unchecked")
    ArrayList<Object> valueAsList()
    {
        return (ArrayList<Object>) value;
    }

    Expression valueAsExpression()
    {
        return (Expression) value;
    }

    static class Expression
    {
        static final int MODE_INDEX_ARRAY = 0;
        static final int MODE_INDEX = 1;
        static final int MODE_PATH = 2;

        int mode = MODE_INDEX_ARRAY;

        ArrayList<Integer> index = new ArrayList<>();
        boolean isJustColon = true;
        String path;
    }
}

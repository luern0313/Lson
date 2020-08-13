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
        ArrayList<Integer> index = new ArrayList<>();
        boolean isIndex = false;
        boolean isJustColon = true;
    }
}

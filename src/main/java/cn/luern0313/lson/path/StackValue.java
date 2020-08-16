package cn.luern0313.lson.path;

import java.util.ArrayList;

/**
 * 被 luern0313 创建于 2020/8/10.
 */

class StackValue
{
    static final int TYPE_EXPRESSION = 0;
    static final int TYPE_FILTER = 1;

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

    static StackValue newFilter()
    {
        return new StackValue(TYPE_FILTER, new Filter());
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

    Filter valueAsFilter()
    {
        return (Filter) value;
    }

    static class Expression
    {
        ExpressionMode mode = ExpressionMode.INDEX_ARRAY;

        ArrayList<Integer> index = new ArrayList<>();
        boolean isJustColon = true;
        String path;

        enum ExpressionMode
        {
            INDEX_ARRAY,
            INDEX,
            PATH
        }
    }

    static class Filter
    {
        FilterComparator comparator = FilterComparator.EXISTENCE;
        FilterPart left;
        FilterPart right;

        enum FilterComparator
        {
            EXISTENCE,
            EQUAL,
            NOT_EQUAL,
            LESS,
            LESS_EQUAL,
            GREATER,
            GREATER_EQUAL,
            REGULAR,
            IN,
            NOT_IN;
        }

        static class FilterPart
        {
            FilterPartMode mode = FilterPartMode.PATH;
            ArrayList<Object> part;

            enum FilterPartMode
            {
                PATH,
                ARRAY;
            }
        }
    }
}

package cn.luern0313.lson.path;

import java.util.ArrayList;

import cn.luern0313.lson.exception.PathParseException;

/**
 * 被 luern0313 创建于 2020/8/10.
 */

class StackValue {
    static final int TYPE_BASE_PATH = 0;
    static final int TYPE_EXPRESSION = 1;
    static final int TYPE_FILTER = 2;

    final int type;
    final StackValueObject value;

    private StackValue(int type, StackValueObject value) {
        this.type = type;
        this.value = value;
    }

    public void add(Object path) {
        value.add(path);
    }

    static StackValue newBasePath() {
        return new StackValue(TYPE_BASE_PATH, new BasePath());
    }

    static StackValue newExpression() {
        return new StackValue(TYPE_EXPRESSION, new Expression());
    }

    static StackValue newFilter() {
        return new StackValue(TYPE_FILTER, new Filter());
    }

    BasePath valueAsBasePath() {
        return (BasePath) value;
    }

    Expression valueAsExpression() {
        return (Expression) value;
    }

    Filter valueAsFilter() {
        return (Filter) value;
    }

    static abstract class StackValueObject {
        abstract void add(Object path);
    }

    static class BasePath extends StackValueObject {
        ArrayList<Object> paths = new ArrayList<>();

        @Override
        void add(Object path) {
            paths.add(path);
        }
    }

    static class Expression extends StackValueObject {
        ExpressionMode mode = ExpressionMode.ARRAY;

        ArrayList<Object> index = new ArrayList<>(16);
        boolean isJustColon = true;
        Filter filter;

        @Override
        void add(Object object) {
            if (mode == ExpressionMode.ARRAY || mode == ExpressionMode.INDEX)
                index.add(object);
            else if (mode == ExpressionMode.FILTER)
                this.filter = (Filter) object;
        }

        enum ExpressionMode {
            ARRAY,
            INDEX,
            FILTER
        }
    }

    static class Filter extends StackValueObject {
        int index = 0;
        PathType.PathFilter.FilterComparator comparator = PathType.PathFilter.FilterComparator.EXISTENCE;
        FilterPart left = new FilterPart();
        FilterPart right = new FilterPart();

        @Override
        void add(Object path) {
            FilterPart filterPart;
            if (index == 0)
                filterPart = left;
            else
                filterPart = right;

            if (path instanceof PathType.PathIndexArray) {
                filterPart.changeMode(PathType.PathFilter.PathFilterPart.FilterPartMode.ARRAY);
                filterPart.part.addAll(((PathType.PathIndexArray) path).index);
            } else
                filterPart.part.add(path);
        }

        FilterPart getCurrentPart() {
            if (index == 0)
                return left;
            else
                return right;
        }

        static class FilterPart {
            PathType.PathFilter.PathFilterPart.FilterPartMode mode = PathType.PathFilter.PathFilterPart.FilterPartMode.UNSPECIFIED;
            ArrayList<Object> part = new ArrayList<>();

            void changeMode(PathType.PathFilter.PathFilterPart.FilterPartMode filterPartMode) {
                if (mode == PathType.PathFilter.PathFilterPart.FilterPartMode.UNSPECIFIED)
                    mode = filterPartMode;
                else
                    throw new PathParseException("Unexpected filter mode");
            }
        }
    }
}

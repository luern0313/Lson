package cn.luern0313.lson.path;

import java.util.ArrayList;

/**
 * 被 luern0313 创建于 2020/8/10.
 */

public class PathType {
    public static class PathJsonRoot {
        @Override
        public String toString() {
            return "PathJsonRoot{}";
        }
    }

    public static class PathJsonCurrent {
        @Override
        public String toString() {
            return "PathJsonCurrent{}";
        }
    }

    public static class PathIndex {
        private static final int DEF_START = 0;
        private static final int DEF_END = Integer.MAX_VALUE;
        private static final int DEF_STEP = 1;

        public int start;
        public int end;
        public int step;

        public PathIndex(int start) {
            this(start, DEF_END);
        }

        public PathIndex(int start, int end) {
            this(start, end, DEF_STEP);
        }

        public PathIndex(int start, int end, int step) {
            if (start == Integer.MIN_VALUE)
                start = DEF_START;
            if (end == Integer.MIN_VALUE)
                end = DEF_END;
            if (step == Integer.MIN_VALUE)
                step = DEF_STEP;

            this.start = start;
            this.end = end;
            this.step = step;
        }

        @Override
        public String toString() {
            return "PathIndex{" + "start=" + start + ", end=" + end + ", step=" + step + '}';
        }
    }

    public static class PathIndexArray {
        public ArrayList<Object> index;

        public PathIndexArray(ArrayList<Object> index) {
            this.index = index;
        }

        @Override
        public String toString() {
            return "PathIndexArray{" + "index=" + index + '}';
        }
    }

    public static class PathPath {
        public String path;

        public PathPath(String path) {
            this.path = path;
        }

        @Override
        public String toString() {
            return "PathPath{" + "path='" + path + '\'' + '}';
        }
    }

    public static class PathFilter {
        public FilterComparator comparator;
        public PathFilterPart left;
        public PathFilterPart right;

        public PathFilter(StackValue.Filter filter) {
            this.comparator = filter.comparator;
            this.left = new PathFilterPart(filter.left.mode, filter.left.part);
            this.right = new PathFilterPart(filter.right.mode, filter.right.part);
        }

        public enum FilterComparator {
            EXISTENCE,
            EQUAL,
            NOT_EQUAL,
            LESS,
            LESS_EQUAL,
            GREATER,
            GREATER_EQUAL,
            REGULAR,
            IN,
            NOT_IN
        }

        public static class PathFilterPart {
            public FilterPartMode mode;
            public ArrayList<Object> part;

            public PathFilterPart(FilterPartMode mode, ArrayList<Object> part) {
                this.mode = mode;
                this.part = part;
            }

            public enum FilterPartMode {
                UNSPECIFIED,
                PATH,
                ARRAY,
                SINGLE
            }

            @Override
            public String toString() {
                return "PathFilterPart{" + "mode=" + mode + ", part=" + part + '}';
            }
        }

        @Override
        public String toString() {
            return "PathFilter{" + "comparator=" + comparator + ", left=" + left + ", right=" + right + '}';
        }
    }
}

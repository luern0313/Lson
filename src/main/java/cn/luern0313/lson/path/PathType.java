package cn.luern0313.lson.path;

import java.util.ArrayList;

/**
 * 被 luern0313 创建于 2020/8/10.
 */

public class PathType
{
    public static class PathJsonRoot
    {
        @Override
        public String toString()
        {
            return "PathJsonRoot{}";
        }
    }

    public static class PathIndex
    {
        private static final int DEF_START = 0;
        private static final int DEF_END = Integer.MAX_VALUE;
        private static final int DEF_STEP = 1;

        public int start;
        public int end;
        public int step;

        public PathIndex(int start)
        {
            this(start, DEF_END);
        }

        public PathIndex(int start, int end)
        {
            this(start, end, DEF_STEP);
        }

        public PathIndex(int start, int end, int step)
        {
            if(start == Integer.MIN_VALUE)
                start = DEF_START;
            if(end == Integer.MIN_VALUE)
                end = DEF_END;
            if(step == Integer.MIN_VALUE)
                step = DEF_STEP;

            this.start = start;
            this.end = end;
            this.step = step;
        }

        @Override
        public String toString()
        {
            return "PathIndex{" + "start=" + start + ", end=" + end + ", step=" + step + '}';
        }
    }

    public static class PathIndexArray
    {
        public ArrayList<Integer> index;

        public PathIndexArray(ArrayList<Integer> index)
        {
            this.index = index;
        }

        @Override
        public String toString()
        {
            return "PathIndexArray{" + "index=" + index + '}';
        }
    }

    public static class PathPath
    {
        public String path;

        public PathPath(String path)
        {
            this.path = path;
        }

        @Override
        public String toString()
        {
            return "PathPath{" + "path='" + path + '\'' + '}';
        }
    }

}

package cn.luern0313.lson.json;

import java.util.Arrays;

import cn.luern0313.lson.exception.PathParseException;

/**
 * 被 luern0313 创建于 2020/8/22.
 */

class Stack
{
    final int SIZE;
    final StackValue[] array;
    int pos = 0;

    public Stack()
    {
        this.SIZE = 100;
        this.array = new StackValue[this.SIZE];
    }

    boolean isEmpty()
    {
        return pos == 0;
    }

    void push(StackValue obj)
    {
        if(pos == SIZE)
            throw new PathParseException("Maximum depth reached.");
        array[pos] = obj;
        pos++;
    }

    StackValue pop()
    {
        if(isEmpty())
            throw new PathParseException();
        pos--;
        return array[pos];
    }

    StackValue pop(int type)
    {
        if(isEmpty())
            throw new PathParseException();
        pos--;
        StackValue obj = array[pos];
        if(obj.type == type)
            return obj;
        throw new PathParseException("Unmatched type.");
    }

    Class<?> getTopValueClass()
    {
        StackValue obj = array[pos - 1];
        return obj.value.getClass();
    }

    int getTopValueType()
    {
        if(!isEmpty())
        {
            StackValue obj = array[pos - 1];
            return obj.type;
        }
        return -1;
    }

    StackValue peek()
    {
        if(isEmpty())
            return null;
        return array[pos - 1];
    }

    StackValue peek(int type)
    {
        if(isEmpty())
            return null;
        StackValue obj = array[pos - 1];
        if(obj.type == type)
        {
            return obj;
        }
        throw new PathParseException("Unmatched type " + type);
    }

    @Override
    public String toString()
    {
        return "Stack{" + "SIZE=" + SIZE + ", array=" + Arrays.toString(array) + ", pos=" + pos + '}';
    }
}

package cn.luern0313.lson.util;

import java.io.IOException;
import java.io.Reader;

import cn.luern0313.lson.exception.PathParseException;

/**
 * 被 luern0313 创建于 2020/8/22.
 */

public class CharReaderUtil {
    static final int BUFFER_SIZE = 1024;

    public int readed = 0;
    public int pos = 0;
    public int size = 0;

    final char[] buffer;
    final Reader reader;

    public CharReaderUtil(Reader reader) {
        this.buffer = new char[BUFFER_SIZE];
        this.reader = reader;
    }

    public boolean hasMore() {
        if (pos < size)
            return true;
        fillBuffer(null);
        return pos < size;
    }

    public String next(int size) {
        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < size; i++)
            sb.append(next());
        return sb.toString();
    }

    public char next() {
        if (this.pos == this.size)
            fillBuffer("EOF");
        char ch = this.buffer[this.pos];
        this.pos++;
        return ch;
    }

    public char peek() {
        if (this.pos == this.size)
            fillBuffer("EOF");
        if (this.pos < this.size)
            return this.buffer[this.pos];
        else
            return (char) 0;
    }

    public ErrorMessage getErrorMessage() {
        ErrorMessage errorMessage = new ErrorMessage(this.pos);
        int index = Math.min(10, this.pos);
        this.pos -= index;
        errorMessage.message = next(Math.min(this.size - this.pos, index + 10));
        errorMessage.messageErrorIndex = index;
        return errorMessage;
    }

    void fillBuffer(String eofErrorMessage) {
        try {
            int n = reader.read(buffer);
            if (n == -1) {
                if (eofErrorMessage != null)
                    throw new PathParseException(eofErrorMessage, getErrorMessage());
                return;
            }
            this.pos = 0;
            this.size = n;
            this.readed += n;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class ErrorMessage {
        public String message;
        public long index;
        public int messageErrorIndex;

        public ErrorMessage(long index) {
            this.index = index;
        }
    }
}

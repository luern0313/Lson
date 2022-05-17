package cn.luern0313.lson.element;

/**
 * JSON null类。
 *
 * @author luern0313
 */

public class LsonNull extends LsonElement {
    public static LsonNull getJsonNull() {
        return new LsonNull();
    }

    @Override
    public boolean isLsonNull() {
        return true;
    }

    @Override
    public LsonElement deepCopy() {
        return this;
    }

    @Override
    public String toString() {
        return "null";
    }
}

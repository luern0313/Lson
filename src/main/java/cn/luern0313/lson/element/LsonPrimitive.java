package cn.luern0313.lson.element;

/**
 * JSON元素类。
 *
 * @author luern0313
 */

public class LsonPrimitive extends LsonElement {
    private final Object value;

    public LsonPrimitive(Object value) {
        this.value = value;
    }

    public boolean isNull(int index) {
        return get() == null;
    }

    public Object get() {
        return value;
    }

    public boolean isBoolean() {
        return value instanceof Boolean;
    }

    public boolean isString() {
        return value instanceof String;
    }

    public boolean isNumber() {
        return value instanceof Number;
    }

    public boolean isInt() {
        return value instanceof Integer;
    }

    public boolean isByte() {
        return value instanceof Byte;
    }

    public boolean isShort() {
        return value instanceof Short;
    }

    public boolean isFloat() {
        return value instanceof Float;
    }

    public boolean isDouble() {
        return value instanceof Double;
    }

    public boolean isLong() {
        return value instanceof Long;
    }

    public boolean getAsBoolean() {
        if (isBoolean())
            return (Boolean) value;
        return Boolean.parseBoolean(getAsString());
    }

    public String getAsString() {
        if (isBoolean())
            return String.valueOf(getAsBoolean());
        else if (isNumber())
            return String.valueOf(getAsNumber().toString());
        return (String) value;
    }

    public Number getAsNumber() {
        if (isString())
            return Double.parseDouble((String) value);
        return (Number) value;
    }

    public int getAsInt() {
        if (isNumber())
            return getAsNumber().intValue();
        return Integer.parseInt(getAsString());
    }

    public byte getAsByte() {
        if (isNumber())
            return getAsNumber().byteValue();
        return Byte.parseByte(getAsString());
    }

    public short getAsShort() {
        if (isNumber())
            return getAsNumber().shortValue();
        return Short.parseShort(getAsString());
    }

    public float getAsFloat() {
        if (isNumber())
            return getAsNumber().floatValue();
        return Float.parseFloat(getAsString());
    }

    public double getAsDouble() {
        if (isNumber())
            return getAsNumber().doubleValue();
        return Double.parseDouble(getAsString());
    }

    public long getAsLong() {
        if (isNumber())
            return getAsNumber().longValue();
        return Long.parseLong(getAsString());
    }

    public Class<?> getValueClass() {
        return value.getClass();
    }

    @Override
    public String toString() {
        if (isString())
            return "\"" + getAsString() + "\"";
        return getAsString();
    }

    @Override
    public boolean isLsonPrimitive() {
        return true;
    }

    @Override
    public LsonPrimitive getAsLsonPrimitive() {
        return this;
    }

    @Override
    public LsonElement deepCopy() {
        return this;
    }
}

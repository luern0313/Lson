package cn.luern0313.lson.util;

/**
 * 被 luern0313 创建于 2020/9/11.
 */

public class DeserializationValueUtil {
    private Object value;
    private final TypeUtil type;

    public DeserializationValueUtil() {
        type = null;
    }

    public DeserializationValueUtil(String string) {
        this(string, null);
    }

    public DeserializationValueUtil(Object value) {
        TypeUtil typeUtil = new TypeUtil(value.getClass());
        if (typeUtil.getAsClass() == StringBuilder.class)
            typeUtil.setType(String.class);

        this.value = value;
        this.type = typeUtil;
    }

    public DeserializationValueUtil(Object value, Class<?> type) {
        TypeUtil typeUtil = new TypeUtil(value.getClass());
        if (typeUtil.isString())
            this.value = new StringBuilder(value.toString());
        else if (typeUtil.isNumber())
            this.value = ((Number) value).doubleValue();
        else
            this.value = value;
        this.type = new TypeUtil(type);
    }

    public Object get() {
        return value;
    }

    public Object get(TypeUtil typeUtil) {
        return typeUtil.getAsClass().cast(value);
    }

    public Class<?> getType() {
        return type.getAsClass();
    }

    public Class<?> getCurrentType() {
        return value.getClass();
    }

    public DeserializationValueUtil set(Object value) {
        if (this.value instanceof StringBuilder && value instanceof String) {
            ((StringBuilder) this.value).setLength(0);
            ((StringBuilder) this.value).append(value);
        } else if (value instanceof Number) {
            this.value = ((Number) value).doubleValue();
            if (Math.floor(((Number) value).byteValue()) < ((Number) value).doubleValue())
                this.type.setType(double.class);
        } else
            this.value = value;
        return this;
    }

    public StringBuilder getAsStringBuilder() {
        if (value instanceof StringBuilder)
            return (StringBuilder) value;
        else
            return new StringBuilder(toString());
    }

    public Number getAsNumber() {
        if (DataProcessUtil.isDouble(value.toString()))
            return Double.parseDouble(value.toString());
        return null;
    }

    public Boolean getAsBoolean() {
        if (value instanceof Boolean)
            return (Boolean) value;
        return null;
    }

    public boolean isNull() {
        return value == null;
    }

    @Override
    public String toString() {
        if (value == null) return null;

        String string = value.toString();
        if (DataProcessUtil.isDouble(string)) {
            double number = Double.parseDouble(string);
            switch (type.getName()) {
                case "int":
                case "java.lang.Integer":
                    return String.valueOf((int) number);
                case "short":
                case "java.lang.Short":
                    return String.valueOf((short) number);
                case "long":
                case "java.lang.Long":
                    return String.valueOf((long) number);
                case "float":
                case "java.lang.Float":
                    return String.valueOf((float) number);
            }
        }
        return string;
    }
}

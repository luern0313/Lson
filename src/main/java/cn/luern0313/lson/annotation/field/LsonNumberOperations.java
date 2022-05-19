package cn.luern0313.lson.annotation.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.luern0313.lson.annotation.LsonDefinedAnnotation;
import cn.luern0313.lson.annotation.other.AnnotationOrder;

/**
 * 对数字进行数学运算。
 *
 * <p>注明运算符和数字时，请以反序列化为标准注明。序列化时会自动反向运算。
 *
 * <p>{@link LsonNumberOperations#isCastInteger()} 为true时可以在反序列化时将结果向下求整，
 * 但序列化时将不会恢复，请注意。
 *
 * <p>反序列化中：输入{@code Number}类型，输出{@code Number}类型。
 * <p>序列化中：输入{@code Number}类型，输出{@code Number}类型。
 *
 * @author luern0313
 */

@LsonDefinedAnnotation(config = LsonNumberOperations.LsonNumberOperationsConfig.class,
        acceptableDeserializationType = LsonDefinedAnnotation.AcceptableType.NUMBER,
        acceptableSerializationType = LsonDefinedAnnotation.AcceptableType.NUMBER)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LsonNumberOperations {
    /**
     * 要对数字进行运算的运算符。
     *
     * @return 运算符。
     */
    Operator operator();

    /**
     * 要对被注解数进行运算的右侧数字，如加数/减数等。
     *
     * @return 算式中右侧的数字。
     */
    double number();

    /**
     * 是否将最终数字转为int，可去除小数点和小数点后的数字。
     *
     * <p>通常在计算数字后结果为整数时使用，若不为整数则将向下取整。
     *
     * @return 是否转为Integer
     */
    boolean isCastInteger() default false;

    /**
     * 用于排序注解的执行顺序，见{@link AnnotationOrder}。
     *
     * @return 注解执行顺序
     */
    @AnnotationOrder int order() default Integer.MAX_VALUE;

    //TODO 支持求余等更多运算符
    enum Operator {
        /**
         * 加
         */
        ADD,

        /**
         * 减
         */
        MINUS,

        /**
         * 乘
         */
        MULTIPLY,

        /**
         * 除
         */
        DIVISION
    }

    class LsonNumberOperationsConfig implements LsonDefinedAnnotation.LsonDefinedAnnotationConfig<LsonNumberOperations> {
        @Override
        public Object deserialization(Object value, LsonNumberOperations lsonNumberOperations, Object object) {
            double result = operationsHandler((Double) value, lsonNumberOperations.operator(), lsonNumberOperations.number());
            if (lsonNumberOperations.isCastInteger())
                return (int) result;
            else
                return result;
        }

        @Override
        public Object serialization(Object value, LsonNumberOperations lsonNumberOperations, Object object) {
            Operator operator = Operator.values()[lsonNumberOperations.operator().ordinal() - (lsonNumberOperations.operator().ordinal() % 2 * 2 - 1)];
            return operationsHandler((Double) value, operator, lsonNumberOperations.number());
        }

        private Double operationsHandler(double left, Operator operator, double right) {
            if (operator == Operator.ADD)
                return left + right;
            else if (operator == Operator.MINUS)
                return left - right;
            else if (operator == Operator.MULTIPLY)
                return left * right;
            else if (operator == Operator.DIVISION)
                return left / right;
            return 0d;
        }
    }
}

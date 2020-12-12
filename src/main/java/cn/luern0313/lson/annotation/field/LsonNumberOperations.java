package cn.luern0313.lson.annotation.field;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.luern0313.lson.annotation.LsonDefinedAnnotation;

/**
 * 对数字进行数学运算。
 *
 * <p>反序列化中：输入{@code Number}类型，输出{@code Number}类型。
 * <p>序列化中：输入{@code Number}类型，输出{@code Number}类型。
 */

@LsonDefinedAnnotation(config = LsonNumberOperations.LsonNumberOperationsConfig.class, acceptableDeserializationType = LsonDefinedAnnotation.AcceptableType.NUMBER, acceptableSerializationType = LsonDefinedAnnotation.AcceptableType.NUMBER)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LsonNumberOperations
{
    Operator operator();

    double number();

    enum Operator
    {
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

    class LsonNumberOperationsConfig implements LsonDefinedAnnotation.LsonDefinedAnnotationConfig
    {
        @Override
        public Object deserialization(Object value, Annotation annotation, Object object)
        {
            return operationsHandler((Double) value, ((LsonNumberOperations) annotation).operator(), ((LsonNumberOperations) annotation).number());
        }

        @Override
        public Object serialization(Object value, Annotation annotation, Object object)
        {
            Operator operator = Operator.values()[((LsonNumberOperations) annotation).operator().ordinal() - (((LsonNumberOperations) annotation).operator().ordinal() % 2 * 2 - 1)];
            return operationsHandler(((LsonNumberOperations) annotation).number(), operator, (Double) value);
        }

        private double operationsHandler(double left, Operator operator, double right)
        {
            if(operator == Operator.ADD)
                return left + right;
            else if(operator == Operator.MINUS)
                return left - right;
            else if(operator == Operator.MULTIPLY)
                return left * right;
            else if(operator == Operator.DIVISION)
                return left / right;
            return 0;
        }
    }
}

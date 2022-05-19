package cn.luern0313.lson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 此注解用来标记Lson注解，包括内置注解或开发者自定义的注解。
 *
 * <p>开发者需要创建一个类，并实现接口{@link LsonDefinedAnnotationConfig}，用来在反序列化和序列化中
 * 处理并返回注解处理后的值，并在{@link LsonDefinedAnnotation#config()}注明该类的Class。
 *
 * <p>自定义的注解需要标记此注解，并在你实体类需要的变量上添加此注解。Lson在处理时会将值与你的
 * 自定义注解传入你的注解处理类，由你自行处理。
 *
 * @author luern0313
 */

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LsonDefinedAnnotation {
    /**
     * 注明用来处理注解类的Class，该类需要实现接口{@link LsonDefinedAnnotationConfig}。
     *
     * @return 处理注解类的Class。
     */
    Class<? extends LsonDefinedAnnotationConfig<?>> config();

    /**
     * 反序列化过程中，该注解<b>接受</b>的数据类型。
     *
     * <p>或 序列化过程中，该注解<b>输出</b>的数据类型。
     *
     * <p>Lson会先试着将该数据转为该类型，如不能转换则不执行本次注解处理。
     *
     * @return 类型数据
     */
    AcceptableType acceptableDeserializationType() default AcceptableType.NOT_HANDLE;

    /**
     * 序列化过程中，该注解<b>接受</b>的数据类型。
     *
     * <p>或 反序列化过程中，该注解<b>输出</b>的数据类型。
     *
     * <p>Lson会先试着将该数据转为该类型，如不能转换则不执行本次注解处理。
     *
     * @return 类型数据
     */
    AcceptableType acceptableSerializationType() default AcceptableType.NOT_HANDLE;

    /**
     * 标记此自定义注解是否需要忽略数组类型。
     *
     * <p>开发者自定义注解如标记了数组类型的变量，默认情况下，Lson会逐个将数组内元素交由你的
     * 注解处理类处理，但如果设置为true，Lson会忽略数组类型，将整个变量传入你的注解处理类。
     *
     * @return 是否忽略数组。
     */
    boolean isIgnoreArray() default false;

    /**
     * 标记此自定义注解是否需要忽略{@link java.util.List}及相关类型。
     *
     * <p>开发者自定义注解如标记了{@link java.util.List}类型的变量，默认情况下，Lson会逐个将
     * 数组内元素交由你的注解处理类处理，但如果设置为true，Lson会忽略数组类型，将整个变量传入
     * 你的注解处理类。
     *
     * @return 是否忽略List及相关类型。
     */
    boolean isIgnoreList() default false;

    /**
     * 标记此自定义注解是否需要忽略{@link java.util.Map}及相关类型。
     *
     * <p>开发者自定义注解如标记了{@link java.util.Map}类型的变量，默认情况下，Lson会逐个将
     * 数组内元素交由你的注解处理类处理，但如果设置为true，Lson会忽略数组类型，将整个变量传入
     * 你的注解处理类。
     *
     * @return 是否忽略Map及相关类型。
     */
    boolean isIgnoreMap() default false;

    enum AcceptableType {
        NOT_HANDLE,
        STRING,
        NUMBER,
        BOOLEAN
    }

    /**
     * @param <Annotation> 该注解的类型
     */
    interface LsonDefinedAnnotationConfig<Annotation> {
        /**
         * 在反序列化过程中处理注解。
         *
         * @param value      处理前的值，该值类型与{@link LsonDefinedAnnotation#acceptableDeserializationType()}
         *                   注明的类型相同。
         * @param annotation 此注解类的实例。
         * @param object     反序列化目标类的实例。
         * @return 处理完成的值。
         */
        Object deserialization(Object value, Annotation annotation, Object object);

        /**
         * 在序列化过程中处理注解。
         *
         * @param value      处理前的值，该值类型与{@link LsonDefinedAnnotation#acceptableDeserializationType()}
         *                   注明的类型相同。
         * @param annotation 此注解类的实例。
         * @param object     要序列化的目标对象。
         * @return 处理完成的值。
         */
        Object serialization(Object value, Annotation annotation, Object object);
    }
}

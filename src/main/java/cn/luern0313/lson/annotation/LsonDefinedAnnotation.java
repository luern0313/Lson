package cn.luern0313.lson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.luern0313.lson.LsonUtil;

/**
 * 此注解用来标记开发者自定义的Lson注解。
 *
 * <p>开发者需要创建一个类，并实现接口{@link LsonUtil.LsonAnnotationListener}，在程序开始时
 * 通过静态方法{@link LsonUtil#setLsonAnnotationListener(LsonUtil.LsonAnnotationListener)}
 * 传入该类的实例。
 *
 * <p>自定义的注解需要标记此注解，并在你实体类需要的变量上添加此注解。Lson在处理时会将值与你的
 * 自定义注解传入你的注解处理类，由你自行处理。
 *
 * @author luern0313
 */

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LsonDefinedAnnotation
{
    /**
     * 反序列化过程中，该注解处理接受的数据类型。
     *
     * <p>Lson会先试着将该数据转为该类型，如不能转换则不进行处理。
     *
     * @return 类型数据
     *
     * @author luern0313
     */
    AcceptableType acceptableDeserializationType() default AcceptableType.NOT_HANDLE;

    /**
     * 序列化过程中，该注解处理接受的数据类型。
     *
     * <p>Lson会先试着将该数据转为该类型，如不能转换则不进行处理。
     * <p>由于Lson会以反序列化相反的过程执行序列化，以相反的过程
     *
     * @return 类型数据
     *
     * @author luern0313
     */
    AcceptableType acceptableSerializationType() default AcceptableType.NOT_HANDLE;

    /**
     * 标记此自定义注解是否需要忽略数组类型。
     *
     * <p>开发者自定义注解如标记了数组类型的变量，默认情况下，Lson会逐个将数组内元素交由你的
     * 注解处理类处理，但如果设置为true，Lson会忽略数组类型，将整个变量传入你的注解处理类。
     *
     * @return 是否忽略数组。
     *
     * @author luern0313
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
     *
     * @author luern0313
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
     *
     * @author luern0313
     */
    boolean isIgnoreMap() default false;

    enum AcceptableType
    {
        NOT_HANDLE,
        STRING,
        NUMBER
    }
}

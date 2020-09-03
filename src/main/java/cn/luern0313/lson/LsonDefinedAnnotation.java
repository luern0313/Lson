package cn.luern0313.lson;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
     * 注解可用于变量的类型的白名单。
     *
     * <p>可传入一个或多个{@link Class}类，如变量类型不在列表中，则抛出若默认则不做任何限制。
     *
     * @return 白名单数组。
     *
     * @author luern0313
     */
    Class<?>[] applyTypeWhiteList() default {};

    /**
     * 注解可用于变量的类型的黑名单。
     *
     * <p>可传入一个或多个{@link Class}类，若默认则不做任何限制。
     *
     * @return 黑名单数组。
     *
     * @author luern0313
     */
    Class<?>[] applyTypeBlackList() default {};

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
}

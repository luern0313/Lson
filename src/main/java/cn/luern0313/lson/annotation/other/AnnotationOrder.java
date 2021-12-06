package cn.luern0313.lson.annotation.other;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.luern0313.lson.annotation.field.LsonPath;

/**
 * 用于注解之间执行顺序的排序
 *
 * <p>由于java获取变量注解列表时并不能保证注解的顺序与定义的顺序一致，在自定义注解时，
 * 添加一个变量并注解{@link AnnotationOrder}。在使用时如实体类变量有两个或以上注解修
 * 饰时（{@link LsonPath}除外），手动填写每个注解中被{@link AnnotationOrder}修饰的
 * 变量，Lson处理时会按顺序重新排序并执行。
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AnnotationOrder
{
}

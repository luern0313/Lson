package cn.luern0313.lson.annotation.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定json中的路径，用于填充至指定的变量。
 *
 * <p>所有需要反序列化的变量必须标注此注解并填写路径。
 *
 * @author luern0313
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LsonPath
{
    /**
     * json路径数组。
     *
     * <p>你可以填写多个路径作为备份，若首个路径未查找到对应值，会使用备份路径查找，直到查找
     * 成功或所有路径查找完毕。
     *
     * @return path数组+
     */
    String[] value() default "";

    /**
     * 预处理为指定的类型。
     *
     * <p>这个类型决定了Lson在处理该变量所有的注解前，会将在json中取到的变量转为什么类型。
     *
     * <p>若为默认的{@link Object#getClass()}，Lson会以目标变量类型作为转换依据。
     *
     * @return 预处理转换的类型。
     */
    Class<?> preClass() default Object.class;

    /**
     * 注解执行的顺序。
     *
     * <p>由于java获取变量注解并不能保证注解的顺序与定义的顺序一致，所以建议在变量有两个或
     * 以上注解（{@link LsonPath}除外），并且这些注解需要有执行的先后顺序时，手动设置这个
     * 参数，否则注解的处理可能出现问题。
     *
     * <p>注：自定义顺序中不需要加入{@link LsonPath}。
     *
     * @return 注解执行的顺序。
     */
    Class<?>[] annotationsOrder() default {};
}

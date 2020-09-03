package cn.luern0313.lson.annotation;

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
     * @return path数组
     *
     * @author luern0313
     */
    String[] value();
}

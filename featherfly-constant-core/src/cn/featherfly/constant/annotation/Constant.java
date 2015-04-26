
package cn.featherfly.constant.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 配置文件覆盖式常量的配置.
 * </p>
 * @author 钟冀
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Constant {
    /**
     * <p>
     * 该常量的描述.
     * </p>
     * @return
     */
    String value();
}

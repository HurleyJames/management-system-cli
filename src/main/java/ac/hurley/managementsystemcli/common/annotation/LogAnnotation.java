package ac.hurley.managementsystemcli.common.annotation;

import java.lang.annotation.*;

/**
 * @author hurley
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogAnnotation {

    /**
     * 模块
     *
     * @return
     */
    String title() default "";

    /**
     * 功能
     *
     * @return
     */
    String action() default "";
}

package my.project.forum.aop.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE ,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Loggable {
    String controller();
    String method();
}

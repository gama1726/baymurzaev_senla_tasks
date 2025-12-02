package autoservice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для внедрения зависимостей (Dependency Injection).
 * Помечается поле, в которое необходимо внедрить зависимость.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {
    /**
     * Опциональное имя компонента для внедрения.
     * Если не указано, используется тип поля.
     */
    String value() default "";
}


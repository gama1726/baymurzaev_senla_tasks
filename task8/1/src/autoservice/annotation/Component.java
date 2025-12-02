package autoservice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для пометки класса как компонента DI контейнера.
 * Компоненты автоматически регистрируются в контейнере и могут быть внедрены в другие компоненты.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
    /**
     * Имя компонента в контейнере.
     * По умолчанию: имя класса.
     */
    String value() default "";
    
    /**
     * Тип регистрации компонента.
     */
    Scope scope() default Scope.SINGLETON;
}


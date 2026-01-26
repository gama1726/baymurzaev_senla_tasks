package autoservice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для конфигурирования полей из property-файла.
 * 
 * Все атрибуты опциональны:
 * - configFileName: имя файла конфигурации (по умолчанию "config.properties")
 * - propertyName: имя свойства (по умолчанию "ИМЯ_КЛАССА.ИМЯ_ПОЛЯ")
 * - type: тип преобразования (по умолчанию определяется по типу поля)
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigProperty {
    /**
     * Имя файла конфигурации.
     * По умолчанию: "config.properties"
     */
    String configFileName() default "config.properties";
    
    /**
     * Имя свойства в файле конфигурации.
     * По умолчанию: "ИМЯ_КЛАССА.ИМЯ_ПОЛЯ"
     */
    String propertyName() default "";
    
    /**
     * Тип преобразования значения.
     * По умолчанию: определяется автоматически по типу поля
     */
    PropertyType type() default PropertyType.AUTO;
}


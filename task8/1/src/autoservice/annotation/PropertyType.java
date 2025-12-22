package autoservice.annotation;

/**
 * Типы преобразования значений свойств конфигурации.
 */
public enum PropertyType {
    /**
     * Автоматическое определение типа по типу поля.
     */
    AUTO,
    
    /**
     * Преобразование в String.
     */
    STRING,
    
    /**
     * Преобразование в Integer.
     */
    INTEGER,
    
    /**
     * Преобразование в Boolean.
     */
    BOOLEAN,
    
    /**
     * Преобразование в Long.
     */
    LONG,
    
    /**
     * Преобразование в Double.
     */
    DOUBLE
}


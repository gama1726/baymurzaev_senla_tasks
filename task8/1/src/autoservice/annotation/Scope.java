package autoservice.annotation;

/**
 * Область видимости компонента в DI контейнере.
 */
public enum Scope {
    /**
     * Один экземпляр на весь контейнер (Singleton).
     */
    SINGLETON,
    
    /**
     * Новый экземпляр при каждом запросе (Prototype).
     */
    PROTOTYPE
}


package autoservice.injection;

import autoservice.annotation.Component;
import autoservice.annotation.Inject;
import autoservice.annotation.Scope;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Контейнер для Dependency Injection.
 * Управляет жизненным циклом компонентов и внедрением зависимостей.
 */
public class DIContainer {
    private static DIContainer instance;
    private final Map<Class<?>, Object> singletons = new ConcurrentHashMap<>();
    private final Map<Class<?>, Class<?>> implementations = new ConcurrentHashMap<>();
    private final Set<Class<?>> registeredClasses = new HashSet<>();
    
    private DIContainer() {
    }
    
    public static DIContainer getInstance() {
        if (instance == null) {
            instance = new DIContainer();
        }
        return instance;
    }
    
    /**
     * Регистрирует класс как компонент.
     */
    public void register(Class<?> clazz) {
        registeredClasses.add(clazz);
        
        // Регистрируем все интерфейсы, которые реализует класс
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> iface : interfaces) {
            implementations.put(iface, clazz);
        }
        implementations.put(clazz, clazz);
    }
    
    /**
     * Регистрирует компонент по аннотации @Component.
     */
    public void registerComponent(Class<?> clazz) {
        Component annotation = clazz.getAnnotation(Component.class);
        if (annotation == null) {
            return;
        }
        
        register(clazz);
    }
    
    /**
     * Получает экземпляр компонента.
     */
    @SuppressWarnings("unchecked")
    public <T> T getInstance(Class<T> clazz) {
        // Проверяем, есть ли зарегистрированная реализация
        Class<?> implementation = implementations.get(clazz);
        if (implementation == null) {
            implementation = clazz;
        }
        
        Component annotation = implementation.getAnnotation(Component.class);
        Scope scope = (annotation != null) ? annotation.scope() : Scope.SINGLETON;
        
        if (scope == Scope.SINGLETON) {
            // Возвращаем singleton
            Object instance = singletons.get(implementation);
            if (instance == null) {
                instance = createInstance(implementation);
                singletons.put(implementation, instance);
            }
            return (T) instance;
        } else {
            // Создаем новый экземпляр
            return (T) createInstance(implementation);
        }
    }
    
    /**
     * Создает экземпляр класса и внедряет зависимости.
     */
    private Object createInstance(Class<?> clazz) {
        try {
            // Находим конструктор (предпочитаем конструктор без параметров)
            Constructor<?> constructor = findSuitableConstructor(clazz);
            Object instance = constructor.newInstance();
            
            // Внедряем зависимости в поля
            injectDependencies(instance);
            
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании экземпляра " + clazz.getName(), e);
        }
    }
    
    /**
     * Находит подходящий конструктор.
     */
    private Constructor<?> findSuitableConstructor(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getConstructors();
        
        // Ищем конструктор без параметров
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() == 0) {
                return constructor;
            }
        }
        
        // Если нет конструктора без параметров, берем первый доступный
        if (constructors.length > 0) {
            Constructor<?> constructor = constructors[0];
            // Пытаемся создать экземпляры для параметров конструктора
            return constructor;
        }
        
        throw new RuntimeException("Не найден подходящий конструктор для " + clazz.getName());
    }
    
    /**
     * Внедряет зависимости в поля объекта (публичный метод для использования извне).
     */
    public void injectDependencies(Object instance) {
        Class<?> clazz = instance.getClass();
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field field : fields) {
            Inject injectAnnotation = field.getAnnotation(Inject.class);
            if (injectAnnotation == null) {
                continue;
            }
            
            try {
                field.setAccessible(true);
                
                // Определяем тип зависимости
                Class<?> fieldType = field.getType();
                
                // Получаем экземпляр зависимости
                Object dependency = getInstance(fieldType);
                
                // Устанавливаем значение
                field.set(instance, dependency);
            } catch (Exception e) {
                throw new RuntimeException("Ошибка при внедрении зависимости в поле " + 
                                         field.getName() + " класса " + clazz.getName(), e);
            }
        }
    }
    
    /**
     * Регистрирует готовый экземпляр как singleton.
     */
    public <T> void registerInstance(Class<T> clazz, T instance) {
        singletons.put(clazz, instance);
        implementations.put(clazz, clazz);
    }
    
    /**
     * Очищает контейнер.
     */
    public void clear() {
        singletons.clear();
        implementations.clear();
        registeredClasses.clear();
    }
}


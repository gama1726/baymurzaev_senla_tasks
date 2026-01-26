package autoservice.injection;

import autoservice.annotation.ConfigProperty;
import autoservice.annotation.PropertyType;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Инжектор конфигурации из property-файлов.
 * Обрабатывает аннотацию @ConfigProperty и заполняет поля значениями из конфигурационных файлов.
 */
public class ConfigInjector {
    
    /**
     * Заполняет поля объекта значениями из конфигурационных файлов на основе аннотаций @ConfigProperty.
     * 
     * @param object объект для заполнения конфигурацией
     * @throws Exception если произошла ошибка при чтении конфигурации или установке значений
     */
    public static void injectConfig(Object object) throws Exception {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field field : fields) {
            ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
            if (annotation == null) {
                continue;
            }
            
            // Определяем имя файла конфигурации
            String configFileName = annotation.configFileName();
            if (configFileName.isEmpty()) {
                configFileName = "config.properties";
            }
            
            // Определяем имя свойства
            String propertyName = annotation.propertyName();
            if (propertyName.isEmpty()) {
                propertyName = clazz.getSimpleName() + "." + field.getName();
            }
            
            // Загружаем свойства из файла
            Properties properties = loadProperties(configFileName);
            String propertyValue = properties.getProperty(propertyName);
            
            if (propertyValue == null) {
                System.out.println("Предупреждение: свойство '" + propertyName + "' не найдено в файле " + configFileName);
                continue;
            }
            
            // Устанавливаем значение в поле
            setFieldValue(object, field, propertyValue, annotation.type());
        }
    }
    
    /**
     * Загружает свойства из файла.
     * Пытается найти файл в разных местах: ресурсы, src/, корень проекта.
     */
    private static Properties loadProperties(String fileName) throws IOException {
        Properties properties = new Properties();
        
        // Пытаемся загрузить из разных мест
        InputStream is = null;
        
        // 1. Пытаемся загрузить из ресурсов (classpath)
        is = ConfigInjector.class.getClassLoader().getResourceAsStream(fileName);
        
        // 2. Если не найден в ресурсах, пытаемся из файловой системы
        if (is == null) {
            // Пробуем src/config.properties
            java.io.File file = new java.io.File("src/" + fileName);
            if (file.exists()) {
                is = new FileInputStream(file);
            }
        }
        
        // 3. Пробуем в корне проекта
        if (is == null) {
            java.io.File file = new java.io.File(fileName);
            if (file.exists()) {
                is = new FileInputStream(file);
            }
        }
        
        if (is == null) {
            throw new IOException("Не удалось найти файл конфигурации: " + fileName + 
                " (проверьте src/" + fileName + " или добавьте в resources)");
        }
        
        try (InputStream inputStream = is) {
            properties.load(inputStream);
        }
        return properties;
    }
    
    /**
     * Устанавливает значение в поле с учетом типа.
     */
    private static void setFieldValue(Object object, Field field, String value, PropertyType propertyType) throws Exception {
        field.setAccessible(true);
        
        Class<?> fieldType = field.getType();
        Object convertedValue = convertValue(value, fieldType, propertyType, field);
        
        field.set(object, convertedValue);
    }
    
    /**
     * Преобразует строковое значение в нужный тип.
     */
    private static Object convertValue(String value, Class<?> targetType, PropertyType propertyType, Field field) throws Exception {
        // Если указан явный тип, используем его
        if (propertyType != PropertyType.AUTO) {
            return convertByPropertyType(value, propertyType);
        }
        
        // Автоматическое определение типа
        if (targetType == String.class) {
            return value;
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(value);
        } else if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(value);
        } else if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(value);
        } else if (targetType == List.class || targetType == ArrayList.class) {
            return parseList(value, field);
        } else if (targetType == Set.class || targetType == HashSet.class) {
            return parseSet(value, field);
        } else if (targetType.isArray()) {
            return parseArray(value, targetType.getComponentType());
        } else {
            // По умолчанию возвращаем String
            return value;
        }
    }
    
    /**
     * Преобразует значение по указанному типу PropertyType.
     */
    private static Object convertByPropertyType(String value, PropertyType type) {
        switch (type) {
            case STRING:
                return value;
            case INTEGER:
                return Integer.parseInt(value);
            case BOOLEAN:
                return Boolean.parseBoolean(value);
            case LONG:
                return Long.parseLong(value);
            case DOUBLE:
                return Double.parseDouble(value);
            default:
                return value;
        }
    }
    
    /**
     * Парсит строку в список.
     */
    private static List<?> parseList(String value, Field field) {
        Type genericType = field.getGenericType();
        Class<?> elementType = String.class;
        
        if (genericType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) genericType;
            Type[] actualTypes = paramType.getActualTypeArguments();
            if (actualTypes.length > 0 && actualTypes[0] instanceof Class) {
                elementType = (Class<?>) actualTypes[0];
            }
        }
        
        List<Object> list = new ArrayList<>();
        String[] parts = value.split(",");
        for (String part : parts) {
            list.add(convertSimpleValue(part.trim(), elementType));
        }
        return list;
    }
    
    /**
     * Парсит строку в множество.
     */
    private static Set<?> parseSet(String value, Field field) {
        Type genericType = field.getGenericType();
        Class<?> elementType = String.class;
        
        if (genericType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) genericType;
            Type[] actualTypes = paramType.getActualTypeArguments();
            if (actualTypes.length > 0 && actualTypes[0] instanceof Class) {
                elementType = (Class<?>) actualTypes[0];
            }
        }
        
        Set<Object> set = new HashSet<>();
        String[] parts = value.split(",");
        for (String part : parts) {
            set.add(convertSimpleValue(part.trim(), elementType));
        }
        return set;
    }
    
    /**
     * Парсит строку в массив.
     */
    private static Object parseArray(String value, Class<?> componentType) {
        String[] parts = value.split(",");
        Object[] array = (Object[]) java.lang.reflect.Array.newInstance(componentType, parts.length);
        for (int i = 0; i < parts.length; i++) {
            array[i] = convertSimpleValue(parts[i].trim(), componentType);
        }
        return array;
    }
    
    /**
     * Преобразует простое значение в нужный тип.
     */
    private static Object convertSimpleValue(String value, Class<?> type) {
        if (type == String.class) {
            return value;
        } else if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        } else if (type == long.class || type == Long.class) {
            return Long.parseLong(value);
        } else if (type == double.class || type == Double.class) {
            return Double.parseDouble(value);
        }
        return value;
    }
}


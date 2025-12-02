package autoservice.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Менеджер конфигурации приложения.
 * Загружает настройки из property-файла и предоставляет доступ к ним.
 */
public class ConfigurationManager {
    private static final String CONFIG_FILE = "config.properties";
    private static ConfigurationManager instance;
    private final Properties properties;

    private ConfigurationManager() {
        this.properties = new Properties();
        loadConfiguration();
    }

    public static ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    /**
     * Загружает конфигурацию из файла.
     * Если файл не существует, создает файл с настройками по умолчанию.
     */
    private void loadConfiguration() {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
            System.out.println("Конфигурация загружена из " + CONFIG_FILE);
        } catch (IOException e) {
            // Файл не существует, создаем с настройками по умолчанию
            System.out.println("Файл конфигурации не найден. Создаю файл с настройками по умолчанию.");
            setDefaultProperties();
            saveConfiguration();
        }
    }

    /**
     * Устанавливает настройки по умолчанию.
     */
    private void setDefaultProperties() {
        properties.setProperty("garage.slot.add.remove.enabled", "true");
        properties.setProperty("order.shift.enabled", "true");
        properties.setProperty("order.delete.enabled", "true");
    }

    /**
     * Сохраняет конфигурацию в файл.
     */
    public void saveConfiguration() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, "Autoservice Configuration");
            System.out.println("Конфигурация сохранена в " + CONFIG_FILE);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении конфигурации: " + e.getMessage());
        }
    }

    /**
     * Проверяет, разрешено ли добавлять/удалять гаражные места.
     */
    public boolean isGarageSlotAddRemoveEnabled() {
        return Boolean.parseBoolean(properties.getProperty("garage.slot.add.remove.enabled", "true"));
    }

    /**
     * Проверяет, разрешено ли смещать время выполнения заказов.
     */
    public boolean isOrderShiftEnabled() {
        return Boolean.parseBoolean(properties.getProperty("order.shift.enabled", "true"));
    }

    /**
     * Проверяет, разрешено ли удалять заказы.
     */
    public boolean isOrderDeleteEnabled() {
        return Boolean.parseBoolean(properties.getProperty("order.delete.enabled", "true"));
    }

    /**
     * Устанавливает разрешение на добавление/удаление гаражных мест.
     */
    public void setGarageSlotAddRemoveEnabled(boolean enabled) {
        properties.setProperty("garage.slot.add.remove.enabled", String.valueOf(enabled));
    }

    /**
     * Устанавливает разрешение на смещение времени заказов.
     */
    public void setOrderShiftEnabled(boolean enabled) {
        properties.setProperty("order.shift.enabled", String.valueOf(enabled));
    }

    /**
     * Устанавливает разрешение на удаление заказов.
     */
    public void setOrderDeleteEnabled(boolean enabled) {
        properties.setProperty("order.delete.enabled", String.valueOf(enabled));
    }
}


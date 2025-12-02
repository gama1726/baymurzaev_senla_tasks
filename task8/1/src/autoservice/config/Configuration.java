package autoservice.config;

import autoservice.annotation.ConfigProperty;

/**
 * Класс конфигурации приложения.
 * Поля заполняются автоматически из property-файла через аннотации @ConfigProperty.
 */
public class Configuration {
    
    @ConfigProperty(propertyName = "garage.slot.add.remove.enabled")
    private boolean garageSlotAddRemoveEnabled = true;
    
    @ConfigProperty(propertyName = "order.shift.enabled")
    private boolean orderShiftEnabled = true;
    
    @ConfigProperty(propertyName = "order.delete.enabled")
    private boolean orderDeleteEnabled = true;
    
    // Геттеры
    public boolean isGarageSlotAddRemoveEnabled() {
        return garageSlotAddRemoveEnabled;
    }
    
    public boolean isOrderShiftEnabled() {
        return orderShiftEnabled;
    }
    
    public boolean isOrderDeleteEnabled() {
        return orderDeleteEnabled;
    }
    
    // Сеттеры (для возможности изменения)
    public void setGarageSlotAddRemoveEnabled(boolean enabled) {
        this.garageSlotAddRemoveEnabled = enabled;
    }
    
    public void setOrderShiftEnabled(boolean enabled) {
        this.orderShiftEnabled = enabled;
    }
    
    public void setOrderDeleteEnabled(boolean enabled) {
        this.orderDeleteEnabled = enabled;
    }
}


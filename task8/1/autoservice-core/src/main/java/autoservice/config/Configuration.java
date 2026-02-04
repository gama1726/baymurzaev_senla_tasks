package autoservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Класс конфигурации приложения.
 * Поля заполняются из property-файла через аннотацию @Value (Spring PropertySourcesPlaceholderConfigurer).
 */
@Component
public class Configuration {

    @Value("${garage.slot.add.remove.enabled:true}")
    private boolean garageSlotAddRemoveEnabled = true;

    @Value("${order.shift.enabled:true}")
    private boolean orderShiftEnabled = true;

    @Value("${order.delete.enabled:true}")
    private boolean orderDeleteEnabled = true;

    public boolean isGarageSlotAddRemoveEnabled() {
        return garageSlotAddRemoveEnabled;
    }

    public boolean isOrderShiftEnabled() {
        return orderShiftEnabled;
    }

    public boolean isOrderDeleteEnabled() {
        return orderDeleteEnabled;
    }

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

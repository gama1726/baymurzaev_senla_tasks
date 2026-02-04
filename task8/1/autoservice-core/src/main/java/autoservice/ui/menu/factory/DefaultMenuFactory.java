package autoservice.ui.menu.factory;

import autoservice.service.ServiceManager;
import org.springframework.context.ApplicationContext;

/**
 * Конкретная реализация абстрактной фабрики меню.
 * Создает семейства фабрик для различных типов пунктов меню.
 */
public class DefaultMenuFactory implements AbstractMenuFactory {
    private final ServiceManager serviceManager;
    private final ApplicationContext applicationContext;

    public DefaultMenuFactory(ServiceManager serviceManager, ApplicationContext applicationContext) {
        this.serviceManager = serviceManager;
        this.applicationContext = applicationContext;
    }

    @Override
    public OrderMenuFactory createOrderMenuFactory() {
        return new DefaultOrderMenuFactory(serviceManager, applicationContext);
    }

    @Override
    public MechanicMenuFactory createMechanicMenuFactory() {
        return new DefaultMechanicMenuFactory(serviceManager, applicationContext);
    }

    @Override
    public GarageSlotMenuFactory createGarageSlotMenuFactory() {
        return new DefaultGarageSlotMenuFactory(serviceManager, applicationContext);
    }

    @Override
    public CapacityMenuFactory createCapacityMenuFactory() {
        return new DefaultCapacityMenuFactory(serviceManager, applicationContext);
    }
}

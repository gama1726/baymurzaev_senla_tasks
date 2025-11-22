package autoservice.ui.menu.factory;

import autoservice.service.ServiceManager;

/**
 * Конкретная реализация абстрактной фабрики меню.
 * Создает семейства фабрик для различных типов пунктов менюю.
 */
public class DefaultMenuFactory implements AbstractMenuFactory {
    private final ServiceManager serviceManager;

    public DefaultMenuFactory(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public OrderMenuFactory createOrderMenuFactory() {
        return new DefaultOrderMenuFactory(serviceManager);
    }

    @Override
    public MechanicMenuFactory createMechanicMenuFactory() {
        return new DefaultMechanicMenuFactory(serviceManager);
    }

    @Override
    public GarageSlotMenuFactory createGarageSlotMenuFactory() {
        return new DefaultGarageSlotMenuFactory(serviceManager);
    }

    @Override
    public CapacityMenuFactory createCapacityMenuFactory() {
        return new DefaultCapacityMenuFactory(serviceManager);
    }
}

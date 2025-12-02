package autoservice.ui.menu.factory;

import autoservice.injection.DIContainer;
import autoservice.service.ServiceManager;

/**
 * Конкретная реализация абстрактной фабрики меню.
 * Создает семейства фабрик для различных типов пунктов менюю.
 */
public class DefaultMenuFactory implements AbstractMenuFactory {
    private final ServiceManager serviceManager;
    private final DIContainer container;

    public DefaultMenuFactory(ServiceManager serviceManager, DIContainer container) {
        this.serviceManager = serviceManager;
        this.container = container;
    }

    @Override
    public OrderMenuFactory createOrderMenuFactory() {
        return new DefaultOrderMenuFactory(serviceManager, container);
    }

    @Override
    public MechanicMenuFactory createMechanicMenuFactory() {
        return new DefaultMechanicMenuFactory(serviceManager, container);
    }

    @Override
    public GarageSlotMenuFactory createGarageSlotMenuFactory() {
        return new DefaultGarageSlotMenuFactory(serviceManager, container);
    }

    @Override
    public CapacityMenuFactory createCapacityMenuFactory() {
        return new DefaultCapacityMenuFactory(serviceManager, container);
    }
}

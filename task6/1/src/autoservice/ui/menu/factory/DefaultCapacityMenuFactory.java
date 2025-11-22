package autoservice.ui.menu.factory;

import autoservice.service.ServiceManager;
import autoservice.ui.menu.*;

/**
 * Конкретная реализация фабрики для создания пунктов меню расчета свободных слотов пов ремени.
 */
public class DefaultCapacityMenuFactory implements CapacityMenuFactory {
    private final ServiceManager serviceManager;

    public DefaultCapacityMenuFactory(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public MenuItem createFreeCapacityAtItem() {
        return new MenuItem("Свободная ёмкость на дату",
                new FreeCapacityAtAction(serviceManager), null);
    }

    @Override
    public MenuItem createNextFreeDateItem() {
        return new MenuItem("Ближайшая свободная дата",
                new NextFreeDateAction(serviceManager), null);
    }
}


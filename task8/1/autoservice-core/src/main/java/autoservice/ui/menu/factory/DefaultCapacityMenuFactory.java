package autoservice.ui.menu.factory;

import autoservice.service.ServiceManager;
import autoservice.ui.menu.*;
import org.springframework.context.ApplicationContext;

/**
 * Конкретная реализация фабрики для создания пунктов меню расчета свободных слотов по времени.
 */
public class DefaultCapacityMenuFactory implements CapacityMenuFactory {
    private final ServiceManager serviceManager;
    private final ApplicationContext applicationContext;

    public DefaultCapacityMenuFactory(ServiceManager serviceManager, ApplicationContext applicationContext) {
        this.serviceManager = serviceManager;
        this.applicationContext = applicationContext;
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


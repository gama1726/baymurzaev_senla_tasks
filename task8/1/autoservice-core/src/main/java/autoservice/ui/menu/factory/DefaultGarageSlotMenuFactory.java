package autoservice.ui.menu.factory;

import autoservice.injection.DIContainer;
import autoservice.service.ServiceManager;
import autoservice.ui.menu.*;

/**
 * Конкретная реализация фабрики для создания пунктов меню гаражных мест.
 */
public class DefaultGarageSlotMenuFactory implements GarageSlotMenuFactory {
    private final ServiceManager serviceManager;
    private final DIContainer container;

    public DefaultGarageSlotMenuFactory(ServiceManager serviceManager, DIContainer container) {
        this.serviceManager = serviceManager;
        this.container = container;
    }
    
    private void injectDependencies(Object action) {
        container.injectDependencies(action);
    }

    @Override
    public MenuItem createListGarageSlotsItem() {
        return new MenuItem("Список гаражных мест",
                new ListGarageSlotsAction(serviceManager), null);
    }

    @Override
    public MenuItem createListFreeGarageSlotsNowItem() {
        return new MenuItem("Свободные места (сейчас)",
                new ListFreeGarageSlotsNowAction(serviceManager), null);
    }

    @Override
    public MenuItem createAddGarageSlotItem() {
        AddGarageSlotAction action = new AddGarageSlotAction(serviceManager);
        injectDependencies(action);
        return new MenuItem("Добавить гаражное место", action, null);
    }

    @Override
    public MenuItem createRemoveGarageSlotItem() {
        RemoveGarageSlotAction action = new RemoveGarageSlotAction(serviceManager);
        injectDependencies(action);
        return new MenuItem("Удалить гаражное место", action, null);
    }

    @Override
    public MenuItem createExportGarageSlotsItem() {
        return new MenuItem("Экспортировать гаражные места в CSV",
                new ExportGarageSlotsAction(serviceManager), null);
    }

    @Override
    public MenuItem createImportGarageSlotsItem() {
        return new MenuItem("Импортировать гаражные места из CSV",
                new ImportGarageSlotsAction(serviceManager), null);
    }
}


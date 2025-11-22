package autoservice.ui.menu.factory;

import autoservice.service.ServiceManager;
import autoservice.ui.menu.*;

/**
 * Конкретная реализация фабрики для создания пунктов меню гаражных мест.
 */
public class DefaultGarageSlotMenuFactory implements GarageSlotMenuFactory {
    private final ServiceManager serviceManager;

    public DefaultGarageSlotMenuFactory(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
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
        return new MenuItem("Добавить гаражное место",
                new AddGarageSlotAction(serviceManager), null);
    }

    @Override
    public MenuItem createRemoveGarageSlotItem() {
        return new MenuItem("Удалить гаражное место",
                new RemoveGarageSlotAction(serviceManager), null);
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


package autoservice.ui.menu.factory;

import autoservice.service.ServiceManager;
import autoservice.ui.menu.*;
import org.springframework.context.ApplicationContext;

/**
 * Конкретная реализация фабрики для создания пунктов меню механиков.
 */
public class DefaultMechanicMenuFactory implements MechanicMenuFactory {
    private final ServiceManager serviceManager;
    private final ApplicationContext applicationContext;

    public DefaultMechanicMenuFactory(ServiceManager serviceManager, ApplicationContext applicationContext) {
        this.serviceManager = serviceManager;
        this.applicationContext = applicationContext;
    }

    @Override
    public MenuItem createListMechanicsItem() {
        return new MenuItem("Список механиков",
                new ListMechanicsAction(serviceManager), null);
    }

    @Override
    public MenuItem createAddMechanicItem() {
        return new MenuItem("Добавить механика",
                new AddMechanicAction(serviceManager), null);
    }

    @Override
    public MenuItem createRemoveMechanicItem() {
        return new MenuItem("Удалить механика",
                new RemoveMechanicAction(serviceManager), null);
    }

    @Override
    public MenuItem createExportMechanicsItem() {
        return new MenuItem("Экспортировать механиков в CSV",
                new ExportMechanicsAction(serviceManager), null);
    }

    @Override
    public MenuItem createImportMechanicsItem() {
        return new MenuItem("Импортировать механиков из CSV",
                new ImportMechanicsAction(serviceManager), null);
    }
}


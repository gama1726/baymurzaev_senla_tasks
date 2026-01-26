package autoservice.ui.menu;

import autoservice.annotation.Inject;
import autoservice.config.Configuration;
import autoservice.service.ServiceManager;

import java.util.Scanner;

/**
 * Удалить гаражное место по ID.
 */
public class RemoveGarageSlotAction implements IAction{
    private final ServiceManager serviceManager;
    
    @Inject
    private Configuration configuration;
    
    public RemoveGarageSlotAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }
    @Override
    public void execute() {
        if (!configuration.isGarageSlotAddRemoveEnabled()) {
            System.out.println("Добавление/удаление гаражных мест запрещено в конфигурации.");
            return;
        }
        
        Scanner scanner = new Scanner(System.in);
        System.out.println("ID места для удаления ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            boolean ok = serviceManager.removeGarageSlotById(id);
            System.out.println(ok ? "Место удалено. " : "Место не найдено");
        }catch (NumberFormatException e){
            System.out.println("Нужно ввести целое число.");
        }
    }
}

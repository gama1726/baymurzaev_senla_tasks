package autoservice.ui.menu;

import autoservice.config.ConfigurationManager;
import autoservice.service.ServiceManager;

import java.util.Scanner;

/**
 * Удалить гаражное место по ID.
 */
public class RemoveGarageSlotAction implements IAction{
    private final ServiceManager serviceManager;
    private final ConfigurationManager configManager;
    
    public RemoveGarageSlotAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
        this.configManager = ConfigurationManager.getInstance();
    }
    
    @Override
    public void execute() {
        if (!configManager.isGarageSlotAddRemoveEnabled()) {
            System.out.println("Ошибка: добавление/удаление гаражных мест отключено в конфигурации.");
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

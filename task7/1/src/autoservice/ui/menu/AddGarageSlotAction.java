package autoservice.ui.menu;

import autoservice.config.ConfigurationManager;
import autoservice.model.GarageSlot;
import autoservice.service.ServiceManager;

import java.util.Scanner;

/**
 * Добавить гаражное место.
 */
public class AddGarageSlotAction implements IAction {
    private final ServiceManager serviceManager;
    private final ConfigurationManager configManager;
    
    public AddGarageSlotAction(ServiceManager serviceManager) {
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
        System.out.println("ID гаражного места: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            serviceManager.addGarageSlot(new GarageSlot(id));
            System.out.println("Гаражное место добавлено.");
        }
        catch (NumberFormatException e) {
            System.out.println("Нужно ввести целое число.");
        }
    }
}

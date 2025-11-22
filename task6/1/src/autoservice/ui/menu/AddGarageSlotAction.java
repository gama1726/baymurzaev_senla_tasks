package autoservice.ui.menu;
import autoservice.model.GarageSlot;
import autoservice.service.ServiceManager;

import java.awt.event.ActionListener;
import java.util.Scanner;

/**
 * Добавить гаражное место.
 */
public class AddGarageSlotAction implements IAction {
    private final ServiceManager serviceManager;
    public AddGarageSlotAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }
    @Override
    public void execute() {
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

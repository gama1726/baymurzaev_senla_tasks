package autoservice.ui.menu;

import autoservice.model.Mechanic;
import autoservice.service.ServiceManager;

import java.util.Scanner;

/**
 * Добавить механика.
 */
public class AddMechanicAction implements IAction{
    private final ServiceManager serviceManager;
    public AddMechanicAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }
    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("ID механика: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Имя механика: ");
            String name = scanner.nextLine().trim();

            serviceManager.addMechanic(new Mechanic(id, name));
            System.out.println("Механик добавлен");
        }
        catch (Exception e) {
            System.out.println("Ошибка при добавлении механика: " + e.getMessage());
        }
    }
}

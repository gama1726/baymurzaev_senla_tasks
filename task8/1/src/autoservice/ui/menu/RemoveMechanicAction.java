package autoservice.ui.menu;

import autoservice.service.ServiceManager;

import java.util.Scanner;

/**
 * Удалить механика по ID.
 */
public class RemoveMechanicAction implements IAction {
    private final ServiceManager serviceManager;
    public RemoveMechanicAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }
    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("ID механика для удаления:");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            boolean ok = serviceManager.removeMechanicById(id);
            System.out.println(ok ? "Механик удален. ": "Механик не найден");
        } catch (NumberFormatException  e) {
            System.out.println("Нужно ввести целое число.");
        }
    }
}

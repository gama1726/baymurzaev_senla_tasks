package autoservice.ui.menu;

import autoservice.service.ServiceManager;

import java.util.Scanner;

/**
 * Закрыть заказ.
 */
public class CloseOrderAction implements IAction {

    private final ServiceManager serviceManager;

    public CloseOrderAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("ID заказа для закрытия: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            boolean ok = serviceManager.closeOrder(id);
            System.out.println(ok ? "Заказ закрыт." : "Заказ не найден или уже не активен.");
        } catch (NumberFormatException e) {
            System.out.println("Нужно ввести целое число.");
        }
    }
}

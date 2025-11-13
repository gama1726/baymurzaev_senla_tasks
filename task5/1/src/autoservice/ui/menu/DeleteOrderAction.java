package autoservice.ui.menu;

import autoservice.service.ServiceManager;

import java.util.Scanner;

/**
 * Пометить заказ как удалённый.
 */
public class DeleteOrderAction implements  IAction{
    private final ServiceManager serviceManager;

    public DeleteOrderAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("ID заказа для удаления: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            boolean ok = serviceManager.deleteOrder(id);
            System.out.println(ok ? "Заказ помечен как удалённый." : "Заказ не найден.");
        } catch (NumberFormatException e) {
            System.out.println("Нужно ввести целое число.");
        }
    }
}

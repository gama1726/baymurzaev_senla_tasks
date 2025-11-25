package autoservice.ui.menu;
import autoservice.service.ServiceManager;

import java.util.Scanner;

/**
 * Отменить заказ.
 */
public class CancelOrderAction implements IAction{
    private final ServiceManager serviceManager;
    public CancelOrderAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("ID заказа для отмены: ");
        try{
            int id = Integer.parseInt(scanner.nextLine().trim());
            boolean ok = serviceManager.cancelOrder(id);
            System.out.println(ok ? "Заказ отменен." : "Заказ не найден или уже не активен");
        }catch (NumberFormatException e) {
            System.out.println("Нужно ввести целое число.");
        }
    }

}

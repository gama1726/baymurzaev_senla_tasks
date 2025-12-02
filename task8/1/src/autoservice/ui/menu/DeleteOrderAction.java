package autoservice.ui.menu;

import autoservice.annotation.Inject;
import autoservice.config.Configuration;
import autoservice.service.ServiceManager;

import java.util.Scanner;

/**
 * Пометить заказ как удалённый.
 */
public class DeleteOrderAction implements  IAction{
    private final ServiceManager serviceManager;
    
    @Inject
    private Configuration configuration;

    public DeleteOrderAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public void execute() {
        if (!configuration.isOrderDeleteEnabled()) {
            System.out.println("Удаление заказов запрещено в конфигурации.");
            return;
        }
        
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

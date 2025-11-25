package autoservice.ui.menu;

import autoservice.config.ConfigurationManager;
import autoservice.service.ServiceManager;

import java.util.Scanner;

/**
 * Пометить заказ как удалённый.
 */
public class DeleteOrderAction implements  IAction{
    private final ServiceManager serviceManager;
    private final ConfigurationManager configManager;

    public DeleteOrderAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
        this.configManager = ConfigurationManager.getInstance();
    }

    @Override
    public void execute() {
        if (!configManager.isOrderDeleteEnabled()) {
            System.out.println("Ошибка: удаление заказов отключено в конфигурации.");
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

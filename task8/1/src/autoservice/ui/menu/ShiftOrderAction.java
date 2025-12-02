package autoservice.ui.menu;

import autoservice.annotation.Inject;
import autoservice.config.Configuration;
import autoservice.service.ServiceManager;

import java.util.Scanner;

/**
 * Сместить время заказа.
 */
public class ShiftOrderAction implements  IAction {
    private final ServiceManager serviceManager;
    
    @Inject
    private Configuration configuration;
    
    public ShiftOrderAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }
    @Override
    public void execute() {
        if (!configuration.isOrderShiftEnabled()) {
            System.out.println("Смещение времени заказов запрещено в конфигурации.");
            return;
        }
        
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("ID заказа для сдвига: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Сместить на (минут): ");
            int minutes = Integer.parseInt(scanner.nextLine().trim());

            boolean ok = serviceManager.shiftOrder(id, minutes);
            System.out.println(ok ? "Время заказа смещено." : "Заказ не найден.");
        } catch (NumberFormatException e) {
            System.out.println("Нужно ввести целые числа.");
        }
    }

}


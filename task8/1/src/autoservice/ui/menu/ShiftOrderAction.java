package autoservice.ui.menu;

import autoservice.config.ConfigurationManager;
import autoservice.service.ServiceManager;

import java.util.Scanner;

/**
 * Сместить время заказа.
 */
public class ShiftOrderAction implements  IAction {
    private final ServiceManager serviceManager;
    private final ConfigurationManager configManager;
    
    public ShiftOrderAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
        this.configManager = ConfigurationManager.getInstance();
    }
    
    @Override
    public void execute() {
        if (!configManager.isOrderShiftEnabled()) {
            System.out.println("Ошибка: смещение времени выполнения заказов отключено в конфигурации.");
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


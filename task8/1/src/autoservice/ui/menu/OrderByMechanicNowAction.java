package autoservice.ui.menu;

import autoservice.model.ServiceOrder;
import autoservice.service.ServiceManager;

import java.util.Optional;
import java.util.Scanner;

/**
 * Показать заказ, выполняемый механиком прямо сейчас.
 */
public class OrderByMechanicNowAction implements IAction {

    private final ServiceManager serviceManager;

    public OrderByMechanicNowAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("ID механика: ");
        try {
            int mechId = Integer.parseInt(scanner.nextLine().trim());
            Optional<ServiceOrder> opt = serviceManager.getOrderByMechanicNow(mechId);
            if (opt.isPresent()) {
                System.out.println("Текущий заказ механика #" + mechId + ":");
                System.out.println(opt.get());
            } else {
                System.out.println("У механика сейчас нет активного заказа.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Нужно ввести целое число.");
        }
    }
}


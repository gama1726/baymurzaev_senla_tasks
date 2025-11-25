package autoservice.ui.menu;

import autoservice.model.Mechanic;
import autoservice.service.ServiceManager;

import java.util.Optional;
import java.util.Scanner;

/**
 * Показать механика, выполняющего указанный заказ.
 */
public class MechanicByOrderAction implements IAction {

    private final ServiceManager serviceManager;

    public MechanicByOrderAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("ID заказа: ");
        try {
            int orderId = Integer.parseInt(scanner.nextLine().trim());
            Optional<Mechanic> opt = serviceManager.getMechanicByOrderId(orderId);
            if (opt.isPresent()) {
                System.out.println("Заказ #" + orderId + " выполняет механик:");
                System.out.println(opt.get());
            } else {
                System.out.println("Заказ не найден или не назначен механик.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Нужно ввести целое число.");
        }
    }
}

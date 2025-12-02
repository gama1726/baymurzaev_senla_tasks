package autoservice.ui.menu;
import autoservice.model.OrderSort;
import autoservice.model.ServiceOrder;
import autoservice.service.ServiceManager;

import java.util.List;
import java.util.Scanner;

/**
 * Показать текущие (активные) заказы.
 */
public class ListCurrentOrdersAction implements IAction {
    private final ServiceManager serviceManager;

    public ListCurrentOrdersAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("""
               Сортировка текущих заказов:
               1. По дате подачи
               2. По планируемому началу
               3. По дате завершения
               4. По цене
               """);
        System.out.print("Выберите: ");
        String choice = scanner.nextLine().trim();

        OrderSort sort;
        switch (choice) {
            case "1": sort = OrderSort.BY_SUBMIT_DATE; break;
            case "3": sort = OrderSort.BY_FINISH_DATE; break;
            case "4": sort = OrderSort.BY_PRICE; break;
            case "2":
            default:  sort = OrderSort.BY_PLANNED_START;
        }

        List<ServiceOrder> orders = serviceManager.getCurrentOrderSorted(sort);
        System.out.println("\n=== Текущие заказы ===");
        if (orders.isEmpty()) {
            System.out.println("(нет активных заказов)");
        } else {
            orders.forEach(System.out::println);
        }
    }

}

package autoservice.ui.menu;

import autoservice.model.OrderSort;
import autoservice.model.ServiceOrder;
import autoservice.service.ServiceManager;

import java.util.List;

/**
 * Действие: вывести список всех заказов.
 */
public class ListOrdersAction implements IAction {
    private final ServiceManager serviceManager;
    public ListOrdersAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }
    @Override
    public void execute() {
        System.out.println("DEBUG: зашли в ListOrdersAction.execute()");
        System.out.println("\n=== Список заказов ===");
        List<ServiceOrder> orders = serviceManager.getAllOrdersSorted(OrderSort.BY_PLANNED_START);
        if(orders.isEmpty()){
            System.out.println("(Нет заказов)");
        } else{
            orders.forEach(System.out::println);
        }
    }
}

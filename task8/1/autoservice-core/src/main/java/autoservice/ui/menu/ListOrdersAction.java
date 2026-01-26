package autoservice.ui.menu;

import autoservice.model.OrderSort;
import autoservice.model.ServiceOrder;
import autoservice.service.ServiceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Действие: вывести список всех заказов.
 */
public class ListOrdersAction implements IAction {
    private static final Logger logger = LogManager.getLogger(ListOrdersAction.class);
    private final ServiceManager serviceManager;
    public ListOrdersAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }
    @Override
    public void execute() {
        logger.info("Начало выполнения команды: список всех заказов");
        try {
            System.out.println("\n=== Список заказов ===");
            List<ServiceOrder> orders = serviceManager.getAllOrdersSorted(OrderSort.BY_PLANNED_START);
            if(orders.isEmpty()){
                System.out.println("(Нет заказов)");
            } else{
                orders.forEach(System.out::println);
            }
            logger.info("Команда выполнена успешно. Найдено заказов: {}", orders.size());
        } catch (Exception e) {
            logger.error("Ошибка при получении списка заказов", e);
            throw e;
        }
    }
}

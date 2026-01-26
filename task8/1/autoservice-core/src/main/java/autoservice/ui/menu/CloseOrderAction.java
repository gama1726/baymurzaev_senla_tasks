package autoservice.ui.menu;

import autoservice.service.ServiceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

/**
 * Закрыть заказ.
 */
public class CloseOrderAction implements IAction {
    private static final Logger logger = LogManager.getLogger(CloseOrderAction.class);
    private final ServiceManager serviceManager;

    public CloseOrderAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public void execute() {
        logger.info("Начало выполнения команды: закрытие заказа");
        Scanner scanner = new Scanner(System.in);
        System.out.print("ID заказа для закрытия: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            logger.info("Попытка закрытия заказа с ID: {}", id);
            boolean ok = serviceManager.closeOrder(id);
            if (ok) {
                logger.info("Заказ с ID {} успешно закрыт", id);
                System.out.println("Заказ закрыт.");
            } else {
                logger.warn("Заказ с ID {} не найден или уже не активен", id);
                System.out.println("Заказ не найден или уже не активен.");
            }
        } catch (NumberFormatException e) {
            logger.error("Ошибка: неверный формат ID заказа", e);
            System.out.println("Нужно ввести целое число.");
        } catch (Exception e) {
            logger.error("Ошибка при закрытии заказа", e);
            throw e;
        }
    }
}

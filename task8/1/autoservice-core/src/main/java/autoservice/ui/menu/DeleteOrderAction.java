package autoservice.ui.menu;

import autoservice.annotation.Inject;
import autoservice.config.Configuration;
import autoservice.service.ServiceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

/**
 * Пометить заказ как удалённый.
 */
public class DeleteOrderAction implements  IAction{
    private static final Logger logger = LogManager.getLogger(DeleteOrderAction.class);
    private final ServiceManager serviceManager;
    
    @Inject
    private Configuration configuration;

    public DeleteOrderAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public void execute() {
        logger.info("Начало выполнения команды: удаление заказа");
        try {
            if (!configuration.isOrderDeleteEnabled()) {
                logger.warn("Попытка удаления заказа, но операция запрещена в конфигурации");
                System.out.println("Удаление заказов запрещено в конфигурации.");
                return;
            }
            
            Scanner scanner = new Scanner(System.in);
            System.out.print("ID заказа для удаления: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            logger.info("Попытка удаления заказа с ID: {}", id);
            
            boolean ok = serviceManager.deleteOrder(id);
            if (ok) {
                logger.info("Заказ с ID {} успешно удален", id);
                System.out.println("Заказ помечен как удалённый.");
            } else {
                logger.warn("Заказ с ID {} не найден", id);
                System.out.println("Заказ не найден.");
            }
        } catch (NumberFormatException e) {
            logger.error("Ошибка: неверный формат ID заказа", e);
            System.out.println("Нужно ввести целое число.");
        } catch (Exception e) {
            logger.error("Ошибка при удалении заказа", e);
            throw e;
        }
    }
}

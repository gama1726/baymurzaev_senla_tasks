package autoservice.ui.menu;

import autoservice.exception.ImportExportException;
import autoservice.service.ServiceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

/**
 * Действие: экспорт заказов в CSV файл.
 */
public class ExportOrdersAction implements IAction {
    private static final Logger logger = LogManager.getLogger(ExportOrdersAction.class);
    private final ServiceManager serviceManager;

    public ExportOrdersAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public void execute() {
        logger.info("Начало выполнения команды: экспорт заказов в CSV");
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Введите путь к файлу для экспорта: ");
            String filePath = scanner.nextLine().trim();

            if (filePath.isEmpty()) {
                logger.warn("Попытка экспорта с пустым путем к файлу");
                System.out.println("Ошибка: путь к файлу не может быть пустым.");
                return;
            }

            logger.info("Экспорт заказов в файл: {}", filePath);
            serviceManager.exportOrdersToCsv(filePath);
            System.out.println("Экспорт заказов завершен успешно.");
            logger.info("Экспорт заказов завершен успешно. Файл: {}", filePath);
        } catch (ImportExportException e) {
            logger.error("Ошибка при экспорте заказов", e);
            System.out.println("Ошибка при экспорте заказов: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при экспорте заказов", e);
            System.out.println("Неожиданная ошибка: " + e.getMessage());
        }
    }
}




package autoservice.ui.menu;

import autoservice.exception.EntityNotFoundException;
import autoservice.exception.ImportExportException;
import autoservice.service.ServiceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

/**
 * Действие: импорт заказов из CSV файла с автоматическим связыванием объектов.
 */
public class ImportOrdersAction implements IAction {
    private static final Logger logger = LogManager.getLogger(ImportOrdersAction.class);
    private final ServiceManager serviceManager;

    public ImportOrdersAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public void execute() {
        logger.info("Начало выполнения команды: импорт заказов из CSV");
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Введите путь к CSV файлу для импорта: ");
            String filePath = scanner.nextLine().trim();

            if (filePath.isEmpty()) {
                logger.warn("Попытка импорта с пустым путем к файлу");
                System.out.println("Ошибка: путь к файлу не может быть пустым.");
                return;
            }

            logger.info("Импорт заказов из файла: {}", filePath);
            serviceManager.importOrdersFromCsv(filePath);
            System.out.println("Импорт заказов завершен успешно.");
            logger.info("Импорт заказов завершен успешно. Файл: {}", filePath);
        } catch (ImportExportException e) {
            logger.error("Ошибка при импорте заказов", e);
            System.out.println("Ошибка при импорте заказов: " + e.getMessage());
        } catch (EntityNotFoundException e) {
            logger.error("Ошибка: сущность не найдена при импорте", e);
            System.out.println("Ошибка: " + e.getMessage() + ". Убедитесь, что механики и гаражные места импортированы.");
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при импорте заказов", e);
            System.out.println("Неожиданная ошибка: " + e.getMessage());
        }
    }
}




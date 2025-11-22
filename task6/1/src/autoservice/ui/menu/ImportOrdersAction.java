package autoservice.ui.menu;

import autoservice.exception.EntityNotFoundException;
import autoservice.exception.ImportExportException;
import autoservice.service.ServiceManager;

import java.util.Scanner;

/**
 * Действие: импорт заказов из CSV файла с автоматическим связыванием объектов.
 */
public class ImportOrdersAction implements IAction {
    private final ServiceManager serviceManager;

    public ImportOrdersAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public void execute() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Введите путь к CSV файлу для импорта: ");
            String filePath = scanner.nextLine().trim();

            if (filePath.isEmpty()) {
                System.out.println("Ошибка: путь к файлу не может быть пустым.");
                return;
            }

            serviceManager.importOrdersFromCsv(filePath);
            System.out.println("Импорт заказов завершен успешно.");
        } catch (ImportExportException e) {
            System.out.println("Ошибка при импорте заказов: " + e.getMessage());
        } catch (EntityNotFoundException e) {
            System.out.println("Ошибка: " + e.getMessage() + ". Убедитесь, что механики и гаражные места импортированы.");
        } catch (Exception e) {
            System.out.println("Неожиданная ошибка: " + e.getMessage());
        }
    }
}


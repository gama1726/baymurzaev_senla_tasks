package autoservice.ui.menu;

import autoservice.exception.ImportExportException;
import autoservice.service.ServiceManager;

import java.util.Scanner;

/**
 * Действие: экспорт заказов в CSV файл.
 */
public class ExportOrdersAction implements IAction {
    private final ServiceManager serviceManager;

    public ExportOrdersAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public void execute() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Введите путь к файлу для экспорта: ");
            String filePath = scanner.nextLine().trim();

            if (filePath.isEmpty()) {
                System.out.println("Ошибка: путь к файлу не может быть пустым.");
                return;
            }

            serviceManager.exportOrdersToCsv(filePath);
            System.out.println("Экспорт заказов завершен успешно.");
        } catch (ImportExportException e) {
            System.out.println("Ошибка при экспорте заказов: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Неожиданная ошибка: " + e.getMessage());
        }
    }
}


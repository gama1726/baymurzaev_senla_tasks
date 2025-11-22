package autoservice.ui.menu;

import autoservice.exception.ImportExportException;
import autoservice.service.ServiceManager;

import java.util.Scanner;

/**
 * Действие: импорт механиков из CSV файла.
 */
public class ImportMechanicsAction implements IAction {
    private final ServiceManager serviceManager;

    public ImportMechanicsAction(ServiceManager serviceManager) {
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

            serviceManager.importMechanicsFromCsv(filePath);
            System.out.println("Импорт механиков завершен успешно.");
        } catch (ImportExportException e) {
            System.out.println("Ошибка при импорте механиков: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Неожиданная ошибка: " + e.getMessage());
        }
    }
}


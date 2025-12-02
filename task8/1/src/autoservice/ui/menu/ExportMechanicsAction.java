package autoservice.ui.menu;

import autoservice.exception.ImportExportException;
import autoservice.service.ServiceManager;

import java.util.Scanner;

/**
 * Действие: экспорт механиков в CSV файл.
 */
public class ExportMechanicsAction implements IAction {
    private final ServiceManager serviceManager;

    public ExportMechanicsAction(ServiceManager serviceManager) {
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

            serviceManager.exportMechanicsToCsv(filePath);
            System.out.println("Экспорт механиков завершен успешно.");
        } catch (ImportExportException e) {
            System.out.println("Ошибка при экспорте механиков: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Неожиданная ошибка: " + e.getMessage());
        }
    }
}




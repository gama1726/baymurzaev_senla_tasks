package autoservice.ui.menu;

import autoservice.service.ServiceManager;

import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * Показать свободный слот на указанную дату/время.
 */
public class FreeCapacityAtAction implements IAction {

    private final ServiceManager serviceManager;

    public FreeCapacityAtAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Дата/время (yyyy-MM-ddTHH:mm): ");
            LocalDateTime t = LocalDateTime.parse(scanner.nextLine().trim());
            int capacity = serviceManager.freeCapacityAt(t);
            System.out.println("Свободный слот на " + t + ": " + capacity);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}

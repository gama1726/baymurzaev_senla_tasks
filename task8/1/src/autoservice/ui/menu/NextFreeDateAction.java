package autoservice.ui.menu;

import autoservice.service.ServiceManager;

import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * Показать ближайшую свободную дату.
 */
public class NextFreeDateAction implements IAction {

    private final ServiceManager serviceManager;

    public NextFreeDateAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Начать поиск от (Enter = сейчас, формат yyyy-MM-ddTHH:mm): ");
        String s = scanner.nextLine().trim();
        LocalDateTime from;
        if (s.isEmpty()) {
            from = LocalDateTime.now();
        } else {
            from = LocalDateTime.parse(s);
        }
        LocalDateTime next = serviceManager.findNextFreeDate(from);
        System.out.println("Ближайшая свободная дата: " + next);
    }
}

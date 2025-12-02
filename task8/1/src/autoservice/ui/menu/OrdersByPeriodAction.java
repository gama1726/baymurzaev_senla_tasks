package autoservice.ui.menu;

import autoservice.model.OrderSort;
import autoservice.model.OrderStatus;
import autoservice.model.ServiceOrder;
import autoservice.service.ServiceManager;

import java.time.LocalDateTime;
import java.util.*;

public class OrdersByPeriodAction implements IAction {

    private final ServiceManager serviceManager;

    public OrdersByPeriodAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    private LocalDateTime readDateTime(Scanner scanner, String prompt) {
        System.out.print(prompt + " (формат yyyy-MM-ddTHH:mm): ");
        String s = scanner.nextLine().trim();
        if (s.isEmpty()) {
            return null; // будем трактовать как «без границы»
        }
        return LocalDateTime.parse(s);
    }

    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in);
        try {
            LocalDateTime from = readDateTime(scanner, "Начало периода (Enter - без начала)");
            LocalDateTime to   = readDateTime(scanner, "Конец периода (Enter - без конца)");

            System.out.println("Фильтр по статусам:");
            System.out.println("1. Только NEW");
            System.out.println("2. Только CLOSED");
            System.out.println("3. Только CANCELED");
            System.out.println("4. Только DELETED");
            System.out.println("5. Все статусы");
            System.out.print("Выберите: ");
            String statusChoice = scanner.nextLine().trim();

            Set<OrderStatus> statuses = null;
            switch (statusChoice) {
                case "1": statuses = EnumSet.of(OrderStatus.NEW); break;
                case "2": statuses = EnumSet.of(OrderStatus.CLOSED); break;
                case "3": statuses = EnumSet.of(OrderStatus.CANCELED); break;
                case "4": statuses = EnumSet.of(OrderStatus.DELETED); break;
                case "5":
                default:  statuses = EnumSet.allOf(OrderStatus.class);
            }

            System.out.println("""
                Сортировка заказов:
                1. По дате подачи
                2. По планируемому началу
                3. По дате завершения
                4. По цене
                """);
            System.out.print("Выберите: ");
            String sortChoice = scanner.nextLine().trim();

            OrderSort sort;
            switch (sortChoice) {
                case "1": sort = OrderSort.BY_SUBMIT_DATE; break;
                case "3": sort = OrderSort.BY_FINISH_DATE; break;
                case "4": sort = OrderSort.BY_PRICE; break;
                case "2":
                default:  sort = OrderSort.BY_PLANNED_START;
            }

            // если обе границы null — используем «сейчас ± большой интервал», или просто now..now
            if (from == null) from = LocalDateTime.MIN.plusYears(1000); // чтобы не ловить переполнения
            if (to   == null) to   = LocalDateTime.MAX.minusYears(1000);

            List<ServiceOrder> orders =
                    serviceManager.getOrders(from, to, statuses, sort);

            System.out.println("\n=== Заказы за период ===");
            if (orders.isEmpty()) {
                System.out.println("(нет заказов по заданным условиям)");
            } else {
                orders.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println("Ошибка при получении заказов: " + e.getMessage());
        }
    }
}

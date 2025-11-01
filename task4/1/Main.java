import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        ServiceManager manager = new ServiceManager();

        // ----- ДАННЫЕ -----
        Mechanic m1 = new Mechanic(1, "Андрей");
        Mechanic m2 = new Mechanic(2, "Магомед");
        Mechanic m3 = new Mechanic(3, "Руслан");

        manager.addMechanic(m1);
        manager.addMechanic(m2);
        manager.addMechanic(m3);

        GarageSlot g1 = new GarageSlot(101);
        GarageSlot g2 = new GarageSlot(102);
        GarageSlot g3 = new GarageSlot(103);

        manager.addGarageSlot(g1);
        manager.addGarageSlot(g2);
        manager.addGarageSlot(g3);

        LocalDateTime now = LocalDateTime.now();

        // Три заказа: два сейчас, один позже
        ServiceOrder o1 = new ServiceOrder(
                1001, m1, g1,
                new TimeSlot(now.plusMinutes(0), now.plusHours(2)),
                1200
        );
        ServiceOrder o2 = new ServiceOrder(
                1002, m2, g2,
                new TimeSlot(now.plusMinutes(30), now.plusHours(1).plusMinutes(30)),
                800
        );
        ServiceOrder o3 = new ServiceOrder(
                1003, m3, g3,
                new TimeSlot(now.plusHours(3), now.plusHours(5)),
                2000
        );

        manager.addOrder(o1);
        manager.addOrder(o2);
        manager.addOrder(o3);

        // ДЕМОНСТРАЦИЯ ФУНКЦИОНАЛА

        // 1) Список свободных мест сейчас (третий слот занят заказом o3 позже — значит СЕЙЧАС он свободен)
        manager.printList("Свободные места (сейчас)", manager.getFreeGarageSlotsNow());

        // 2) Все заказы с разными сортировками
        manager.printList("Все заказы: по дате подачи", manager.getAllOrdersSorted(OrderSort.BY_SUBMIT_DATE));
        manager.printList("Все заказы: по планируемому началу", manager.getAllOrdersSorted(OrderSort.BY_PLANNED_START));
        manager.printList("Все заказы: по цене", manager.getAllOrdersSorted(OrderSort.BY_PRICE));

        // 3) Список механиков: по алфавиту и по текущей занятости
        manager.printList("Механики по алфавиту", manager.getMechanicSorted(MechanicSort.BY_NAME));
        manager.printList("Механики по занятости (сейчас)", manager.getMechanicSorted(MechanicSort.BY_WORKLOAD));

        // 4) Текущие выполняемые заказы , сортировка по цене
        manager.printList("Текущие заказы (по цене)", manager.getCurrentOrderSorted(OrderSort.BY_PRICE));

        // 5) Заказ, выполняемый конкретным мастером сейчас
        System.out.println("\nЗаказ у мастера #" + m1.getId() + " сейчас: " + manager.getOrderByMechanicNow(m1.getId()));

        // 6) Механик, выполняющий конкретный заказ
        System.out.println("Механик по заказу #1002: " + manager.getMechanicByOrderId(1002));

        // 7) Заказы за период и по статусам (после изменения статусов)
        manager.closeOrder(1001);   // закрыли первый
        manager.cancelOrder(1002);  // отменили второй
        manager.deleteOrder(1003);  // пометили удалённым третий (он был в будущем)

        Set<OrderStatus> statuses = EnumSet.of(OrderStatus.CLOSED, OrderStatus.CANCELED, OrderStatus.DELETED);
        manager.printList(
                "Заказы (закрыт/отменён/удалён) за период",
                manager.getOrders(now.minusHours(1), now.plusHours(6), statuses, OrderSort.BY_FINISH_DATE)
        );

        // 8) Количество свободных мест (ёмкость) на произвольный момент в будущем
        LocalDateTime tCheck = now.plusHours(4); // когда изначально планировался o3
        System.out.println("\nСвободная ёмкость на " + tCheck + ": " + manager.freeCapacityAt(tCheck));

        // 9) Ближайшая свободная дата от now (после закрытия/отмены/удаления она может быть прямо сейчас)
        System.out.println("Ближайшая свободная дата от " + now + ": " + manager.findNextFreeDate(now));

        // 10) Сдвиг времени заказов 
        // Создадим новый заказ и сдвинем его на час
        ServiceOrder o4 = new ServiceOrder(
                1004, m1, g1,
                new TimeSlot(now.plusHours(6), now.plusHours(7)),
                1500
        );
        manager.addOrder(o4);
        System.out.println("\nДо сдвига: " + o4);
        manager.shiftOrders(60); // +60 минут
        System.out.println("После сдвига: " + o4);
    }
}

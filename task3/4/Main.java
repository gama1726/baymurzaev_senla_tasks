import java.time.LocalDateTime;
public class Main {
    public static void main(String[] args) {
    
    ServiceManager manager = new ServiceManager();
    //Мастера
    Mechanic m1 = new Mechanic(1, "Андрей");
    Mechanic m2 = new Mechanic(2,"Магомед");
    manager.addMechanic(m1);
    manager.addMechanic(m2);
     //Места
    GarageSlot s1 = new GarageSlot(101);
    GarageSlot s2 = new GarageSlot(102);
    manager.addGarageSlot(s1);
    manager.addGarageSlot(s2);
    manager.showFreeSlots();
    //Время
    LocalDateTime now = LocalDateTime.now();
    TimeSlot t1 = new TimeSlot(now.plusMinutes(0), now.plusMinutes(60));
    TimeSlot t2 = new TimeSlot(now.plusMinutes(70), now.plusMinutes(130));
    //Заказы
    ServiceOrder o1 = new ServiceOrder(1001,m1,s1,t1);
    ServiceOrder o2 = new ServiceOrder(1002,m2,s2,t2);
    manager.addOrder(o1);
    manager.addOrder(o2);
    //Закрытие заказ и отмена
    manager.closeOrder(1001);
    manager.cancelOrder(1002);
    //Смещение заказа
    manager.shiftOrders(30);
    //Вывод всех заказов
    System.out.println("\nВсе заказы:");
    System.out.println(o1);
    System.out.println(o2);
    }
}


import java.util.ArrayList;
import java.util.List;

public class ServiceManager {
    private List<Mechanic> mechanics = new ArrayList<>();
    private List<GarageSlot> garageSlots = new ArrayList<>();
    private List<TimeSlot> timeSlots = new ArrayList<>();
    private List<ServiceOrder> orders = new ArrayList<>();

    //механики
    public void addMechanic(Mechanic mechanic){
        mechanics.add(mechanic);
        System.out.println("Добавлен " + mechanic);
    }
    public void removeMechanic(int id){
        mechanics.removeIf(m -> m.getId() == id);
        System.out.println("Удален механик № " + id);
    }
    //Места в гараже
    public void addGarageSlot(GarageSlot slot){
        garageSlots.add(slot);
        System.out.println("Добавлено место " + slot);
    }
    public void removeGarageSlot(int id){
        garageSlots.removeIf(s -> s.getId() == id);
        System.out.println("Удалено место " + id);
    }
    //Заказы
    public void addOrder(ServiceOrder order){
        orders.add(order);
        System.out.println("Добавлен заказ:\n" + order);
    }

    public void cancelOrder(int id){
        for(ServiceOrder o : orders){
            if(o.getId() == id){
                o.cancel();
                System.out.println("Отменен заказ № " + id);
                return;
            }
        }
    }
    public void closeOrder(int id){
        for(ServiceOrder o : orders){
            if(o.getId() == id){
                o.close();
                System.out.println("Закрыт заказ № " + id);
                return;
            }
        }
    }
    //Смещение заказа
    public void shiftOrders(int minutes){
        System.out.println("Cмещаем все заказы на " + minutes + "минут");
        for(ServiceOrder o : orders){
            if(o.getStatus() == OrderStatus.NEW){
                o.shift(minutes);
                System.out.println("Смещен " + o);
            }
        }
    }
}

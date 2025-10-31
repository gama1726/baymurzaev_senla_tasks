
import java.time.LocalDateTime;
import java.util.*;

public class ServiceManager {
    private List<Mechanic> mechanics = new ArrayList<>();
    private List<GarageSlot> garageSlots = new ArrayList<>();
    private List<TimeSlot> timeSlots = new ArrayList<>();
    private List<ServiceOrder> orders = new ArrayList<>();

    //Добавление и удаление
    //добавить механика
    public void addMechanic(Mechanic mechanic){
        mechanics.add(mechanic);
        System.out.println("Добавлен " + mechanic);
    }
    //удалить механика
    public void removeMechanicById(int id){
        mechanics.removeIf(m -> m.getId() == id);
        System.out.println("Удален механик № " + id);
    }
    //добавить место в гараже
    public void addGarageSlot(GarageSlot slot){
        garageSlots.add(slot);
        System.out.println("Добавлено место " + slot);
    }
    //удалить место в гараже
    public void removeGarageSlotById(int id){
        garageSlots.removeIf(s -> s.getId() == id);
        System.out.println("Удалено место " + id);
    }
    
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
    //удаление заказа
        public boolean deleteOrder(int orderId){
        ServiceOrder o = findOrder(orderId);
        if (o == null) return false;
        o.markDeleted();
        return true;
    }
    //Смещение заказа
    public boolean  shiftOrders(int minutes){
        System.out.println("Cмещаем все заказы на " + minutes + "минут");
        for(ServiceOrder o : orders){
            if(o.getStatus() == OrderStatus.NEW){
                o.shift(minutes);
                System.out.println("Смещен " + o);
            }
        }
        return true;
    }
    
    // Найти заказ по ID.
    private ServiceOrder findOrder(int id){
        for (ServiceOrder o : orders) if (o.getId() == id) return o;
        return null;
    }
//вспомогательное
//проверяем,активен ли заказ в данный момент
private boolean isActive(ServiceOrder o, LocalDateTime when){
        return o.getStatus() == OrderStatus.NEW && o.getTimeSlot().contains(when);
    }
    //Возвращает компаратор для сортировки заказов
    private Comparator<ServiceOrder> orderComporator(OrderSort sort){
        switch (sort) {
            case BY_SUBMIT_DATE:
                return Comparator.comparing(ServiceOrder::getSubmittedAt);
            case BY_PLANNED_START:
                return Comparator.comparing(o -> o.getTimeSlot().getStart());
            case BY_FINISH_DATE:
                return Comparator.comparing(o -> {
                    LocalDateTime f = o.getFinishedAt();
                    return f == null ? LocalDateTime.MAX :f;
                });
            case BY_PRICE:
                return Comparator.comparingInt(ServiceOrder::getPrice);
            default:
                return Comparator.comparingInt(ServiceOrder::getId);
        }
    }
//Отчетные выборки
//Гаражи
// Список свободных мест в гараже на данный момент.
    public List<GarageSlot> getFreeGarageSlotsNow(){
        return getFreeGarageSlotsAt(LocalDateTime.now());
    }
    // Список свободных мест на конкретное время.
    public List <GarageSlot> getFreeGarageSlotsAt(LocalDateTime when){
        List <GarageSlot> free = new ArrayList<>(garageSlots);
        for (ServiceOrder o : orders){
            if (o.getStatus() == OrderStatus.NEW && o.getTimeSlot().contains(when)){
                free.remove(o.getGarageSlot());
            }
        }
        return free;
    }
    // public void showFreeSlots(){
    //     LocalDateTime now = LocalDateTime.now();
    //     List<GarageSlot> free = getFreeGarageSlotsAt(now);
    //     if (free.isEmpty()){
    //         System.err.println("Свободных мест нет на " + now);
    //     } else {
    //         for (GarageSlot s : free){
    //             System.err.println(s);
    //         }
    //     }
    // }

    //Заказы
    //Все заказы, отсортированные по заданному критерию
     public List<ServiceOrder> getAllOrdersSorted(OrderSort sort){
        List<ServiceOrder> res = new ArrayList<>(orders);
        res.sort(orderComporator(sort));
        return res;
    }
    //cписок текущих выполняемых заказов
    public List<ServiceOrder> getCurrentOrderSorted(OrderSort sort){
        LocalDateTime now = LocalDateTime.now(); 
        List<ServiceOrder> res = new ArrayList<>();
         for(ServiceOrder o : orders){
            if(isActive(o, now)){
                res.add(o);
            }
         }
         res.sort(orderComporator(sort));
         return res;
    }
    //текущий заказ у конкретного мастера
    public ServiceOrder getOrderByMechanicNow(int mechanicId){
        LocalDateTime now = LocalDateTime.now();
        for (ServiceOrder o : orders){
            if(o.getStatus() == OrderStatus.NEW && o.getMechanic().getId() == mechanicId && o.getTimeSlot().contains(now)){
                return o;
            }
        }
        return null;
    }
    //Механик, выполняющий заказ по ID.
    public Mechanic getMechanicByOrderId(int orderId){
        ServiceOrder o = findOrder(orderId);
        return (o == null) ? null : o.getMechanic();
    }
    //Заказы за заданный период и статусы
    public List<ServiceOrder> getOrders(LocalDateTime from,LocalDateTime to,Set<OrderStatus> statuses,OrderSort sort){
        List<ServiceOrder> res = new ArrayList<>();
        for (ServiceOrder o : orders) {
            boolean statusOk = (statuses == null || statuses.contains(o.getStatus()));
            boolean timeOk = (from == null && to == null) || (o.getTimeSlot().getStart().isBefore(to) && o.getTimeSlot().getEnd().isAfter(from));
            if (statusOk && timeOk) res.add(o);
        }
        res.sort(orderComporator(sort));
        return res;
    }
    //Механики
    //Список механиков, отсортированных по имени или занятости
    public List<Mechanic> getMechanicSorted(MechanicSort sort){
        List<Mechanic> res = new ArrayList<>(mechanics);
        if(sort == MechanicSort.BY_NAME){
            res.sort(Comparator.comparing(Mechanic :: getName));
        } else {
            LocalDateTime now = LocalDateTime.now();
            res.sort(Comparator.comparing(m -> currentWorkload(m,now)));
        }
        return res;
    }
    //Подсчёт текущей занятости механика
    private int currentWorkload(Mechanic m,LocalDateTime when){
        int count = 0;
        for(ServiceOrder o : orders){
            if (o.getStatus() == OrderStatus.NEW && o.getMechanic().getId() == m.getId() && o.getTimeSlot().contains(when)){
                count++;
            }

        }
        return count;
    }
    //Кол-во свободных мест (минимум между свободными механиками и местами).
    public int freeCapacityAt(LocalDateTime when){
        int freeMechanics = 0;
        for(Mechanic m : mechanics){
            boolean busy = false;
            for (ServiceOrder o : orders){
                if (o.getStatus() == OrderStatus.NEW && o.getMechanic().getId() == m.getId() && o.getTimeSlot().contains(when)){
                    busy = true;
                    break;
                }
            }
            if(!busy) {
                freeMechanics++;
            }
        }
        int freeGarages = getFreeGarageSlotsAt(when).size();
        return Math.min(freeMechanics,freeGarages);
    }
    //Найти ближайшую свободную дату (шаг 15 мин)
    public LocalDateTime findNextFreeDate(LocalDateTime from){
        LocalDateTime t = from;
        for(int i = 0; i < 96 * 30; i++){//Перебираем 96 * 30 = 2880 шагов, по 15 минут каждый.
    // Это примерно 30 суток (96 интервалов по 15 минут в сутках).
            if(freeCapacityAt(t) > 0){
                return t;
            }
            t = t.plusMinutes(15);
        }
        return null;
    }
    // Выводит заголовок и список в консоль.
    public void printList(String title,List<?> list){
        System.out.println("\n" + title + ":");
        if(list.isEmpty()){
            System.out.println("пусто");
        } else {
            for (Object o : list){
                System.out.println(o);
            }
        }
    }
    //Печатает отчёт по заказам за период
    public void demoPeriodReport(LocalDateTime from,LocalDateTime to){
        Set<OrderStatus> st = EnumSet.of(OrderStatus.NEW, OrderStatus.CLOSED, OrderStatus.CANCELED, OrderStatus.DELETED);
        List<ServiceOrder> list = getOrders(from,to,st,OrderSort.BY_PLANNED_START);
        printList("Отчет по заказам [" + from + ".." + to + "]",list);
    }
}
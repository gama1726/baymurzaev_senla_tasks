package autoservice.service;

import autoservice.model.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Менеджер управления автосервисом (Singleton).
 * Хранит механиков, гаражные места и заказы, выдает операции и выборки по тегам.
 */
public class ServiceManager {
    // ----- Singleton -----
    private static final ServiceManager INSTANCE = new ServiceManager();
    public static ServiceManager getInstance(){
        return INSTANCE;
    }
    // ----- Данные модели -----
    private List<Mechanic> mechanics = new ArrayList<>();
    private List<GarageSlot> garageSlots = new ArrayList<>();
    private List<TimeSlot> timeSlots = new ArrayList<>();
    private List<ServiceOrder> orders = new ArrayList<>();

    private ServiceManager(){

    }
    //Добавление и удаление

     // ----- Механики -----
    //добавить механика
    public void addMechanic(Mechanic mechanic){
        mechanics.add(mechanic);
        System.out.println("Добавлен " + mechanic);
    }
    //удалить механика
    public boolean removeMechanicById(int id){
        boolean removed = mechanics.removeIf(m -> m.getId() == id);

        if (removed) {
            System.out.println("Удалён механик № " + id);
        } else {
            System.out.println("Механик № " + id + " не найден.");
        }
        return removed;
    }
    //ищет механика по id
    public Optional<Mechanic> findMechanicById(int id){
        return mechanics.stream()
        .filter(m -> m.getId() == id)
        .findFirst();
    }
    // ----- Гаражи -----
    //добавить место в гараже
    public void addGarageSlot(GarageSlot slot){
        garageSlots.add(slot);
        System.out.println("Добавлено место " + slot);
    }
    //удалить место в гараже
    public boolean removeGarageSlotById(int id){
        boolean removed = garageSlots.removeIf(s -> s.getId() == id);
        if (removed) {
            System.out.println("Удалено место № " + id);
        } else {
            System.out.println("Место № " + id + " не найдено.");
        }
        return removed;
    }
    public Optional<GarageSlot> findGarageSlotById(int id){
        return garageSlots.stream()
                .filter(m -> m.getId() == id)
                .findFirst();
    }
    public List<GarageSlot> getGarageSlots(){
        return Collections.unmodifiableList(garageSlots);
    }
    // ----- Заказы -----
    //добавить заказ
    public void addOrder(ServiceOrder order){
        orders.add(order);
        System.out.println("Добавлен заказ:\n" + order);
    }
    public Optional<ServiceOrder> findOrderById(int id){
        return orders.stream()
                .filter(o -> o.getId() == id)
                .findFirst();
    }
    //отменить заказ
    public boolean  cancelOrder(int orderId){
        return findOrderById(orderId)
                .map(o -> {o.close(); return true; })
                .orElse(false);
    }
    //закрытие заказа
    public boolean  closeOrder(int orderId){
        return findOrderById(orderId)
                .map(o -> {o.close(); return true; })
                .orElse(false);
    }

    //удаление заказа
        public boolean deleteOrder(int orderId){
           return findOrderById(orderId)
                .map(o -> {o.close(); return true; })
                .orElse(false);
    }
    //Смещение заказа
    public boolean  shiftOrder(int orderId,int minutes){
        System.out.println("Cмещаем все заказы на " + minutes + "минут");
           return findOrderById(orderId)
                .map(o -> {o.close(); return true; })
                .orElse(false);

    }
    public List<ServiceOrder> getOrders(){
        return Collections.unmodifiableList(orders);
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
    // ----- Выборки / отчёты -----
//Гаражи

    //Заказы
    /** Все заказы, отсортированные по заданному критерию. */
     public List<ServiceOrder> getAllOrdersSorted(OrderSort sort){
            return orders.stream()
                    .sorted(orderComporator(sort))
                    .collect(Collectors.toList());
    }
    /** Текущие выполняемые заказы. */
    public List<ServiceOrder> getCurrentOrderSorted(OrderSort sort){
        LocalDateTime now = LocalDateTime.now(); 
        return orders.stream()
                .filter(o -> isActive(o, now))
                .sorted(orderComporator(sort))
                .collect(Collectors.toList());

    }
    /** Заказ, выполняемый механиком прямо сейчас. */
    public Optional<ServiceOrder> getOrderByMechanicNow(int mechanicId){
        LocalDateTime now = LocalDateTime.now();
        return orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.NEW)
                .filter(o ->o.getMechanic().getId() == mechanicId)
                .filter(o -> o.getTimeSlot().contains(now))
                .findFirst();
    }
    /** Механик, выполняющий конкретный заказ. */
    public Optional <Mechanic> getMechanicByOrderId(int orderId){
        return findOrderById(orderId)
                .map(ServiceOrder::getMechanic);
    }
    /** Заказы за период с фильтром по статусу и сортировкой. */
    public List<ServiceOrder> getOrders(LocalDateTime from,LocalDateTime to,Set<OrderStatus> statuses,OrderSort sort){
        return orders.stream()
                .filter(o -> statuses == null || statuses.contains(o.getStatus()))
                .filter(o -> {
                    if(from == null && to == null) return true;
                    return o.getTimeSlot().getStart().isBefore(to)
                            && o.getTimeSlot().getEnd().isAfter(from);
                })
                .sorted(orderComporator(sort))
                .collect(Collectors.toList());
    }
    //Механики
    /** Список механиков по имени или занятости. */
    public List<Mechanic> getMechanicSorted(MechanicSort sort) {
        if (sort == MechanicSort.BY_NAME) {
            return mechanics.stream()
                    .sorted(Comparator.comparing(Mechanic::getName))
                    .collect(Collectors.toList());
        } else {
            LocalDateTime now = LocalDateTime.now();
            return mechanics.stream()
                    .sorted(Comparator.comparing(Mechanic::getName))
                    .collect(Collectors.toList());
        }
    }
    //Подсчёт текущей занятости механика
    private int currentWorkload(Mechanic m,LocalDateTime when){
        return(int) orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.NEW)
                .filter(o -> o.getMechanic().getId() == m.getId())
                .filter(o -> o.getTimeSlot().contains(when))
                .count();
    }
    /** Свободные места сейчас. */
    public List<GarageSlot> getFreeGarageSlotsNow(){
        return getFreeGarageSlotsAt(LocalDateTime.now());
    }
    /** Свободные места на момент времени. */
    public List <GarageSlot> getFreeGarageSlotsAt(LocalDateTime when){
        List <GarageSlot> free = new ArrayList<>(garageSlots);
        orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.NEW)
                .filter(o -> o.getTimeSlot().contains(when))
                .map(ServiceOrder::getGarageSlot)
                .forEach(free::remove);
        return free;
    }
    /** Кол-во свободных мест на момент (минимум между механиками и местами). */
    public int freeCapacityAt(LocalDateTime when){
        int freeMechanics = (int) mechanics.stream()
                .filter(m -> currentWorkload(m,when) == 0)
                .count();
        int freeGarages = getFreeGarageSlotsAt(when).size();
        return Math.min(freeMechanics,freeGarages);
    }
    /** Поиск ближайшей свободной даты (шаг 15 минут, до 30 дней). */
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
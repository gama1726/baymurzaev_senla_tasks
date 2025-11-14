package autoservice.service;

import autoservice.model.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Главный сервис для управления автосервисом (Singleton).
 * Делегирует вызовы специализированным сервисам.
 */
public class ServiceManager {
    // ----- Singleton -----
    private static final ServiceManager INSTANCE = new ServiceManager();
    public static ServiceManager getInstance(){
        return INSTANCE;
    }
    
    // ----- Специализированные сервисы -----
    private final OrderService orderService;
    private final MechanicService mechanicService;
    private final GarageSlotService garageSlotService;
    private final CapacityService capacityService;

    private ServiceManager(){
        // Инициализация сервисов
        this.orderService = new OrderService();
        this.mechanicService = new MechanicService(orderService);
        this.garageSlotService = new GarageSlotService(orderService);
        this.capacityService = new CapacityService(mechanicService, garageSlotService, orderService);
    }
    // ----- Механики -----
    public void addMechanic(Mechanic mechanic){
        mechanicService.addMechanic(mechanic);
    }
    
    public boolean removeMechanicById(int id){
        return mechanicService.removeMechanicById(id);
    }
    
    public Optional<Mechanic> findMechanicById(int id){
        return mechanicService.findMechanicById(id);
    }
    
    public List<Mechanic> getMechanicSorted(MechanicSort sort) {
        return mechanicService.getMechanicSorted(sort);
    }
    // ----- Гаражи -----
    public void addGarageSlot(GarageSlot slot){
        garageSlotService.addGarageSlot(slot);
    }
    
    public boolean removeGarageSlotById(int id){
        return garageSlotService.removeGarageSlotById(id);
    }
    
    public Optional<GarageSlot> findGarageSlotById(int id){
        return garageSlotService.findGarageSlotById(id);
    }
    
    public List<GarageSlot> getGarageSlots(){
        return garageSlotService.getGarageSlots();
    }
    
    public List<GarageSlot> getFreeGarageSlotsNow(){
        return garageSlotService.getFreeGarageSlotsNow();
    }
    
    public List<GarageSlot> getFreeGarageSlotsAt(LocalDateTime when){
        return garageSlotService.getFreeGarageSlotsAt(when);
    }
    // ----- Заказы -----
    public void addOrder(ServiceOrder order){
        orderService.addOrder(order);
    }
    
    public Optional<ServiceOrder> findOrderById(int id){
        return orderService.findOrderById(id);
    }
    
    public boolean cancelOrder(int orderId){
        return orderService.cancelOrder(orderId);
    }
    
    public boolean closeOrder(int orderId){
        return orderService.closeOrder(orderId);
    }

    public boolean deleteOrder(int orderId){
        return orderService.deleteOrder(orderId);
    }
    
    public boolean shiftOrder(int orderId, int minutes){
        return orderService.shiftOrder(orderId, minutes);
    }
    
    public List<ServiceOrder> getOrders(){
        return orderService.getOrders();
    }
    
    public List<ServiceOrder> getAllOrdersSorted(OrderSort sort){
        return orderService.getAllOrdersSorted(sort);
    }
    
    public List<ServiceOrder> getCurrentOrderSorted(OrderSort sort){
        return orderService.getCurrentOrderSorted(sort);
    }
    
    public Optional<ServiceOrder> getOrderByMechanicNow(int mechanicId){
        return orderService.getOrderByMechanicNow(mechanicId);
    }
    
    public Optional<Mechanic> getMechanicByOrderId(int orderId){
        return orderService.getMechanicByOrderId(orderId);
    }
    
    public List<ServiceOrder> getOrders(LocalDateTime from, LocalDateTime to, Set<OrderStatus> statuses, OrderSort sort){
        return orderService.getOrders(from, to, statuses, sort);
    }
    
    public void printList(String title, List<?> list){
        orderService.printList(title, list);
    }
    
    public void demoPeriodReport(LocalDateTime from, LocalDateTime to){
        orderService.demoPeriodReport(from, to);
    }

    // ----- Слоты и свободные даты -----
    public int freeCapacityAt(LocalDateTime when){
        return capacityService.freeCapacityAt(when);
    }
    
    public LocalDateTime findNextFreeDate(LocalDateTime from){
        return capacityService.findNextFreeDate(from);
    }
}
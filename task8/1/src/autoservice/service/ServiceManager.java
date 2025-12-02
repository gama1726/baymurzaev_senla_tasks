package autoservice.service;

import autoservice.annotation.Component;
import autoservice.annotation.Inject;
import autoservice.exception.AutoserviceException;
import autoservice.exception.EntityNotFoundException;
import autoservice.exception.ImportExportException;
import autoservice.model.*;
import autoservice.service.importexport.GarageSlotImportExportService;
import autoservice.service.importexport.MechanicImportExportService;
import autoservice.service.importexport.OrderImportExportService;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Главный сервис для управления автосервисом (Singleton через DI).
 * Делегирует вызовы специализированным сервисам.
 */
@Component
public class ServiceManager {
    // ----- Специализированные сервисы -----
    @Inject
    private OrderService orderService;
    
    @Inject
    private MechanicService mechanicService;
    
    @Inject
    private GarageSlotService garageSlotService;
    
    @Inject
    private CapacityService capacityService;
    
    // ----- Сервисы импорта/экспорта -----
    @Inject
    private MechanicImportExportService mechanicImportExportService;
    
    @Inject
    private GarageSlotImportExportService garageSlotImportExportService;
    
    @Inject
    private OrderImportExportService orderImportExportService;
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

    // ----- Импорт/Экспорт -----
    
    /**
     * Экспортирует механиков в CSV файл.
     */
    public void exportMechanicsToCsv(String filePath) throws ImportExportException {
        mechanicImportExportService.exportToCsv(filePath);
    }

    /**
     * Импортирует механиков из CSV файла.
     */
    public void importMechanicsFromCsv(String filePath) throws ImportExportException {
        mechanicImportExportService.importFromCsv(filePath);
    }

    /**
     * Экспортирует гаражные места в CSV файл.
     */
    public void exportGarageSlotsToCsv(String filePath) throws ImportExportException {
        garageSlotImportExportService.exportToCsv(filePath);
    }

    /**
     * Импортирует гаражные места из CSV файла.
     */
    public void importGarageSlotsFromCsv(String filePath) throws ImportExportException {
        garageSlotImportExportService.importFromCsv(filePath);
    }

    /**
     * Экспортирует заказы в CSV файл.
     */
    public void exportOrdersToCsv(String filePath) throws ImportExportException {
        orderImportExportService.exportToCsv(filePath);
    }

    /**
     * Импортирует заказы из CSV файла с автоматическим связыванием объектов.
     */
    public void importOrdersFromCsv(String filePath) throws ImportExportException, EntityNotFoundException {
        orderImportExportService.importFromCsv(filePath);
    }
    
    // ----- Геттеры для доступа к сервисам -----
    
    public List<Mechanic> getAllMechanics() {
        return mechanicService.getAllMechanics();
    }
}
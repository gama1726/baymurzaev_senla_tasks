package autoservice.service;

import autoservice.model.GarageSlot;
import autoservice.model.OrderStatus;
import autoservice.model.ServiceOrder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для управления гаражными местами.
 */
public class GarageSlotService {
    private final List<GarageSlot> garageSlots = new ArrayList<>();
    private final OrderService orderService;

    public GarageSlotService(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Добавить гаражное место.
     */
    public void addGarageSlot(GarageSlot slot) {
        garageSlots.add(slot);
        System.out.println("Добавлено место " + slot);
    }

    /**
     * Удалить гаражное место по ID.
     */
    public boolean removeGarageSlotById(int id) {
        boolean removed = garageSlots.removeIf(s -> s.getId() == id);
        if (removed) {
            System.out.println("Удалено место № " + id);
        } else {
            System.out.println("Место № " + id + " не найдено.");
        }
        return removed;
    }

    /**
     * Найти гаражное место по ID.
     */
    public Optional<GarageSlot> findGarageSlotById(int id) {
        return garageSlots.stream()
                .filter(s -> s.getId() == id)
                .findFirst();
    }

    /**
     * Получить список всех гаражных мест.
     */
    public List<GarageSlot> getGarageSlots() {
        return Collections.unmodifiableList(garageSlots);
    }

    /**
     * Получить список свободных гаражных мест на текущий момент.
     */
    public List<GarageSlot> getFreeGarageSlotsNow() {
        return getFreeGarageSlotsAt(LocalDateTime.now());
    }

    /**
     * Получить список свободных гаражных мест на момент времени.
     */
    public List<GarageSlot> getFreeGarageSlotsAt(LocalDateTime when) {
        List<GarageSlot> free = new ArrayList<>(garageSlots);
        orderService.getOrders().stream()
                .filter(o -> o.getStatus() == OrderStatus.NEW)
                .filter(o -> o.getTimeSlot().contains(when))
                .map(ServiceOrder::getGarageSlot)
                .forEach(free::remove);
        return free;
    }

    /**
     * Получить количество свободных гаражных мест на момент времени.
     */
    public int getFreeGarageSlotsCount(LocalDateTime when) {
        return getFreeGarageSlotsAt(when).size();
    }
}



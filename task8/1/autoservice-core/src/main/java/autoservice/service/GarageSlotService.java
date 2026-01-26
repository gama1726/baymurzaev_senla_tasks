package autoservice.service;

import autoservice.annotation.Component;
import autoservice.annotation.Inject;
import autoservice.dao.GarageSlotDAO;
import autoservice.model.GarageSlot;
import autoservice.model.OrderStatus;
import autoservice.model.ServiceOrder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для управления гаражными местами.
 */
@Component
public class GarageSlotService {
    
    private static final Logger logger = LogManager.getLogger(GarageSlotService.class);
    
    @Inject
    private GarageSlotDAO garageSlotDAO;
    
    // OrderService получаем через lazy injection, чтобы избежать циклической зависимости
    private OrderService orderService;

    /**
     * Добавить гаражное место.
     */
    public void addGarageSlot(GarageSlot slot) {
        try {
            garageSlotDAO.save(slot);
            System.out.println("Добавлено место " + slot);
            logger.info("GarageSlot added: {}", slot);
        } catch (Exception e) {
            logger.error("Error adding garage slot: {}", slot, e);
            throw new RuntimeException("Ошибка при добавлении гаражного места: " + e.getMessage(), e);
        }
    }

    /**
     * Удалить гаражное место по ID.
     */
    public boolean removeGarageSlotById(int id) {
        try {
            boolean removed = garageSlotDAO.deleteById(id);
            if (removed) {
                System.out.println("Удалено место № " + id);
                logger.info("GarageSlot removed: id={}", id);
            } else {
                System.out.println("Место № " + id + " не найдено.");
                logger.warn("GarageSlot not found for removal: id={}", id);
            }
            return removed;
        } catch (Exception e) {
            logger.error("Error removing garage slot: id={}", id, e);
            throw new RuntimeException("Ошибка при удалении гаражного места: " + e.getMessage(), e);
        }
    }

    /**
     * Найти гаражное место по ID.
     */
    public Optional<GarageSlot> findGarageSlotById(int id) {
        try {
            return garageSlotDAO.findById(id);
        } catch (Exception e) {
            logger.error("Error finding garage slot: id={}", id, e);
            throw new RuntimeException("Ошибка при поиске гаражного места: " + e.getMessage(), e);
        }
    }

    /**
     * Получить список всех гаражных мест.
     */
    public List<GarageSlot> getGarageSlots() {
        try {
            return garageSlotDAO.findAll();
        } catch (Exception e) {
            logger.error("Error getting all garage slots", e);
            throw new RuntimeException("Ошибка при получении списка гаражных мест: " + e.getMessage(), e);
        }
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
        try {
            List<GarageSlot> allSlots = getGarageSlots();
            List<GarageSlot> free = new ArrayList<>(allSlots);
            // Получаем OrderService через DAO напрямую, чтобы избежать циклической зависимости
            if (orderService == null) {
                // Lazy injection через DIContainer
                orderService = autoservice.injection.DIContainer.getInstance().getInstance(OrderService.class);
            }
            orderService.getOrders().stream()
                    .filter(o -> o.getStatus() == OrderStatus.NEW)
                    .filter(o -> o.getTimeSlot().contains(when))
                    .map(ServiceOrder::getGarageSlot)
                    .forEach(free::remove);
            return free;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении свободных мест: " + e.getMessage(), e);
        }
    }

    /**
     * Получить количество свободных гаражных мест на момент времени.
     */
    public int getFreeGarageSlotsCount(LocalDateTime when) {
        return getFreeGarageSlotsAt(when).size();
    }
    
    /**
     * Обновить статус гаражного места в БД.
     */
    public void updateGarageSlot(GarageSlot slot) {
        try {
            garageSlotDAO.update(slot);
            logger.debug("GarageSlot updated: {}", slot);
        } catch (Exception e) {
            logger.error("Error updating garage slot: {}", slot, e);
            throw new RuntimeException("Ошибка при обновлении гаражного места: " + e.getMessage(), e);
        }
    }
}



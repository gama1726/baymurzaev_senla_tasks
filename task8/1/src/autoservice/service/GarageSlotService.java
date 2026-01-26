package autoservice.service;

import autoservice.annotation.Component;
import autoservice.annotation.Inject;
import autoservice.dao.GarageSlotDAO;
import autoservice.database.ConnectionManager;
import autoservice.model.GarageSlot;
import autoservice.model.OrderStatus;
import autoservice.model.ServiceOrder;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для управления гаражными местами.
 */
@Component
public class GarageSlotService {
    
    @Inject
    private GarageSlotDAO garageSlotDAO;
    
    @Inject
    private ConnectionManager connectionManager;
    
    // OrderService получаем через lazy injection, чтобы избежать циклической зависимости
    private OrderService orderService;

    /**
     * Добавить гаражное место.
     */
    public void addGarageSlot(GarageSlot slot) {
        Connection conn = connectionManager.getConnection();
        try {
            garageSlotDAO.save(slot);
            conn.commit();
            System.out.println("Добавлено место " + slot);
        } catch (SQLException e) {
            rollback(conn);
            throw new RuntimeException("Ошибка при добавлении гаражного места: " + e.getMessage(), e);
        }
    }

    /**
     * Удалить гаражное место по ID.
     */
    public boolean removeGarageSlotById(int id) {
        Connection conn = connectionManager.getConnection();
        try {
            boolean removed = garageSlotDAO.deleteById(id);
            conn.commit();
            if (removed) {
                System.out.println("Удалено место № " + id);
            } else {
                System.out.println("Место № " + id + " не найдено.");
            }
            return removed;
        } catch (SQLException e) {
            rollback(conn);
            throw new RuntimeException("Ошибка при удалении гаражного места: " + e.getMessage(), e);
        }
    }

    /**
     * Найти гаражное место по ID.
     */
    public Optional<GarageSlot> findGarageSlotById(int id) {
        try {
            return garageSlotDAO.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске гаражного места: " + e.getMessage(), e);
        }
    }

    /**
     * Получить список всех гаражных мест.
     */
    public List<GarageSlot> getGarageSlots() {
        try {
            return garageSlotDAO.findAll();
        } catch (SQLException e) {
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
        Connection conn = connectionManager.getConnection();
        try {
            garageSlotDAO.update(slot);
            conn.commit();
        } catch (SQLException e) {
            rollback(conn);
            throw new RuntimeException("Ошибка при обновлении гаражного места: " + e.getMessage(), e);
        }
    }
    
    /**
     * Откатывает транзакцию при ошибке.
     */
    private void rollback(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.rollback();
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при откате транзакции: " + e.getMessage());
        }
    }
}



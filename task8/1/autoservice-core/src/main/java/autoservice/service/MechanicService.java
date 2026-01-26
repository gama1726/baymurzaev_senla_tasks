package autoservice.service;

import autoservice.annotation.Component;
import autoservice.annotation.Inject;
import autoservice.dao.MechanicDAO;
import autoservice.database.ConnectionManager;
import autoservice.exception.AutoserviceException;
import autoservice.model.Mechanic;
import autoservice.model.MechanicSort;
import autoservice.model.OrderStatus;
import autoservice.model.ServiceOrder;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для управления механиками.
 */
@Component
public class MechanicService {
    
    @Inject
    private MechanicDAO mechanicDAO;
    
    @Inject
    private ConnectionManager connectionManager;
    
    // OrderService получаем через lazy injection, чтобы избежать циклической зависимости
    private OrderService orderService;

    /**
     * Добавить механика.
     */
    public void addMechanic(Mechanic mechanic) {
        Connection conn = connectionManager.getConnection();
        try {
            mechanicDAO.save(mechanic);
            conn.commit();
            System.out.println("Добавлен " + mechanic);
        } catch (SQLException e) {
            rollback(conn);
            throw new RuntimeException("Ошибка при добавлении механика: " + e.getMessage(), e);
        }
    }

    /**
     * Удалить механика по ID.
     */
    public boolean removeMechanicById(int id) {
        Connection conn = connectionManager.getConnection();
        try {
            boolean removed = mechanicDAO.deleteById(id);
            conn.commit();
            if (removed) {
                System.out.println("Удалён механик № " + id);
            } else {
                System.out.println("Механик № " + id + " не найден.");
            }
            return removed;
        } catch (SQLException e) {
            rollback(conn);
            throw new RuntimeException("Ошибка при удалении механика: " + e.getMessage(), e);
        }
    }

    /**
     * Найти механика по ID.
     */
    public Optional<Mechanic> findMechanicById(int id) {
        try {
            return mechanicDAO.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске механика: " + e.getMessage(), e);
        }
    }

    /**
     * Получить список всех механиков.
     */
    public List<Mechanic> getAllMechanics() {
        try {
            return mechanicDAO.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка механиков: " + e.getMessage(), e);
        }
    }

    /**
     * Получить список механиков, отсортированных по заданному критерию.
     */
    public List<Mechanic> getMechanicSorted(MechanicSort sort) {
        try {
            List<Mechanic> mechanics = getAllMechanics();
            if (sort == MechanicSort.BY_NAME) {
                return mechanics.stream()
                        .sorted(Comparator.comparing(Mechanic::getName))
                        .collect(Collectors.toList());
            } else {
                LocalDateTime now = LocalDateTime.now();
                return mechanics.stream()
                        .sorted(Comparator.comparing((Mechanic m) -> currentWorkload(m, now))
                                .thenComparing(Mechanic::getName))
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сортировке механиков: " + e.getMessage(), e);
        }
    }

    /**
     * Подсчитать текущую занятость механика.
     */
    private int currentWorkload(Mechanic m, LocalDateTime when) {
        if (orderService == null) {
            // Lazy injection через DIContainer
            orderService = autoservice.injection.DIContainer.getInstance().getInstance(OrderService.class);
        }
        return (int) orderService.getOrders().stream()
                .filter(o -> o.getStatus() == OrderStatus.NEW)
                .filter(o -> o.getMechanic().getId() == m.getId())
                .filter(o -> o.getTimeSlot().contains(when))
                .count();
    }

    /**
     * Получить количество свободных механиков на момент времени.
     */
    public int getFreeMechanicsCount(LocalDateTime when) {
        try {
            List<Mechanic> mechanics = getAllMechanics();
            return (int) mechanics.stream()
                    .filter(m -> currentWorkload(m, when) == 0)
                    .count();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при подсчете свободных механиков: " + e.getMessage(), e);
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



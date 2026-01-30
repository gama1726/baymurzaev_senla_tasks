package autoservice.service;

import autoservice.dao.MechanicDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import autoservice.model.Mechanic;
import autoservice.model.MechanicSort;
import autoservice.model.OrderStatus;
import autoservice.model.ServiceOrder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для управления механиками.
 */
@Service
public class MechanicService {

    private static final Logger logger = LogManager.getLogger(MechanicService.class);

    @Autowired
    private MechanicDAO mechanicDAO;

    @Autowired
    @Lazy
    private OrderService orderService;

    /**
     * Добавить механика.
     */
    public void addMechanic(Mechanic mechanic) {
        try {
            mechanicDAO.save(mechanic);
            System.out.println("Добавлен " + mechanic);
            logger.info("Mechanic added: {}", mechanic);
        } catch (Exception e) {
            logger.error("Error adding mechanic: {}", mechanic, e);
            throw new RuntimeException("Ошибка при добавлении механика: " + e.getMessage(), e);
        }
    }

    /**
     * Удалить механика по ID.
     */
    public boolean removeMechanicById(int id) {
        try {
            boolean removed = mechanicDAO.deleteById(id);
            if (removed) {
                System.out.println("Удалён механик № " + id);
                logger.info("Mechanic removed: id={}", id);
            } else {
                System.out.println("Механик № " + id + " не найден.");
                logger.warn("Mechanic not found for removal: id={}", id);
            }
            return removed;
        } catch (Exception e) {
            logger.error("Error removing mechanic: id={}", id, e);
            throw new RuntimeException("Ошибка при удалении механика: " + e.getMessage(), e);
        }
    }

    /**
     * Найти механика по ID.
     */
    public Optional<Mechanic> findMechanicById(int id) {
        try {
            return mechanicDAO.findById(id);
        } catch (Exception e) {
            logger.error("Error finding mechanic: id={}", id, e);
            throw new RuntimeException("Ошибка при поиске механика: " + e.getMessage(), e);
        }
    }

    /**
     * Получить список всех механиков.
     */
    public List<Mechanic> getAllMechanics() {
        try {
            return mechanicDAO.findAll();
        } catch (Exception e) {
            logger.error("Error getting all mechanics", e);
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
    
}



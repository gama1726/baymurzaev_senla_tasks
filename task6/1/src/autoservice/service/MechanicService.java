package autoservice.service;

import autoservice.model.Mechanic;
import autoservice.model.MechanicSort;
import autoservice.model.OrderStatus;
import autoservice.model.ServiceOrder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для управления механиками.
 */
public class MechanicService {
    private final List<Mechanic> mechanics = new ArrayList<>();
    private final OrderService orderService;

    public MechanicService(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Добавить механика.
     */
    public void addMechanic(Mechanic mechanic) {
        mechanics.add(mechanic);
        System.out.println("Добавлен " + mechanic);
    }

    /**
     * Удалить механика по ID.
     */
    public boolean removeMechanicById(int id) {
        boolean removed = mechanics.removeIf(m -> m.getId() == id);
        if (removed) {
            System.out.println("Удалён механик № " + id);
        } else {
            System.out.println("Механик № " + id + " не найден.");
        }
        return removed;
    }

    /**
     * Найти механика по ID.
     */
    public Optional<Mechanic> findMechanicById(int id) {
        return mechanics.stream()
                .filter(m -> m.getId() == id)
                .findFirst();
    }

    /**
     * Получить список всех механиков.
     */
    public List<Mechanic> getAllMechanics() {
        return Collections.unmodifiableList(mechanics);
    }

    /**
     * Получить список механиков, отсортированных по заданному критерию.
     */
    public List<Mechanic> getMechanicSorted(MechanicSort sort) {
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
        return (int) mechanics.stream()
                .filter(m -> currentWorkload(m, when) == 0)
                .count();
    }
}



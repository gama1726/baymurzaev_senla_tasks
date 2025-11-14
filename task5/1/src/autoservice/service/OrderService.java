package autoservice.service;

import autoservice.model.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для управления заказами.
 */
public class OrderService {
    private final List<ServiceOrder> orders = new ArrayList<>();

    /**
     * Добавить заказ.
     */
    public void addOrder(ServiceOrder order) {
        orders.add(order);
        System.out.println("Добавлен заказ:\n" + order);
    }

    /**
     * Найти заказ по ID.
     */
    public Optional<ServiceOrder> findOrderById(int id) {
        return orders.stream()
                .filter(o -> o.getId() == id)
                .findFirst();
    }

    /**
     * Получить список всех заказов.
     */
    public List<ServiceOrder> getOrders() {
        return Collections.unmodifiableList(orders);
    }

    /**
     * Отменить заказ.
     */
    public boolean cancelOrder(int orderId) {
        return findOrderById(orderId)
                .map(o -> {o.cancel(); return true; })
                .orElse(false);
    }

    /**
     * Закрыть заказ.
     */
    public boolean closeOrder(int orderId) {
        return findOrderById(orderId)
                .map(o -> {o.close(); return true; })
                .orElse(false);
    }

    /**
     * Удалить заказ.
     */
    public boolean deleteOrder(int orderId) {
        return findOrderById(orderId)
                .map(o -> {o.markDeleted(); return true; })
                .orElse(false);
    }

    /**
     * Сместить время заказа.
     */
    public boolean shiftOrder(int orderId, int minutes) {
        return findOrderById(orderId)
                .map(o -> {o.shift(minutes); return true; })
                .orElse(false);
    }

    /**
     * Проверить, активен ли заказ в данный момент.
     */
    private boolean isActive(ServiceOrder o, LocalDateTime when) {
        return o.getStatus() == OrderStatus.NEW && o.getTimeSlot().contains(when);
    }

    /**
     * Получить компаратор для сортировки заказов.
     */
    private Comparator<ServiceOrder> orderComparator(OrderSort sort) {
        switch (sort) {
            case BY_SUBMIT_DATE:
                return Comparator.comparing(ServiceOrder::getSubmittedAt);
            case BY_PLANNED_START:
                return Comparator.comparing(o -> o.getTimeSlot().getStart());
            case BY_FINISH_DATE:
                return Comparator.comparing(o -> {
                    LocalDateTime f = o.getFinishedAt();
                    return f == null ? LocalDateTime.MAX : f;
                });
            case BY_PRICE:
                return Comparator.comparingInt(ServiceOrder::getPrice);
            default:
                return Comparator.comparingInt(ServiceOrder::getId);
        }
    }

    /**
     * Получить все заказы, отсортированные по заданному критерию.
     */
    public List<ServiceOrder> getAllOrdersSorted(OrderSort sort) {
        return orders.stream()
                .sorted(orderComparator(sort))
                .collect(Collectors.toList());
    }

    /**
     * Получить текущие выполняемые заказы, отсортированные по заданному критерию.
     */
    public List<ServiceOrder> getCurrentOrderSorted(OrderSort sort) {
        LocalDateTime now = LocalDateTime.now();
        return orders.stream()
                .filter(o -> isActive(o, now))
                .sorted(orderComparator(sort))
                .collect(Collectors.toList());
    }

    /**
     * Получить заказ, выполняемый механиком прямо сейчас.
     */
    public Optional<ServiceOrder> getOrderByMechanicNow(int mechanicId) {
        LocalDateTime now = LocalDateTime.now();
        return orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.NEW)
                .filter(o -> o.getMechanic().getId() == mechanicId)
                .filter(o -> o.getTimeSlot().contains(now))
                .findFirst();
    }

    /**
     * Получить механика, выполняющего конкретный заказ.
     */
    public Optional<Mechanic> getMechanicByOrderId(int orderId) {
        return findOrderById(orderId)
                .map(ServiceOrder::getMechanic);
    }

    /**
     * Получить заказы за период с фильтром по статусу и сортировкой.
     */
    public List<ServiceOrder> getOrders(LocalDateTime from, LocalDateTime to, Set<OrderStatus> statuses, OrderSort sort) {
        return orders.stream()
                .filter(o -> statuses == null || statuses.contains(o.getStatus()))
                .filter(o -> {
                    if (from == null && to == null) return true;
                    return o.getTimeSlot().getStart().isBefore(to)
                            && o.getTimeSlot().getEnd().isAfter(from);
                })
                .sorted(orderComparator(sort))
                .collect(Collectors.toList());
    }

    /**
     * Вывести заголовок и список в консоль.
     */
    public void printList(String title, List<?> list) {
        System.out.println("\n" + title + ":");
        if (list.isEmpty()) {
            System.out.println("пусто");
        } else {
            for (Object o : list) {
                System.out.println(o);
            }
        }
    }

    /**
     * Печатает отчёт по заказам за период.
     */
    public void demoPeriodReport(LocalDateTime from, LocalDateTime to) {
        Set<OrderStatus> st = EnumSet.of(OrderStatus.NEW, OrderStatus.CLOSED, OrderStatus.CANCELED, OrderStatus.DELETED);
        List<ServiceOrder> list = getOrders(from, to, st, OrderSort.BY_PLANNED_START);
        printList("Отчет по заказам [" + from + ".." + to + "]", list);
    }
}



package autoservice.service;

import autoservice.annotation.Component;
import autoservice.annotation.Inject;
import autoservice.dao.ServiceOrderDAO;
import autoservice.database.ConnectionManager;
import autoservice.model.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для управления заказами.
 */
@Component
public class OrderService {
    
    @Inject
    private ServiceOrderDAO orderDAO;
    
    @Inject
    private GarageSlotService garageSlotService;
    
    @Inject
    private ConnectionManager connectionManager;

    /**
     * Добавить заказ.
     * Транзакция: сохраняет заказ и обновляет статус гаражного места.
     */
    public void addOrder(ServiceOrder order) {
        Connection conn = connectionManager.getConnection();
        try {
            // Сохраняем заказ
            orderDAO.save(order);
            
            // Обновляем статус гаражного места (занято)
            garageSlotService.updateGarageSlot(order.getGarageSlot());
            
            conn.commit();
            System.out.println("Добавлен заказ:\n" + order);
        } catch (SQLException e) {
            rollback(conn);
            throw new RuntimeException("Ошибка при добавлении заказа: " + e.getMessage(), e);
        }
    }

    /**
     * Найти заказ по ID.
     */
    public Optional<ServiceOrder> findOrderById(int id) {
        try {
            return orderDAO.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске заказа: " + e.getMessage(), e);
        }
    }

    /**
     * Получить список всех заказов.
     */
    public List<ServiceOrder> getOrders() {
        try {
            return orderDAO.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка заказов: " + e.getMessage(), e);
        }
    }

    /**
     * Отменить заказ.
     * Транзакция: обновляет статус заказа и освобождает гаражное место.
     */
    public boolean cancelOrder(int orderId) {
        Connection conn = connectionManager.getConnection();
        try {
            Optional<ServiceOrder> orderOpt = orderDAO.findById(orderId);
            if (orderOpt.isEmpty()) {
                return false;
            }
            
            ServiceOrder order = orderOpt.get();
            order.cancel();
            orderDAO.update(order);
            
            // Освобождаем гаражное место
            garageSlotService.updateGarageSlot(order.getGarageSlot());
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            rollback(conn);
            throw new RuntimeException("Ошибка при отмене заказа: " + e.getMessage(), e);
        }
    }

    /**
     * Закрыть заказ.
     * Транзакция: обновляет статус заказа и освобождает гаражное место.
     */
    public boolean closeOrder(int orderId) {
        Connection conn = connectionManager.getConnection();
        try {
            Optional<ServiceOrder> orderOpt = orderDAO.findById(orderId);
            if (orderOpt.isEmpty()) {
                return false;
            }
            
            ServiceOrder order = orderOpt.get();
            order.close();
            orderDAO.update(order);
            
            // Освобождаем гаражное место
            garageSlotService.updateGarageSlot(order.getGarageSlot());
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            rollback(conn);
            throw new RuntimeException("Ошибка при закрытии заказа: " + e.getMessage(), e);
        }
    }

    /**
     * Удалить заказ (пометить как удалённый).
     * Транзакция: обновляет статус заказа и освобождает гаражное место.
     */
    public boolean deleteOrder(int orderId) {
        Connection conn = connectionManager.getConnection();
        try {
            Optional<ServiceOrder> orderOpt = orderDAO.findById(orderId);
            if (orderOpt.isEmpty()) {
                return false;
            }
            
            ServiceOrder order = orderOpt.get();
            order.markDeleted();
            orderDAO.update(order);
            
            // Освобождаем гаражное место
            garageSlotService.updateGarageSlot(order.getGarageSlot());
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            rollback(conn);
            throw new RuntimeException("Ошибка при удалении заказа: " + e.getMessage(), e);
        }
    }

    /**
     * Физически удалить заказ из БД (для импорта).
     */
    public boolean removeOrderById(int orderId) {
        Connection conn = connectionManager.getConnection();
        try {
            boolean removed = orderDAO.deleteById(orderId);
            conn.commit();
            return removed;
        } catch (SQLException e) {
            rollback(conn);
            throw new RuntimeException("Ошибка при физическом удалении заказа: " + e.getMessage(), e);
        }
    }

    /**
     * Сместить время заказа.
     * Транзакция: обновляет время заказа в БД.
     */
    public boolean shiftOrder(int orderId, int minutes) {
        Connection conn = connectionManager.getConnection();
        try {
            Optional<ServiceOrder> orderOpt = orderDAO.findById(orderId);
            if (orderOpt.isEmpty()) {
                return false;
            }
            
            ServiceOrder order = orderOpt.get();
            order.shift(minutes);
            orderDAO.update(order);
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            rollback(conn);
            throw new RuntimeException("Ошибка при смещении времени заказа: " + e.getMessage(), e);
        }
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
        try {
            List<ServiceOrder> orders = getOrders();
            return orders.stream()
                    .sorted(orderComparator(sort))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сортировке заказов: " + e.getMessage(), e);
        }
    }

    /**
     * Получить текущие выполняемые заказы, отсортированные по заданному критерию.
     */
    public List<ServiceOrder> getCurrentOrderSorted(OrderSort sort) {
        try {
            LocalDateTime now = LocalDateTime.now();
            List<ServiceOrder> orders = getOrders();
            return orders.stream()
                    .filter(o -> isActive(o, now))
                    .sorted(orderComparator(sort))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении текущих заказов: " + e.getMessage(), e);
        }
    }

    /**
     * Получить заказ, выполняемый механиком прямо сейчас.
     */
    public Optional<ServiceOrder> getOrderByMechanicNow(int mechanicId) {
        try {
            LocalDateTime now = LocalDateTime.now();
            List<ServiceOrder> orders = getOrders();
            return orders.stream()
                    .filter(o -> o.getStatus() == OrderStatus.NEW)
                    .filter(o -> o.getMechanic().getId() == mechanicId)
                    .filter(o -> o.getTimeSlot().contains(now))
                    .findFirst();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при поиске заказа механика: " + e.getMessage(), e);
        }
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
        try {
            List<ServiceOrder> orders = getOrders();
            return orders.stream()
                    .filter(o -> statuses == null || statuses.contains(o.getStatus()))
                    .filter(o -> {
                        if (from == null && to == null) return true;
                        return o.getTimeSlot().getStart().isBefore(to)
                                && o.getTimeSlot().getEnd().isAfter(from);
                    })
                    .sorted(orderComparator(sort))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении заказов за период: " + e.getMessage(), e);
        }
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

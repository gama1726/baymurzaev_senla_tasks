package autoservice.service;

import autoservice.dao.ServiceOrderDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import autoservice.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для управления заказами.
 */
@Service
public class OrderService {

    private static final Logger logger = LogManager.getLogger(OrderService.class);

    @Autowired
    private ServiceOrderDAO orderDAO;

    @Autowired
    private GarageSlotService garageSlotService;

    /**
     * Добавить заказ.
     * Транзакция: сохраняет заказ и обновляет статус гаражного места.
     */
    public void addOrder(ServiceOrder order) {
        try {
            // Сохраняем заказ
            orderDAO.save(order);
            
            // Обновляем статус гаражного места (занято)
            garageSlotService.updateGarageSlot(order.getGarageSlot());
            
            System.out.println("Добавлен заказ:\n" + order);
            logger.info("ServiceOrder added: {}", order);
        } catch (Exception e) {
            logger.error("Error adding service order: {}", order, e);
            throw new RuntimeException("Ошибка при добавлении заказа: " + e.getMessage(), e);
        }
    }

    /**
     * Найти заказ по ID.
     */
    public Optional<ServiceOrder> findOrderById(int id) {
        try {
            return orderDAO.findById(id);
        } catch (Exception e) {
            logger.error("Error finding service order: id={}", id, e);
            throw new RuntimeException("Ошибка при поиске заказа: " + e.getMessage(), e);
        }
    }

    /**
     * Получить список всех заказов.
     */
    public List<ServiceOrder> getOrders() {
        try {
            return orderDAO.findAll();
        } catch (Exception e) {
            logger.error("Error getting all service orders", e);
            throw new RuntimeException("Ошибка при получении списка заказов: " + e.getMessage(), e);
        }
    }

    /**
     * Отменить заказ.
     * Транзакция: обновляет статус заказа и освобождает гаражное место.
     */
    public boolean cancelOrder(int orderId) {
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
            
            logger.info("ServiceOrder canceled: id={}", orderId);
            return true;
        } catch (Exception e) {
            logger.error("Error canceling service order: id={}", orderId, e);
            throw new RuntimeException("Ошибка при отмене заказа: " + e.getMessage(), e);
        }
    }

    /**
     * Закрыть заказ.
     * Транзакция: обновляет статус заказа и освобождает гаражное место.
     */
    public boolean closeOrder(int orderId) {
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
            
            logger.info("ServiceOrder closed: id={}", orderId);
            return true;
        } catch (Exception e) {
            logger.error("Error closing service order: id={}", orderId, e);
            throw new RuntimeException("Ошибка при закрытии заказа: " + e.getMessage(), e);
        }
    }

    /**
     * Удалить заказ (пометить как удалённый).
     * Транзакция: обновляет статус заказа и освобождает гаражное место.
     */
    public boolean deleteOrder(int orderId) {
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
            
            logger.info("ServiceOrder deleted: id={}", orderId);
            return true;
        } catch (Exception e) {
            logger.error("Error deleting service order: id={}", orderId, e);
            throw new RuntimeException("Ошибка при удалении заказа: " + e.getMessage(), e);
        }
    }

    /**
     * Физически удалить заказ из БД (для импорта).
     */
    public boolean removeOrderById(int orderId) {
        try {
            boolean removed = orderDAO.deleteById(orderId);
            if (removed) {
                logger.info("ServiceOrder physically removed: id={}", orderId);
            }
            return removed;
        } catch (Exception e) {
            logger.error("Error physically removing service order: id={}", orderId, e);
            throw new RuntimeException("Ошибка при физическом удалении заказа: " + e.getMessage(), e);
        }
    }

    /**
     * Сместить время заказа.
     * Транзакция: обновляет время заказа в БД.
     */
    public boolean shiftOrder(int orderId, int minutes) {
        try {
            Optional<ServiceOrder> orderOpt = orderDAO.findById(orderId);
            if (orderOpt.isEmpty()) {
                return false;
            }
            
            ServiceOrder order = orderOpt.get();
            order.shift(minutes);
            orderDAO.update(order);
            
            logger.info("ServiceOrder shifted: id={}, minutes={}", orderId, minutes);
            return true;
        } catch (Exception e) {
            logger.error("Error shifting service order: id={}, minutes={}", orderId, minutes, e);
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
    
}

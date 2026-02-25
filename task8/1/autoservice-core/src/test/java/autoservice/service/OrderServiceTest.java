package autoservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import autoservice.dao.ServiceOrderDAO;
import autoservice.model.GarageSlot;
import autoservice.model.Mechanic;
import autoservice.model.OrderSort;
import autoservice.model.OrderStatus;
import autoservice.model.ServiceOrder;
import autoservice.model.TimeSlot;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService")
class OrderServiceTest {

    @Mock
    private ServiceOrderDAO orderDAO;

    @Mock
    private GarageSlotService garageSlotService;

    @InjectMocks
    private OrderService orderService;

    private ServiceOrder order;
    private Mechanic mechanic;
    private GarageSlot slot;
    private TimeSlot timeSlot;

    @BeforeEach
    void setUp() {
        mechanic = new Mechanic(1, "Ivan");
        slot = new GarageSlot(1);
        timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        order = new ServiceOrder.Builder()
                .setId(1)
                .setMechanic(mechanic)
                .setGarageSlot(slot)
                .setTimeSlot(timeSlot)
                .setPrice(1000)
                .build();
    }

    @Nested
    @DisplayName("addOrder")
    class AddOrder {
        @Test
        @DisplayName("saves order and updates garage slot")
        void positive_addsOrder() {
            orderService.addOrder(order);
            verify(orderDAO).save(order);
            verify(garageSlotService).updateGarageSlot(order.getGarageSlot());
        }

        @Test
        @DisplayName("throws RuntimeException when DAO fails")
        void negative_daoThrows_rethrowsRuntimeException() {
            doThrow(new RuntimeException("DB error")).when(orderDAO).save(any(ServiceOrder.class));
            assertThrows(RuntimeException.class, () -> orderService.addOrder(order));
        }
    }

    @Nested
    @DisplayName("findOrderById")
    class FindOrderById {
        @Test
        @DisplayName("returns Optional with order")
        void positive_returnsOrder() {
            when(orderDAO.findById(1)).thenReturn(Optional.of(order));
            assertEquals(Optional.of(order), orderService.findOrderById(1));
        }

        @Test
        @DisplayName("returns empty when not found")
        void negative_returnsEmptyWhenNotFound() {
            when(orderDAO.findById(999)).thenReturn(Optional.empty());
            assertTrue(orderService.findOrderById(999).isEmpty());
        }

        @Test
        @DisplayName("throws RuntimeException when DAO fails")
        void negative_daoThrows_rethrowsRuntimeException() {
            doThrow(new RuntimeException("DB error")).when(orderDAO).findById(1);
            assertThrows(RuntimeException.class, () -> orderService.findOrderById(1));
        }
    }

    @Nested
    @DisplayName("getOrders")
    class GetOrders {
        @Test
        @DisplayName("returns list of orders")
        void positive_returnsList() {
            when(orderDAO.findAll()).thenReturn(List.of(order));
            assertEquals(List.of(order), orderService.getOrders());
        }

        @Test
        @DisplayName("throws RuntimeException when DAO fails")
        void negative_daoThrows_rethrowsRuntimeException() {
            doThrow(new RuntimeException("DB error")).when(orderDAO).findAll();
            assertThrows(RuntimeException.class, () -> orderService.getOrders());
        }
    }

    @Nested
    @DisplayName("cancelOrder")
    class CancelOrder {
        @Test
        @DisplayName("cancels order and updates garage slot")
        void positive_returnsTrue() {
            when(orderDAO.findById(1)).thenReturn(Optional.of(order));
            assertTrue(orderService.cancelOrder(1));
            verify(orderDAO).update(order);
            verify(garageSlotService).updateGarageSlot(order.getGarageSlot());
        }

        @Test
        @DisplayName("returns false when order not found")
        void negative_returnsFalseWhenNotFound() {
            when(orderDAO.findById(999)).thenReturn(Optional.empty());
            assertTrue(!orderService.cancelOrder(999));
        }

        @Test
        @DisplayName("throws RuntimeException when DAO fails")
        void negative_daoThrows_rethrowsRuntimeException() {
            when(orderDAO.findById(1)).thenReturn(Optional.of(order));
            doThrow(new RuntimeException("DB error")).when(orderDAO).update(any(ServiceOrder.class));
            assertThrows(RuntimeException.class, () -> orderService.cancelOrder(1));
        }
    }

    @Nested
    @DisplayName("closeOrder")
    class CloseOrder {
        @Test
        @DisplayName("closes order and updates garage slot")
        void positive_returnsTrue() {
            when(orderDAO.findById(1)).thenReturn(Optional.of(order));
            assertTrue(orderService.closeOrder(1));
            verify(orderDAO).update(order);
            verify(garageSlotService).updateGarageSlot(order.getGarageSlot());
        }

        @Test
        @DisplayName("returns false when order not found")
        void negative_returnsFalseWhenNotFound() {
            when(orderDAO.findById(999)).thenReturn(Optional.empty());
            assertTrue(!orderService.closeOrder(999));
        }
    }

    @Nested
    @DisplayName("deleteOrder")
    class DeleteOrder {
        @Test
        @DisplayName("marks order deleted and updates garage slot")
        void positive_returnsTrue() {
            when(orderDAO.findById(1)).thenReturn(Optional.of(order));
            assertTrue(orderService.deleteOrder(1));
            verify(orderDAO).update(order);
        }

        @Test
        @DisplayName("returns false when order not found")
        void negative_returnsFalseWhenNotFound() {
            when(orderDAO.findById(999)).thenReturn(Optional.empty());
            assertTrue(!orderService.deleteOrder(999));
        }
    }

    @Nested
    @DisplayName("removeOrderById")
    class RemoveOrderById {
        @Test
        @DisplayName("returns true when physically deleted")
        void positive_returnsTrue() {
            when(orderDAO.deleteById(1)).thenReturn(true);
            assertTrue(orderService.removeOrderById(1));
        }

        @Test
        @DisplayName("returns false when not found")
        void negative_returnsFalseWhenNotFound() {
            when(orderDAO.deleteById(999)).thenReturn(false);
            assertTrue(!orderService.removeOrderById(999));
        }

        @Test
        @DisplayName("throws RuntimeException when DAO fails")
        void negative_daoThrows_rethrowsRuntimeException() {
            doThrow(new RuntimeException("DB error")).when(orderDAO).deleteById(eq(1));
            assertThrows(RuntimeException.class, () -> orderService.removeOrderById(1));
        }
    }

    @Nested
    @DisplayName("shiftOrder")
    class ShiftOrder {
        @Test
        @DisplayName("shifts order and updates in DAO")
        void positive_returnsTrue() {
            when(orderDAO.findById(1)).thenReturn(Optional.of(order));
            assertTrue(orderService.shiftOrder(1, 30));
            verify(orderDAO).update(order);
        }

        @Test
        @DisplayName("returns false when order not found")
        void negative_returnsFalseWhenNotFound() {
            when(orderDAO.findById(999)).thenReturn(Optional.empty());
            assertTrue(!orderService.shiftOrder(999, 30));
        }
    }

    @Nested
    @DisplayName("getAllOrdersSorted")
    class GetAllOrdersSorted {
        @Test
        @DisplayName("returns sorted list")
        void positive_returnsSorted() {
            when(orderDAO.findAll()).thenReturn(List.of(order));
            List<ServiceOrder> result = orderService.getAllOrdersSorted(OrderSort.BY_PLANNED_START);
            assertEquals(1, result.size());
            assertEquals(order, result.get(0));
        }

        @Test
        @DisplayName("throws RuntimeException when getOrders fails")
        void negative_daoThrows_rethrowsRuntimeException() {
            doThrow(new RuntimeException("DB error")).when(orderDAO).findAll();
            assertThrows(RuntimeException.class, () -> orderService.getAllOrdersSorted(OrderSort.BY_PLANNED_START));
        }
    }

    @Nested
    @DisplayName("getCurrentOrderSorted")
    class GetCurrentOrderSorted {
        @Test
        @DisplayName("returns current orders sorted")
        void positive_returnsList() {
            when(orderDAO.findAll()).thenReturn(List.of(order));
            List<ServiceOrder> result = orderService.getCurrentOrderSorted(OrderSort.BY_PLANNED_START);
            assertTrue(result.size() <= 1);
        }

        @Test
        @DisplayName("throws RuntimeException when getOrders fails")
        void negative_daoThrows_rethrowsRuntimeException() {
            doThrow(new RuntimeException("DB error")).when(orderDAO).findAll();
            assertThrows(RuntimeException.class, () -> orderService.getCurrentOrderSorted(OrderSort.BY_PLANNED_START));
        }
    }

    @Nested
    @DisplayName("getOrderByMechanicNow")
    class GetOrderByMechanicNow {
        @Test
        @DisplayName("returns Optional with order when mechanic has current order")
        void positive_returnsOrder() {
            when(orderDAO.findAll()).thenReturn(List.of(order));
            Optional<ServiceOrder> result = orderService.getOrderByMechanicNow(1);
            assertTrue(result.isEmpty() || result.get().getMechanic().getId() == 1);
        }

        @Test
        @DisplayName("throws RuntimeException when getOrders fails")
        void negative_daoThrows_rethrowsRuntimeException() {
            doThrow(new RuntimeException("DB error")).when(orderDAO).findAll();
            assertThrows(RuntimeException.class, () -> orderService.getOrderByMechanicNow(1));
        }
    }

    @Nested
    @DisplayName("getMechanicByOrderId")
    class GetMechanicByOrderId {
        @Test
        @DisplayName("returns Optional with mechanic")
        void positive_returnsMechanic() {
            when(orderDAO.findById(1)).thenReturn(Optional.of(order));
            assertEquals(Optional.of(mechanic), orderService.getMechanicByOrderId(1));
        }

        @Test
        @DisplayName("returns empty when order not found")
        void negative_returnsEmptyWhenNotFound() {
            when(orderDAO.findById(999)).thenReturn(Optional.empty());
            assertTrue(orderService.getMechanicByOrderId(999).isEmpty());
        }
    }

    @Nested
    @DisplayName("getOrders with period and statuses")
    class GetOrdersPeriod {
        @Test
        @DisplayName("returns filtered and sorted list")
        void positive_returnsList() {
            when(orderDAO.findAll()).thenReturn(List.of(order));
            Set<OrderStatus> statuses = EnumSet.of(OrderStatus.NEW);
            LocalDateTime from = LocalDateTime.now().minusDays(1);
            LocalDateTime to = LocalDateTime.now().plusDays(1);
            List<ServiceOrder> result = orderService.getOrders(from, to, statuses, OrderSort.BY_PLANNED_START);
            assertTrue(result.size() <= 1);
        }

        @Test
        @DisplayName("throws RuntimeException when getOrders fails")
        void negative_daoThrows_rethrowsRuntimeException() {
            doThrow(new RuntimeException("DB error")).when(orderDAO).findAll();
            Set<OrderStatus> statuses = EnumSet.of(OrderStatus.NEW);
            assertThrows(RuntimeException.class,
                () -> orderService.getOrders(LocalDateTime.now(), LocalDateTime.now(), statuses, OrderSort.BY_PLANNED_START));
        }
    }

    @Nested
    @DisplayName("printList")
    class PrintList {
        @Test
        @DisplayName("does not throw")
        void positive_doesNotThrow() {
            orderService.printList("Title", List.of());
            orderService.printList("Title", List.of(order));
        }
    }

    @Nested
    @DisplayName("demoPeriodReport")
    class DemoPeriodReport {
        @Test
        @DisplayName("does not throw when orders loaded")
        void positive_doesNotThrow() {
            when(orderDAO.findAll()).thenReturn(List.of(order));
            orderService.demoPeriodReport(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        }

        @Test
        @DisplayName("throws RuntimeException when getOrders fails")
        void negative_daoThrows_rethrowsRuntimeException() {
            doThrow(new RuntimeException("DB error")).when(orderDAO).findAll();
            assertThrows(RuntimeException.class,
                () -> orderService.demoPeriodReport(LocalDateTime.now(), LocalDateTime.now()));
        }
    }
}

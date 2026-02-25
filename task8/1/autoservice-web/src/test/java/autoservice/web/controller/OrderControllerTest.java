package autoservice.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import autoservice.model.GarageSlot;
import autoservice.model.Mechanic;
import autoservice.model.OrderSort;
import autoservice.model.ServiceOrder;
import autoservice.model.TimeSlot;
import autoservice.service.GarageSlotService;
import autoservice.service.MechanicService;
import autoservice.service.OrderService;
import autoservice.web.GlobalExceptionHandler;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private MechanicService mechanicService;

    @Mock
    private GarageSlotService garageSlotService;

    @InjectMocks
    private OrderController controller;

    private MockMvc mockMvc;
    private Mechanic mechanic;
    private GarageSlot slot;
    private ServiceOrder order;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build();
        mechanic = new Mechanic(1, "Ivan");
        slot = new GarageSlot(1);
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        order = new ServiceOrder.Builder().setId(1).setMechanic(mechanic).setGarageSlot(slot)
                .setTimeSlot(timeSlot).setPrice(1000).build();
    }

    @Test
    void create_returns201() throws Exception {
        when(mechanicService.findMechanicById(1)).thenReturn(Optional.of(mechanic));
        when(garageSlotService.findGarageSlotById(1)).thenReturn(Optional.of(slot));
        when(orderService.getOrders()).thenReturn(List.of());
        String body = "{\"mechanicId\":1,\"garageSlotId\":1,\"timeSlotStart\":\"2025-01-26T10:00:00\","
                + "\"timeSlotEnd\":\"2025-01-26T11:00:00\",\"price\":1000}";
        mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void create_mechanicNotFound_returns400() throws Exception {
        when(mechanicService.findMechanicById(999)).thenReturn(Optional.empty());
        String body = "{\"mechanicId\":999,\"garageSlotId\":1,\"timeSlotStart\":\"2025-01-26T10:00:00\","
                + "\"timeSlotEnd\":\"2025-01-26T11:00:00\",\"price\":1000}";
        mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_returns200() throws Exception {
        when(orderService.findOrderById(1)).thenReturn(Optional.of(order));
        mockMvc.perform(get("/api/orders/1")).andExpect(status().isOk());
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(orderService.findOrderById(999)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/orders/999")).andExpect(status().isNotFound());
    }

    @Test
    void list_returns200() throws Exception {
        when(orderService.getOrders()).thenReturn(List.of(order));
        mockMvc.perform(get("/api/orders")).andExpect(status().isOk());
    }

    @Test
    void cancel_returns200() throws Exception {
        when(orderService.cancelOrder(1)).thenReturn(true);
        when(orderService.findOrderById(1)).thenReturn(Optional.of(order));
        mockMvc.perform(put("/api/orders/1/cancel")).andExpect(status().isOk());
    }

    @Test
    void cancel_notFound_returns404() throws Exception {
        when(orderService.cancelOrder(999)).thenReturn(false);
        mockMvc.perform(put("/api/orders/999/cancel")).andExpect(status().isNotFound());
    }

    @Test
    void close_returns200() throws Exception {
        when(orderService.closeOrder(1)).thenReturn(true);
        when(orderService.findOrderById(1)).thenReturn(Optional.of(order));
        mockMvc.perform(put("/api/orders/1/close")).andExpect(status().isOk());
    }

    @Test
    void close_notFound_returns404() throws Exception {
        when(orderService.closeOrder(999)).thenReturn(false);
        mockMvc.perform(put("/api/orders/999/close")).andExpect(status().isNotFound());
    }

    @Test
    void delete_returns200() throws Exception {
        when(orderService.deleteOrder(1)).thenReturn(true);
        when(orderService.findOrderById(1)).thenReturn(Optional.of(order));
        mockMvc.perform(delete("/api/orders/1")).andExpect(status().isOk());
    }

    @Test
    void delete_notFound_returns404() throws Exception {
        when(orderService.deleteOrder(999)).thenReturn(false);
        mockMvc.perform(delete("/api/orders/999")).andExpect(status().isNotFound());
    }

    @Test
    void shift_returns200() throws Exception {
        when(orderService.shiftOrder(1, 30)).thenReturn(true);
        when(orderService.findOrderById(1)).thenReturn(Optional.of(order));
        mockMvc.perform(put("/api/orders/1/shift").param("minutes", "30")).andExpect(status().isOk());
    }

    @Test
    void shift_notFound_returns404() throws Exception {
        when(orderService.shiftOrder(999, 30)).thenReturn(false);
        mockMvc.perform(put("/api/orders/999/shift").param("minutes", "30")).andExpect(status().isNotFound());
    }

    @Test
    void orderByMechanicNow_returns200() throws Exception {
        when(orderService.getOrderByMechanicNow(1)).thenReturn(Optional.of(order));
        mockMvc.perform(get("/api/orders/mechanic/1/now")).andExpect(status().isOk());
    }

    @Test
    void orderByMechanicNow_notFound_returns404() throws Exception {
        when(orderService.getOrderByMechanicNow(999)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/orders/mechanic/999/now")).andExpect(status().isNotFound());
    }

    @Test
    void period_returns200() throws Exception {
        when(orderService.getOrders(any(), any(), any(), eq(OrderSort.BY_PLANNED_START))).thenReturn(List.of(order));
        mockMvc.perform(get("/api/orders/period").param("from", "2025-01-01T00:00:00").param("to", "2025-01-31T23:59:59"))
                .andExpect(status().isOk());
    }
}

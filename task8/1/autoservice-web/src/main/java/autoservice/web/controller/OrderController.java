package autoservice.web.controller;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import autoservice.model.GarageSlot;
import autoservice.model.Mechanic;
import autoservice.model.OrderSort;
import autoservice.model.ServiceOrder;
import autoservice.model.TimeSlot;
import autoservice.service.GarageSlotService;
import autoservice.service.MechanicService;
import autoservice.service.OrderService;
import autoservice.web.dto.CreateOrderRequest;
import autoservice.web.dto.DtoMapper;
import autoservice.web.dto.ServiceOrderDto;

/**
 * REST API для заказов.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MechanicService mechanicService;

    @Autowired
    private GarageSlotService garageSlotService;

    @PostMapping
    public ResponseEntity<ServiceOrderDto> create(@RequestBody CreateOrderRequest request) {
        Mechanic mechanic = mechanicService.findMechanicById(request.getMechanicId())
            .orElseThrow(() -> new IllegalArgumentException("Механик не найден: " + request.getMechanicId()));
        GarageSlot slot = garageSlotService.findGarageSlotById(request.getGarageSlotId())
            .orElseThrow(() -> new IllegalArgumentException("Гаражное место не найдено: " + request.getGarageSlotId()));
        int orderId = nextOrderId();
        TimeSlot timeSlot = new TimeSlot(request.getTimeSlotStart(), request.getTimeSlotEnd());
        ServiceOrder order = new ServiceOrder.Builder()
            .setId(orderId)
            .setMechanic(mechanic)
            .setGarageSlot(slot)
            .setTimeSlot(timeSlot)
            .setPrice(request.getPrice())
            .build();
        orderService.addOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(DtoMapper.toServiceOrderDto(order));
    }

    private int nextOrderId() {
        List<ServiceOrder> all = orderService.getOrders();
        return all.isEmpty() ? 1 : all.stream().mapToInt(ServiceOrder::getId).max().orElse(0) + 1;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceOrderDto> getById(@PathVariable int id) {
        return orderService.findOrderById(id)
            .map(o -> ResponseEntity.ok(DtoMapper.toServiceOrderDto(o)))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<ServiceOrderDto> list(
        @RequestParam(required = false) String sort
    ) {
        if (sort != null && !sort.isEmpty()) {
            OrderSort s = OrderSort.valueOf(sort.toUpperCase());
            return orderService.getAllOrdersSorted(s).stream()
                .map(DtoMapper::toServiceOrderDto)
                .collect(Collectors.toList());
        }
        return orderService.getOrders().stream()
            .map(DtoMapper::toServiceOrderDto)
            .collect(Collectors.toList());
    }

    @GetMapping("/current")
    public List<ServiceOrderDto> current(
        @RequestParam(required = false, defaultValue = "BY_PLANNED_START") String sort
    ) {
        OrderSort s = OrderSort.valueOf(sort.toUpperCase());
        return orderService.getCurrentOrderSorted(s).stream()
            .map(DtoMapper::toServiceOrderDto)
            .collect(Collectors.toList());
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ServiceOrderDto> cancel(@PathVariable int id) {
        boolean ok = orderService.cancelOrder(id);
        if (!ok) {
            return ResponseEntity.notFound().build();
        }
        return orderService.findOrderById(id)
            .map(o -> ResponseEntity.ok(DtoMapper.toServiceOrderDto(o)))
            .orElse(ResponseEntity.ok().build());
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<ServiceOrderDto> close(@PathVariable int id) {
        boolean ok = orderService.closeOrder(id);
        if (!ok) {
            return ResponseEntity.notFound().build();
        }
        return orderService.findOrderById(id)
            .map(o -> ResponseEntity.ok(DtoMapper.toServiceOrderDto(o)))
            .orElse(ResponseEntity.ok().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceOrderDto> delete(@PathVariable int id) {
        boolean ok = orderService.deleteOrder(id);
        if (!ok) {
            return ResponseEntity.notFound().build();
        }
        return orderService.findOrderById(id)
            .map(o -> ResponseEntity.ok(DtoMapper.toServiceOrderDto(o)))
            .orElse(ResponseEntity.ok().build());
    }

    @PutMapping("/{id}/shift")
    public ResponseEntity<ServiceOrderDto> shift(
        @PathVariable int id,
        @RequestParam int minutes
    ) {
        boolean ok = orderService.shiftOrder(id, minutes);
        if (!ok) {
            return ResponseEntity.notFound().build();
        }
        return orderService.findOrderById(id)
            .map(o -> ResponseEntity.ok(DtoMapper.toServiceOrderDto(o)))
            .orElse(ResponseEntity.ok().build());
    }

    @GetMapping("/mechanic/{mechanicId}/now")
    public ResponseEntity<ServiceOrderDto> orderByMechanicNow(@PathVariable int mechanicId) {
        return orderService.getOrderByMechanicNow(mechanicId)
            .map(o -> ResponseEntity.ok(DtoMapper.toServiceOrderDto(o)))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/period")
    public List<ServiceOrderDto> period(
        @RequestParam String from,
        @RequestParam String to,
        @RequestParam(required = false) String statuses,
        @RequestParam(required = false, defaultValue = "BY_PLANNED_START") String sort
    ) {
        LocalDateTime fromDt = LocalDateTime.parse(from);
        LocalDateTime toDt = LocalDateTime.parse(to);
        Set<autoservice.model.OrderStatus> st = EnumSet.allOf(autoservice.model.OrderStatus.class);
        if (statuses != null && !statuses.isEmpty()) {
            st = java.util.Arrays.stream(statuses.split(","))
                .map(String::trim)
                .map(autoservice.model.OrderStatus::valueOf)
                .collect(Collectors.toSet());
        }
        OrderSort s = OrderSort.valueOf(sort.toUpperCase());
        return orderService.getOrders(fromDt, toDt, st, s).stream()
            .map(DtoMapper::toServiceOrderDto)
            .collect(Collectors.toList());
    }
}

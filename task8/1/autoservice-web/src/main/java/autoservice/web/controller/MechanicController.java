package autoservice.web.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import autoservice.model.Mechanic;
import autoservice.model.MechanicSort;
import autoservice.service.MechanicService;
import autoservice.service.OrderService;
import autoservice.web.dto.CreateMechanicRequest;
import autoservice.web.dto.DtoMapper;
import autoservice.web.dto.MechanicDto;

/**
 * REST API для механиков.
 */
@RestController
@RequestMapping("/api/mechanics")
public class MechanicController {

    @Autowired
    private MechanicService mechanicService;

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<MechanicDto> create(@RequestBody(required = false) CreateMechanicRequest request) {
        int id = nextMechanicId();
        String name = (request != null && request.getName() != null) ? request.getName() : "";
        Mechanic mechanic = new Mechanic(id, name);
        mechanicService.addMechanic(mechanic);
        return ResponseEntity.status(HttpStatus.CREATED).body(DtoMapper.toMechanicDto(mechanic));
    }

    private int nextMechanicId() {
        List<Mechanic> all = mechanicService.getAllMechanics();
        return all.isEmpty() ? 1 : all.stream().mapToInt(Mechanic::getId).max().orElse(0) + 1;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MechanicDto> getById(@PathVariable int id) {
        return mechanicService.findMechanicById(id)
            .map(m -> ResponseEntity.ok(DtoMapper.toMechanicDto(m)))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<MechanicDto> list(
        @RequestParam(required = false) String sort
    ) {
        if (sort != null && !sort.isEmpty()) {
            MechanicSort s = MechanicSort.valueOf(sort.toUpperCase());
            return mechanicService.getMechanicSorted(s).stream()
                .map(DtoMapper::toMechanicDto)
                .collect(Collectors.toList());
        }
        return mechanicService.getAllMechanics().stream()
            .map(DtoMapper::toMechanicDto)
            .collect(Collectors.toList());
    }

    @GetMapping("/free-count")
    public ResponseEntity<Integer> freeCount(@RequestParam(required = false) String at) {
        java.time.LocalDateTime when = at != null && !at.isEmpty()
            ? java.time.LocalDateTime.parse(at)
            : java.time.LocalDateTime.now();
        int count = mechanicService.getFreeMechanicsCount(when);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/by-order/{orderId}")
    public ResponseEntity<MechanicDto> mechanicByOrder(@PathVariable int orderId) {
        return orderService.getMechanicByOrderId(orderId)
            .map(m -> ResponseEntity.ok(DtoMapper.toMechanicDto(m)))
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        boolean removed = mechanicService.removeMechanicById(id);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

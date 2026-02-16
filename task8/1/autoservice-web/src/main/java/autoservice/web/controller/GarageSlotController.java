package autoservice.web.controller;

import java.time.LocalDateTime;
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

import autoservice.model.GarageSlot;
import autoservice.service.GarageSlotService;
import autoservice.web.dto.CreateGarageSlotRequest;
import autoservice.web.dto.DtoMapper;
import autoservice.web.dto.GarageSlotDto;

/**
 * REST API для гаражных мест.
 */
@RestController
@RequestMapping("/api/garage-slots")
public class GarageSlotController {

    @Autowired
    private GarageSlotService garageSlotService;

    @PostMapping
    public ResponseEntity<GarageSlotDto> create(@RequestBody(required = false) CreateGarageSlotRequest request) {
        int id = (request != null && request.getId() != null) ? request.getId() : nextSlotId();
        GarageSlot slot = new GarageSlot(id);
        garageSlotService.addGarageSlot(slot);
        return ResponseEntity.status(HttpStatus.CREATED).body(DtoMapper.toGarageSlotDto(slot));
    }

    private int nextSlotId() {
        List<GarageSlot> all = garageSlotService.getGarageSlots();
        return all.isEmpty() ? 1 : all.stream().mapToInt(GarageSlot::getId).max().orElse(0) + 1;
    }

    @GetMapping("/{id}")
    public ResponseEntity<GarageSlotDto> getById(@PathVariable int id) {
        return garageSlotService.findGarageSlotById(id)
            .map(s -> ResponseEntity.ok(DtoMapper.toGarageSlotDto(s)))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<GarageSlotDto> list() {
        return garageSlotService.getGarageSlots().stream()
            .map(DtoMapper::toGarageSlotDto)
            .collect(Collectors.toList());
    }

    @GetMapping("/free")
    public List<GarageSlotDto> free(
        @RequestParam(required = false) String at
    ) {
        LocalDateTime when = (at != null && !at.isEmpty())
            ? LocalDateTime.parse(at)
            : LocalDateTime.now();
        return garageSlotService.getFreeGarageSlotsAt(when).stream()
            .map(DtoMapper::toGarageSlotDto)
            .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        boolean removed = garageSlotService.removeGarageSlotById(id);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

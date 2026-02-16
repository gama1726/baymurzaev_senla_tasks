package autoservice.web.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import autoservice.service.CapacityService;
import autoservice.web.dto.NextFreeDateResponse;

/**
 * REST API для ёмкости (свободные слоты, следующая свободная дата).
 */
@RestController
@RequestMapping("/api/capacity")
public class CapacityController {

    @Autowired
    private CapacityService capacityService;

    @GetMapping("/free")
    public ResponseEntity<Integer> freeAt(@RequestParam(required = false) String at) {
        LocalDateTime when = (at != null && !at.isEmpty())
            ? LocalDateTime.parse(at)
            : LocalDateTime.now();
        int count = capacityService.freeCapacityAt(when);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/next-free")
    public ResponseEntity<NextFreeDateResponse> nextFree(
        @RequestParam(required = false) String from
    ) {
        LocalDateTime fromDt = (from != null && !from.isEmpty())
            ? LocalDateTime.parse(from)
            : LocalDateTime.now();
        LocalDateTime next = capacityService.findNextFreeDate(fromDt);
        if (next == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new NextFreeDateResponse(next));
    }
}

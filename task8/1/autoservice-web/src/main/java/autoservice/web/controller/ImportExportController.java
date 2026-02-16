package autoservice.web.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import autoservice.service.GarageSlotService;
import autoservice.service.MechanicService;
import autoservice.service.OrderService;
import autoservice.service.importexport.GarageSlotImportExportService;
import autoservice.service.importexport.MechanicImportExportService;
import autoservice.service.importexport.OrderImportExportService;
import autoservice.web.dto.DtoMapper;
import autoservice.web.dto.GarageSlotDto;
import autoservice.web.dto.MechanicDto;
import autoservice.web.dto.ServiceOrderDto;

/**
 * REST API для экспорта данных (списки в JSON/XML).
 * Импорт через файл можно добавить как multipart/form-data.
 */
@RestController
@RequestMapping("/api")
public class ImportExportController {

    @Autowired
    private MechanicService mechanicService;

    @Autowired
    private GarageSlotService garageSlotService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MechanicImportExportService mechanicImportExportService;

    @Autowired
    private GarageSlotImportExportService garageSlotImportExportService;

    @Autowired
    private OrderImportExportService orderImportExportService;

    @GetMapping("/export/mechanics")
    public List<MechanicDto> exportMechanics() {
        return mechanicService.getAllMechanics().stream()
            .map(DtoMapper::toMechanicDto)
            .collect(Collectors.toList());
    }

    @GetMapping("/export/garage-slots")
    public List<GarageSlotDto> exportGarageSlots() {
        return garageSlotService.getGarageSlots().stream()
            .map(DtoMapper::toGarageSlotDto)
            .collect(Collectors.toList());
    }

    @GetMapping("/export/orders")
    public List<ServiceOrderDto> exportOrders() {
        return orderService.getOrders().stream()
            .map(DtoMapper::toServiceOrderDto)
            .collect(Collectors.toList());
    }

    /**
     * Экспорт механиков в CSV по указанному пути (для совместимости с функциональностью).
     * В production лучше отдавать файл через StreamingResponseBody.
     */
    @GetMapping("/export/mechanics/csv")
    public ResponseEntity<Void> exportMechanicsCsv(@RequestParam String path) {
        try {
            mechanicImportExportService.exportToCsv(path);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/export/garage-slots/csv")
    public ResponseEntity<Void> exportGarageSlotsCsv(@RequestParam String path) {
        try {
            garageSlotImportExportService.exportToCsv(path);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/export/orders/csv")
    public ResponseEntity<Void> exportOrdersCsv(@RequestParam String path) {
        try {
            orderImportExportService.exportToCsv(path);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

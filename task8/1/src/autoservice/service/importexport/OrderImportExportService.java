package autoservice.service.importexport;

import autoservice.annotation.Component;
import autoservice.annotation.Inject;
import autoservice.exception.EntityNotFoundException;
import autoservice.exception.ImportExportException;
import autoservice.model.*;
import autoservice.service.GarageSlotService;
import autoservice.service.MechanicService;
import autoservice.service.OrderService;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для импорта и экспорта заказов в формате CSV.
 * Автоматически устанавливает связи между объектами (механик, гаражное место).
 */
@Component
public class OrderImportExportService {
    private static final String CSV_HEADER = "id,mechanicId,garageSlotId,timeSlotStart,timeSlotEnd,price,status,submittedAt,finishedAt";
    private static final String CSV_SEPARATOR = ",";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Inject
    private OrderService orderService;
    
    @Inject
    private MechanicService mechanicService;
    
    @Inject
    private GarageSlotService garageSlotService;

    /**
     * Экспортирует все заказы в CSV файл.
     * @param filePath путь к файлу для экспорта
     * @throws ImportExportException если произошла ошибка при записи
     */
    public void exportToCsv(String filePath) throws ImportExportException {
        try {
            Path path = Paths.get(filePath);
            List<ServiceOrder> orders = orderService.getOrders();

            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                // Записываем заголовок
                writer.write(CSV_HEADER);
                writer.newLine();

                // Записываем данные
                for (ServiceOrder order : orders) {
                    StringBuilder line = new StringBuilder();
                    line.append(order.getId()).append(CSV_SEPARATOR);
                    line.append(order.getMechanic().getId()).append(CSV_SEPARATOR);
                    line.append(order.getGarageSlot().getId()).append(CSV_SEPARATOR);
                    line.append(order.getTimeSlot().getStart().format(DATE_TIME_FORMATTER)).append(CSV_SEPARATOR);
                    line.append(order.getTimeSlot().getEnd().format(DATE_TIME_FORMATTER)).append(CSV_SEPARATOR);
                    line.append(order.getPrice()).append(CSV_SEPARATOR);
                    line.append(order.getStatus().name()).append(CSV_SEPARATOR);
                    line.append(order.getSubmittedAt() != null ? order.getSubmittedAt().format(DATE_TIME_FORMATTER) : "").append(CSV_SEPARATOR);
                    line.append(order.getFinishedAt() != null ? order.getFinishedAt().format(DATE_TIME_FORMATTER) : "");
                    
                    writer.write(line.toString());
                    writer.newLine();
                }
            }

            System.out.println("Экспортировано " + orders.size() + " заказов в файл: " + filePath);
        } catch (IOException e) {
            throw new ImportExportException("Ошибка при экспорте заказов в файл: " + filePath, e);
        }
    }

    /**
     * Импортирует заказы из CSV файла.
     * Обновляет существующие записи с совпавшим ID и добавляет новые.
     * Автоматически устанавливает связи между объектами (механик, гаражное место).
     * @param filePath путь к файлу для импорта
     * @throws ImportExportException если произошла ошибка при чтении или валидации
     */
    public void importFromCsv(String filePath) throws ImportExportException {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                throw new ImportExportException("Файл не найден: " + filePath);
            }

            List<ServiceOrder> importedOrders = new ArrayList<>();
            int lineNumber = 0;
            List<String> errors = new ArrayList<>();

            try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                String line;
                boolean isFirstLine = true;

                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    line = line.trim();

                    // Пропускаем пустые строки
                    if (line.isEmpty()) {
                        continue;
                    }

                    // Пропускаем заголовок
                    if (isFirstLine) {
                        if (line.equals(CSV_HEADER)) {
                            isFirstLine = false;
                            continue;
                        }
                        // Если первый строка не заголовок, значит это данные
                        isFirstLine = false;
                    }

                    try {
                        ServiceOrder order = parseOrder(line, lineNumber);
                        importedOrders.add(order);
                    } catch (Exception e) {
                        String errorMsg = "Строка " + lineNumber + ": " + e.getMessage();
                        errors.add(errorMsg);
                        System.err.println(errorMsg);
                    }
                }
            }

            if (!errors.isEmpty()) {
                System.out.println("Обнаружено " + errors.size() + " ошибок при импорте заказов.");
            }

            // Обновляем или добавляем заказы
            int updated = 0;
            int added = 0;

            for (ServiceOrder order : importedOrders) {
                Optional<ServiceOrder> existing = orderService.findOrderById(order.getId());
                if (existing.isPresent()) {
                    // Удаляем существующий заказ и добавляем обновленный
                    orderService.removeOrderById(order.getId());
                    orderService.addOrder(order);
                    updated++;
                } else {
                    // Добавляем новый заказ
                    orderService.addOrder(order);
                    added++;
                }
            }

            System.out.println("Импорт завершен. Обновлено: " + updated + ", добавлено: " + added);
        } catch (IOException e) {
            throw new ImportExportException("Ошибка при импорте заказов из файла: " + filePath, e);
        }
    }

    /**
     * Парсит строку CSV в объект ServiceOrder с автоматическим связыванием объектов.
     */
    private ServiceOrder parseOrder(String line, int lineNumber) throws ImportExportException, EntityNotFoundException {
        String[] parts = line.split(CSV_SEPARATOR, -1);
        if (parts.length < 7) {
            throw new ImportExportException("Неверный формат строки. Ожидается минимум: id,mechanicId,garageSlotId,timeSlotStart,timeSlotEnd,price,status");
        }

        try {
            // Парсим ID
            int id = Integer.parseInt(parts[0].trim());
            
            // Парсим и находим механика
            int mechanicId = Integer.parseInt(parts[1].trim());
            Mechanic mechanic = mechanicService.findMechanicById(mechanicId)
                    .orElseThrow(() -> new EntityNotFoundException("Механик с ID " + mechanicId + " не найден"));
            
            // Парсим и находим гаражное место
            int garageSlotId = Integer.parseInt(parts[2].trim());
            GarageSlot garageSlot = garageSlotService.findGarageSlotById(garageSlotId)
                    .orElseThrow(() -> new EntityNotFoundException("Гаражное место с ID " + garageSlotId + " не найдено"));
            
            // Парсим временной слот
            LocalDateTime timeSlotStart = parseDateTime(parts[3].trim(), "timeSlotStart");
            LocalDateTime timeSlotEnd = parseDateTime(parts[4].trim(), "timeSlotEnd");
            if (timeSlotStart.isAfter(timeSlotEnd)) {
                throw new ImportExportException("Время начала не может быть позже времени окончания");
            }
            TimeSlot timeSlot = new TimeSlot(timeSlotStart, timeSlotEnd);
            
            // Парсим цену
            int price = Integer.parseInt(parts[5].trim());
            if (price < 0) {
                throw new ImportExportException("Цена не может быть отрицательной");
            }
            
            // Парсим статус (опционально, по умолчанию NEW)
            OrderStatus status = OrderStatus.NEW;
            if (!parts[6].trim().isEmpty()) {
                try {
                    status = OrderStatus.valueOf(parts[6].trim().toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new ImportExportException("Неверный статус: " + parts[6].trim());
                }
            }
            
            // Создаем заказ через Builder
            ServiceOrder.Builder builder = new ServiceOrder.Builder()
                    .setId(id)
                    .setMechanic(mechanic)
                    .setGarageSlot(garageSlot)
                    .setTimeSlot(timeSlot)
                    .setPrice(price);
            
            ServiceOrder order = builder.build();
            
            // Устанавливаем статус и даты из CSV
            LocalDateTime submittedAt = null;
            LocalDateTime finishedAt = null;
            
            if (!parts[7].trim().isEmpty()) {
                submittedAt = parseDateTime(parts[7].trim(), "submittedAt");
            }
            if (parts.length > 8 && !parts[8].trim().isEmpty()) {
                finishedAt = parseDateTime(parts[8].trim(), "finishedAt");
            }
            
            order.setStatusAndDates(status, submittedAt, finishedAt);
            
            return order;
        } catch (NumberFormatException e) {
            throw new ImportExportException("Неверный числовой формат в строке: " + e.getMessage());
        }
    }

    /**
     * Парсит строку даты/времени.
     */
    private LocalDateTime parseDateTime(String dateTimeStr, String fieldName) throws ImportExportException {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            throw new ImportExportException("Поле " + fieldName + " не может быть пустым");
        }
        try {
            return LocalDateTime.parse(dateTimeStr.trim(), DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ImportExportException("Неверный формат даты/времени в поле " + fieldName + ": " + dateTimeStr);
        }
    }
}


package autoservice.service.importexport;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import autoservice.exception.ImportExportException;
import autoservice.model.GarageSlot;
import autoservice.service.GarageSlotService;

/**
 * Сервис для импорта и экспорта гаражных мест в формате CSV.
 */
@Service
@Transactional
public class GarageSlotImportExportService {
    private static final String CSV_HEADER = "id";
    private static final String CSV_SEPARATOR = ",";

    @Autowired
    private GarageSlotService garageSlotService;

    /**
     * Экспортирует все гаражные места в CSV файл.
     * @param filePath путь к файлу для экспорта
     * @throws ImportExportException если произошла ошибка при записи
     */
    public void exportToCsv(String filePath) throws ImportExportException {
        try {
            Path path = Paths.get(filePath);
            List<GarageSlot> slots = garageSlotService.getGarageSlots();

            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                // Записываем заголовок
                writer.write(CSV_HEADER);
                writer.newLine();

                // Записываем данные
                for (GarageSlot slot : slots) {
                    writer.write(String.valueOf(slot.getId()));
                    writer.newLine();
                }
            }

            System.out.println("Экспортировано " + slots.size() + " гаражных мест в файл: " + filePath);
        } catch (IOException e) {
            throw new ImportExportException("Ошибка при экспорте гаражных мест в файл: " + filePath, e);
        }
    }

    /**
     * Импортирует гаражные места из CSV файла.
     * Обновляет существующие записи с совпавшим ID и добавляет новые.
     * @param filePath путь к файлу для импорта
     * @throws ImportExportException если произошла ошибка при чтении или валидации
     */
    public void importFromCsv(String filePath) throws ImportExportException {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                throw new ImportExportException("Файл не найден: " + filePath);
            }

            List<GarageSlot> importedSlots = new ArrayList<>();
            int lineNumber = 0;

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
                        GarageSlot slot = parseGarageSlot(line, lineNumber);
                        importedSlots.add(slot);
                    } catch (Exception e) {
                        System.err.println("Пропущена строка " + lineNumber + ": " + e.getMessage());
                    }
                }
            }

            // Обновляем или добавляем гаражные места
            int updated = 0;
            int added = 0;

            for (GarageSlot slot : importedSlots) {
                Optional<GarageSlot> existing = garageSlotService.findGarageSlotById(slot.getId());
                if (existing.isPresent()) {
                    // Обновляем существующее место (удаляем и добавляем заново)
                    garageSlotService.removeGarageSlotById(slot.getId());
                    garageSlotService.addGarageSlot(new GarageSlot(slot.getId()));
                    updated++;
                } else {
                    // Добавляем новое место
                    garageSlotService.addGarageSlot(slot);
                    added++;
                }
            }

            System.out.println("Импорт завершен. Обновлено: " + updated + ", добавлено: " + added);
        } catch (IOException e) {
            throw new ImportExportException("Ошибка при импорте гаражных мест из файла: " + filePath, e);
        }
    }

    /**
     * Парсит строку CSV в объект GarageSlot.
     */
    private GarageSlot parseGarageSlot(String line, int lineNumber) throws ImportExportException {
        String[] parts = line.split(CSV_SEPARATOR, -1);
        if (parts.length < 1) {
            throw new ImportExportException("Неверный формат строки. Ожидается: id");
        }

        try {
            int id = Integer.parseInt(parts[0].trim());
            return new GarageSlot(id);
        } catch (NumberFormatException e) {
            throw new ImportExportException("Неверный формат ID: " + parts[0]);
        }
    }
}


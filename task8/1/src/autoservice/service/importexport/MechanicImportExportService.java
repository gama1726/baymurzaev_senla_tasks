package autoservice.service.importexport;

import autoservice.annotation.Component;
import autoservice.annotation.Inject;
import autoservice.exception.ImportExportException;
import autoservice.model.Mechanic;
import autoservice.service.MechanicService;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для импорта и экспорта механиков в формате CSV.
 */
@Component
public class MechanicImportExportService {
    private static final String CSV_HEADER = "id,name";
    private static final String CSV_SEPARATOR = ",";

    @Inject
    private MechanicService mechanicService;

    /**
     * Экспортирует всех механиков в CSV файл.
     * @param filePath путь к файлу для экспорта
     * @throws ImportExportException если произошла ошибка при записи
     */
    public void exportToCsv(String filePath) throws ImportExportException {
        try {
            Path path = Paths.get(filePath);
            List<Mechanic> mechanics = mechanicService.getAllMechanics();

            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                // Записываем заголовок
                writer.write(CSV_HEADER);
                writer.newLine();

                // Записываем данные
                for (Mechanic mechanic : mechanics) {
                    writer.write(mechanic.getId() + CSV_SEPARATOR + escapeCsv(mechanic.getName()));
                    writer.newLine();
                }
            }

            System.out.println("Экспортировано " + mechanics.size() + " механиков в файл: " + filePath);
        } catch (IOException e) {
            throw new ImportExportException("Ошибка при экспорте механиков в файл: " + filePath, e);
        }
    }

    /**
     * Импортирует механиков из CSV файла.
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

            List<Mechanic> importedMechanics = new ArrayList<>();
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
                        Mechanic mechanic = parseMechanic(line, lineNumber);
                        importedMechanics.add(mechanic);
                    } catch (Exception e) {
                        System.err.println("Пропущена строка " + lineNumber + ": " + e.getMessage());
                    }
                }
            }

            // Обновляем или добавляем механиков
            int updated = 0;
            int added = 0;

            for (Mechanic mechanic : importedMechanics) {
                Optional<Mechanic> existing = mechanicService.findMechanicById(mechanic.getId());
                if (existing.isPresent()) {
                    // Обновляем существующего механика
                    mechanicService.removeMechanicById(mechanic.getId());
                    mechanicService.addMechanic(mechanic);
                    updated++;
                } else {
                    // Добавляем нового механика
                    mechanicService.addMechanic(mechanic);
                    added++;
                }
            }

            System.out.println("Импорт завершен. Обновлено: " + updated + ", добавлено: " + added);
        } catch (IOException e) {
            throw new ImportExportException("Ошибка при импорте механиков из файла: " + filePath, e);
        }
    }

    /**
     * Парсит строку CSV в объект Mechanic.
     */
    private Mechanic parseMechanic(String line, int lineNumber) throws ImportExportException {
        String[] parts = line.split(CSV_SEPARATOR, -1);
        if (parts.length != 2) {
            throw new ImportExportException("Неверный формат строки. Ожидается: id,name");
        }

        try {
            int id = Integer.parseInt(parts[0].trim());
            String name = unescapeCsv(parts[1].trim());

            if (name.isEmpty()) {
                throw new ImportExportException("Имя механика не может быть пустым");
            }

            return new Mechanic(id, name);
        } catch (NumberFormatException e) {
            throw new ImportExportException("Неверный формат ID: " + parts[0]);
        }
    }

    /**
     * Экранирует специальные символы для CSV.
     */
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        // Если значение содержит запятую или кавычки, заключаем в кавычки
        if (value.contains(CSV_SEPARATOR) || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /**
     * Убирает экранирование из CSV значения.
     */
    private String unescapeCsv(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        // Если значение заключено в кавычки, убираем их и обрабатываем двойные кавычки
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1).replace("\"\"", "\"");
        }
        return value;
    }
}


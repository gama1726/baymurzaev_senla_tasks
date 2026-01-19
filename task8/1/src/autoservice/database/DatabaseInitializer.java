package autoservice.database;

import autoservice.annotation.Component;
import autoservice.annotation.Inject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Инициализатор базы данных.
 * Создает структуру БД при первом запуске приложения.
 */
@Component
public class DatabaseInitializer {
    
    private static final String CREATE_SCHEMA_FILE = "/autoservice/database/create_schema.sql";
    private static final String INSERT_TEST_DATA_FILE = "/autoservice/database/insert_test_data.sql";
    private static final String INITIALIZATION_ERROR = "Ошибка при инициализации БД";
    
    @Inject
    private ConnectionManager connectionManager;
    
    /**
     * Инициализирует базу данных: создает структуру и заполняет тестовыми данными.
     */
    public void initialize() {
        try {
            Connection conn = connectionManager.getConnection();
            
            // Проверяем, существует ли уже структура
            if (isSchemaExists(conn)) {
                System.out.println("Структура БД уже существует, пропускаем создание");
                return;
            }
            
            // Создаем структуру
            System.out.println("Создание структуры БД...");
            executeScript(conn, CREATE_SCHEMA_FILE);
            System.out.println("Структура БД создана");
            
            // Заполняем тестовыми данными
            System.out.println("Заполнение БД тестовыми данными...");
            executeScript(conn, INSERT_TEST_DATA_FILE);
            System.out.println("Тестовые данные добавлены");
        } catch (Exception e) {
            throw new RuntimeException(INITIALIZATION_ERROR + ": " + e.getMessage(), e);
        }
    }
    
    /**
     * Проверяет, существует ли уже структура БД.
     */
    private boolean isSchemaExists(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeQuery("SELECT 1 FROM mechanics LIMIT 1");
            return true;
        } catch (SQLException e) {
            // Таблица не существует
            return false;
        }
    }
    
    /**
     * Выполняет SQL скрипт из файловой системы или ресурсов.
     */
    private void executeScript(Connection conn, String resourcePath) throws Exception {
        InputStream is = null;
        
        // Пытаемся загрузить из файловой системы (относительно src/)
        try {
            java.io.File file = new java.io.File("src" + resourcePath);
            if (file.exists()) {
                is = new java.io.FileInputStream(file);
            }
        } catch (Exception e) {
            // Игнорируем, пробуем ресурсы
        }
        
        // Пытаемся загрузить из ресурсов
        if (is == null) {
            is = getClass().getResourceAsStream(resourcePath);
        }
        if (is == null) {
            is = getClass().getClassLoader().getResourceAsStream(resourcePath);
        }
        
        if (is == null) {
            throw new RuntimeException("Не удалось найти SQL скрипт: " + resourcePath);
        }
        
        String script = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));
        
        // Разделяем скрипт на отдельные команды
        // Используем более умное разделение, учитывая многострочные команды
        String[] commands = script.split(";");
        
        try (Statement stmt = conn.createStatement()) {
            for (String command : commands) {
                command = command.trim();
                // Убираем комментарии из команды
                command = removeComments(command);
                
                if (!command.isEmpty()) {
                    try {
                        // Выполняем команду
                        boolean hasResult = stmt.execute(command);
                        // Если есть результат (например, SELECT), обрабатываем его
                        if (hasResult) {
                            try (var rs = stmt.getResultSet()) {
                                // Просто закрываем ResultSet
                            }
                        }
                    } catch (SQLException e) {
                        // Игнорируем ошибки типа "таблица уже существует" или "индекс уже существует"
                        String errorMsg = e.getMessage().toLowerCase();
                        if (!errorMsg.contains("already exists") && 
                            !errorMsg.contains("уже существует") &&
                            !errorMsg.contains("duplicate key name") &&
                            !errorMsg.contains("duplicate index name")) {
                            // Если это не ошибка дублирования, пробрасываем дальше
                            throw e;
                        }
                    }
                }
            }
            // Коммитим все изменения после выполнения скрипта
            conn.commit();
        }
    }
    
    /**
     * Удаляет однострочные комментарии из SQL команды.
     */
    private String removeComments(String command) {
        // Удаляем комментарии вида -- комментарий
        String[] lines = command.split("\n");
        StringBuilder result = new StringBuilder();
        for (String line : lines) {
            int commentIndex = line.indexOf("--");
            if (commentIndex >= 0) {
                line = line.substring(0, commentIndex);
            }
            result.append(line).append("\n");
        }
        return result.toString().trim();
    }
}

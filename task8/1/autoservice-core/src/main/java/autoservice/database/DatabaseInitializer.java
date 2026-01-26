package autoservice.database;

import autoservice.annotation.Component;
import autoservice.annotation.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    
    private static final Logger logger = LogManager.getLogger(DatabaseInitializer.class);
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
            
            logger.info("Начало инициализации базы данных");
            
            // Проверяем, существует ли уже структура
            if (isSchemaExists(conn)) {
                logger.info("Структура БД уже существует, пропускаем создание");
                System.out.println("Структура БД уже существует, пропускаем создание");
                return;
            }
            
            // Создаем структуру
            logger.info("Создание структуры БД...");
            System.out.println("Создание структуры БД...");
            executeScript(conn, CREATE_SCHEMA_FILE);
            logger.info("Структура БД создана");
            System.out.println("Структура БД создана");
            
            // Заполняем тестовыми данными
            logger.info("Заполнение БД тестовыми данными...");
            System.out.println("Заполнение БД тестовыми данными...");
            executeScript(conn, INSERT_TEST_DATA_FILE);
            logger.info("Тестовые данные добавлены");
            System.out.println("Тестовые данные добавлены");
        } catch (Exception e) {
            logger.error("Ошибка при инициализации БД", e);
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
        
        // Пытаемся загрузить из ресурсов (приоритет)
        is = getClass().getClassLoader().getResourceAsStream(resourcePath);
        
        // Если не найден, пробуем через getResourceAsStream класса
        if (is == null) {
            is = getClass().getResourceAsStream(resourcePath);
        }
        
        // Если не найден в ресурсах, пробуем файловую систему (для разработки)
        if (is == null) {
            try {
                // Получаем путь к корню проекта
                String userDir = System.getProperty("user.dir");
                logger.debug("Текущая рабочая директория: {}", userDir);
                
                // Пробуем разные пути относительно корня проекта
                String[] paths = {
                    userDir + "/autoservice-core/src/main/resources" + resourcePath,
                    userDir + "/src/main/resources" + resourcePath,
                    userDir + "/../autoservice-core/src/main/resources" + resourcePath,
                    "autoservice-core/src/main/resources" + resourcePath,
                    "src/main/resources" + resourcePath
                };
                
                for (String path : paths) {
                    java.io.File file = new java.io.File(path);
                    logger.debug("Проверяю путь: {} - существует: {}", path, file.exists());
                    if (file.exists()) {
                        logger.info("Найден SQL скрипт в файловой системе: {}", path);
                        is = new java.io.FileInputStream(file);
                        break;
                    }
                }
            } catch (Exception e) {
                logger.debug("Не удалось загрузить из файловой системы: {}", resourcePath, e);
            }
        }
        
        if (is == null) {
            logger.error("Не удалось найти SQL скрипт: {}", resourcePath);
            logger.error("Проверьте, что файлы находятся в: autoservice-core/src/main/resources{}", resourcePath);
            throw new RuntimeException("Не удалось найти SQL скрипт: " + resourcePath);
        }
        
        logger.debug("Загружен SQL скрипт: {}", resourcePath);
        
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

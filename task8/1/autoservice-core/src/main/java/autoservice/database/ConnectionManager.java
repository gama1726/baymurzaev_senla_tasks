package autoservice.database;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Менеджер соединений с базой данных (Singleton).
 * Управляет единственным экземпляром соединения с БД.
 */
@Component
public class ConnectionManager {

    private static final String DRIVER_CLASS_NOT_FOUND = "Драйвер БД не найден";
    private static final String CONNECTION_ERROR = "Ошибка при установке соединения с БД";
    private static final String CLOSE_ERROR = "Ошибка при закрытии соединения";

    @Value("${db.url:jdbc:h2:./autoservice_db;DB_CLOSE_ON_EXIT=FALSE;DATABASE_TO_UPPER=FALSE;AUTO_SERVER=TRUE}")
    private String dbUrl;

    @Value("${db.username:sa}")
    private String dbUsername;

    @Value("${db.password:}")
    private String dbPassword;

    @Value("${db.driver:org.h2.Driver}")
    private String dbDriver;
    
    private Connection connection;
    private boolean initialized = false;
    
    /**
     * Инициализирует драйвер БД и создает соединение.
     */
    public void initialize() {
        if (initialized) {
            return;
        }
        
        try {
            // Загрузка драйвера
            Class.forName(dbDriver);
            
            // Создание соединения
            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            connection.setAutoCommit(false); // Отключаем автокоммит для поддержки транзакций
            
            initialized = true;
            System.out.println("Соединение с БД установлено: " + dbUrl);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(DRIVER_CLASS_NOT_FOUND + ": " + dbDriver, e);
        } catch (SQLException e) {
            throw new RuntimeException(CONNECTION_ERROR + ": " + e.getMessage(), e);
        }
    }
    
    /**
     * Получает соединение с БД.
     * Если соединение закрыто, создает новое.
     * 
     * @return соединение с БД
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                initialize();
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(CONNECTION_ERROR + ": " + e.getMessage(), e);
        }
    }
    
    /**
     * Закрывает соединение с БД.
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("Соединение с БД закрыто");
                }
            } catch (SQLException e) {
                System.err.println(CLOSE_ERROR + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Проверяет, инициализировано ли соединение.
     */
    public boolean isInitialized() {
        return initialized;
    }
}

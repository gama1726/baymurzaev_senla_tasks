package autoservice.database;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.stereotype.Component;
import jakarta.persistence.Persistence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Менеджер для создания и управления EntityManagerFactory.
 * Реализует паттерн Singleton через аннотацию @Component.
 */
@Component
public class JpaEntityManagerFactory {
    private static final Logger logger = LogManager.getLogger(JpaEntityManagerFactory.class);
    private static final String PERSISTENCE_UNIT_NAME = "autoservice-pu";
    
    private EntityManagerFactory entityManagerFactory;
    
    /**
     * Инициализирует EntityManagerFactory.
     */
    public void initialize() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            try {
                // Загружаем свойства из config.properties
                Map<String, String> properties = loadDatabaseProperties();
                
                entityManagerFactory = Persistence.createEntityManagerFactory(
                    PERSISTENCE_UNIT_NAME, 
                    properties
                );
                logger.info("EntityManagerFactory initialized successfully");
            } catch (Exception e) {
                logger.error("Failed to initialize EntityManagerFactory", e);
                throw new RuntimeException("Failed to initialize EntityManagerFactory", e);
            }
        }
    }
    
    /**
     * Загружает свойства БД из config.properties.
     */
    private Map<String, String> loadDatabaseProperties() {
        Map<String, String> props = new HashMap<>();
        
        try {
            // Пытаемся загрузить из ресурсов
            InputStream is = getClass().getClassLoader()
                .getResourceAsStream("config.properties");
            
            if (is == null) {
                // Пробуем загрузить из autoservice-core/src/main/resources
                is = getClass().getClassLoader()
                    .getResourceAsStream("autoservice/config.properties");
            }
            
            if (is == null) {
                // Используем значения по умолчанию
                logger.warn("Config file not found, using default values");
                props.put("jakarta.persistence.jdbc.driver", "org.h2.Driver");
                props.put("jakarta.persistence.jdbc.url", 
                    "jdbc:h2:./autoservice_db;DB_CLOSE_ON_EXIT=FALSE;DATABASE_TO_UPPER=FALSE;AUTO_SERVER=TRUE");
                props.put("jakarta.persistence.jdbc.user", "sa");
                props.put("jakarta.persistence.jdbc.password", "");
            } else {
                Properties configProps = new Properties();
                configProps.load(is);
                is.close();
                
                // Загружаем свойства БД
                String driver = configProps.getProperty("db.driver", "org.h2.Driver");
                String url = configProps.getProperty("db.url", 
                    "jdbc:h2:./autoservice_db;DB_CLOSE_ON_EXIT=FALSE;DATABASE_TO_UPPER=FALSE;AUTO_SERVER=TRUE");
                String username = configProps.getProperty("db.username", "sa");
                String password = configProps.getProperty("db.password", "");
                
                // Убираем экранирование из URL (Properties автоматически экранирует : и =)
                if (url != null) {
                    url = url.replace("\\:", ":");
                    url = url.replace("\\=", "=");
                }
                
                props.put("jakarta.persistence.jdbc.driver", driver);
                props.put("jakarta.persistence.jdbc.url", url);
                props.put("jakarta.persistence.jdbc.user", username);
                props.put("jakarta.persistence.jdbc.password", password != null ? password : "");
                
                logger.info("Loaded DB properties: driver={}, url={}, user={}", driver, url, username);
            }
        } catch (Exception e) {
            logger.warn("Error loading config.properties, using defaults", e);
            props.put("jakarta.persistence.jdbc.driver", "org.h2.Driver");
            props.put("jakarta.persistence.jdbc.url", 
                "jdbc:h2:./autoservice_db;DB_CLOSE_ON_EXIT=FALSE;DATABASE_TO_UPPER=FALSE;AUTO_SERVER=TRUE");
            props.put("jakarta.persistence.jdbc.user", "sa");
            props.put("jakarta.persistence.jdbc.password", "");
        }
        
        // Добавляем Hibernate свойства
        props.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        props.put("hibernate.hbm2ddl.auto", "update");
        props.put("hibernate.show_sql", "false");
        props.put("hibernate.format_sql", "true");
        props.put("hibernate.use_sql_comments", "true");
        props.put("hibernate.connection.pool_size", "5");
        
        return props;
    }
    
    /**
     * Получает EntityManagerFactory, инициализируя его при необходимости.
     * 
     * @return EntityManagerFactory
     */
    public EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            initialize();
        }
        return entityManagerFactory;
    }
    
    /**
     * Закрывает EntityManagerFactory.
     */
    public void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            logger.info("EntityManagerFactory closed");
        }
    }
}

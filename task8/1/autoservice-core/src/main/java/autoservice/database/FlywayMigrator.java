package autoservice.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.Flyway;

import java.io.InputStream;
import java.util.Properties;

/**
 * Запуск миграций Flyway при старте приложения.
 * Настройки берутся из config.properties (те же, что и для JPA).
 */
public final class FlywayMigrator {

    private static final Logger logger = LogManager.getLogger(FlywayMigrator.class);
    private static final String LOCATIONS = "classpath:db/migration";

    private FlywayMigrator() {
    }

    /**
     * Выполняет все неприменённые миграции.
     * Вызывать до инициализации JPA.
     */
    public static void run() {
        try {
            Properties config = loadConfig();
            String url = config.getProperty("db.url");
            String user = config.getProperty("db.username");
            String password = config.getProperty("db.password");
            if (url != null) {
                url = url.replace("\\:", ":").replace("\\=", "=");
            }
            if (url == null || url.isEmpty()) {
                url = "jdbc:h2:./autoservice_db;DB_CLOSE_ON_EXIT=FALSE;DATABASE_TO_UPPER=FALSE;AUTO_SERVER=TRUE";
            }
            if (user == null) {
                user = "sa";
            }
            if (password == null) {
                password = "";
            }

            Flyway flyway = Flyway.configure()
                .dataSource(url, user, password)
                .locations(LOCATIONS)
                .load();
            flyway.migrate();
            logger.info("Flyway: миграции выполнены успешно");
        } catch (Exception e) {
            logger.error("Flyway: ошибка при выполнении миграций", e);
            throw new RuntimeException("Ошибка Flyway: " + e.getMessage(), e);
        }
    }

    private static Properties loadConfig() {
        Properties p = new Properties();
        try (InputStream is = FlywayMigrator.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                p.load(is);
            }
        } catch (Exception e) {
            logger.warn("Не удалось загрузить config.properties, используем значения по умолчанию", e);
        }
        if (!p.containsKey("db.url")) {
            p.setProperty("db.url", "jdbc:h2:./autoservice_db;DB_CLOSE_ON_EXIT=FALSE;DATABASE_TO_UPPER=FALSE;AUTO_SERVER=TRUE");
        }
        if (!p.containsKey("db.username")) {
            p.setProperty("db.username", "sa");
        }
        if (!p.containsKey("db.password")) {
            p.setProperty("db.password", "");
        }
        return p;
    }
}

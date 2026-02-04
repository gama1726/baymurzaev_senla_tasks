package autoservice;

import autoservice.config.AppConfig;
import autoservice.dao.*;
import autoservice.database.ConnectionManager;
import autoservice.database.DatabaseInitializer;
import autoservice.database.FlywayMigrator;
import autoservice.database.JpaEntityManagerFactory;
import autoservice.service.*;
import autoservice.service.importexport.*;
import autoservice.ui.menu.factory.AbstractMenuFactory;
import autoservice.ui.menu.factory.DefaultMenuFactory;
import autoservice.ui.menu.Menu;
import autoservice.ui.menu.MenuBuilder;
import autoservice.ui.menu.MenuController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Точка входа в приложение автосервиса.
 *
 * Использует Spring (spring-context) для Dependency Injection:
 *  - конфигурация через AppConfig (Java config);
 *  - репозитории (DAO) и сервисы — синглтоны;
 *  - параметры из config.properties внедряются через @Value (PropertySourcesPlaceholderConfigurer).
 */
public class App {
    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

        logger.info("Запуск приложения автосервиса");

        // Flyway: миграции БД до инициализации JPA
        FlywayMigrator.run();

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {

            // Инициализация JPA
            JpaEntityManagerFactory jpaEntityManagerFactory = context.getBean(JpaEntityManagerFactory.class);
            jpaEntityManagerFactory.initialize();

            // Инициализация ConnectionManager и структуры БД
            ConnectionManager connectionManager = context.getBean(ConnectionManager.class);
            connectionManager.initialize();

            DatabaseInitializer dbInitializer = context.getBean(DatabaseInitializer.class);
            dbInitializer.initialize();

            // ServiceManager и все зависимости уже созданы Spring (синглтоны)
            ServiceManager manager = context.getBean(ServiceManager.class);

            // Abstract Factory для пунктов меню (передаем ApplicationContext для autowire действий)
            AbstractMenuFactory abstractFactory = new DefaultMenuFactory(manager, context);

            MenuBuilder builder = new MenuBuilder(abstractFactory);
            Menu rootMenu = builder.buildRootMenu();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Завершение работы приложения (shutdown hook)");
                connectionManager.closeConnection();
                jpaEntityManagerFactory.close();
            }));

            MenuController controller = new MenuController(rootMenu);
            controller.run();

            logger.info("Завершение работы приложения");
            connectionManager.closeConnection();
            jpaEntityManagerFactory.close();
        } catch (Exception e) {
            logger.error("Критическая ошибка при запуске приложения", e);
            System.err.println("Ошибка при запуске приложения: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

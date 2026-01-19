package autoservice;

import autoservice.config.Configuration;
import autoservice.dao.*;
import autoservice.database.ConnectionManager;
import autoservice.database.DatabaseInitializer;
import autoservice.injection.ConfigInjector;
import autoservice.injection.DIContainer;
import autoservice.model.GarageSlot;
import autoservice.model.Mechanic;
import autoservice.service.*;
import autoservice.service.importexport.*;
import autoservice.ui.menu.factory.AbstractMenuFactory;
import autoservice.ui.menu.factory.DefaultMenuFactory;
import autoservice.ui.menu.Menu;
import autoservice.ui.menu.MenuBuilder;
import autoservice.ui.menu.MenuController;

/**
 * Точка входа в приложение автосервиса.
 *
 * Здесь:
 *  - инициализируется DI контейнер;
 *  - загружается конфигурация через аннотации;
 *  - регистрируются все компоненты;
 *  - получается ServiceManager через DI;
 *  - добавляются тестовые данные (механики, гаражи);
 *  - создаётся Abstract Factory для меню (MenuFactory);
 *  - через MenuBuilder строится корневое меню;
 *  - запускается MenuController (консольный UI по MVC).
 */
public class App {
    public static void main(String[] args) {
        try {
            // Инициализация DI контейнера
            DIContainer container = DIContainer.getInstance();
            
            // Создание и загрузка конфигурации
            Configuration configuration = new Configuration();
            ConfigInjector.injectConfig(configuration);
            // Регистрируем конфигурацию в контейнере для внедрения в Actions
            container.registerInstance(Configuration.class, configuration);
            
            // Регистрация ConnectionManager и инициализация БД
            container.registerComponent(ConnectionManager.class);
            ConnectionManager connectionManager = container.getInstance(ConnectionManager.class);
            // Заполняем конфигурацию через аннотации
            ConfigInjector.injectConfig(connectionManager);
            // Инициализируем соединение
            connectionManager.initialize();
            
            // Инициализация структуры БД
            container.registerComponent(DatabaseInitializer.class);
            DatabaseInitializer dbInitializer = container.getInstance(DatabaseInitializer.class);
            dbInitializer.initialize();
            
            // Регистрация DAO
            container.registerComponent(MechanicDAO.class);
            container.registerComponent(GarageSlotDAO.class);
            container.registerComponent(ServiceOrderDAO.class);
            
            // Регистрация сервисов
            container.registerComponent(OrderService.class);
            container.registerComponent(MechanicService.class);
            container.registerComponent(GarageSlotService.class);
            container.registerComponent(CapacityService.class);
            container.registerComponent(MechanicImportExportService.class);
            container.registerComponent(GarageSlotImportExportService.class);
            container.registerComponent(OrderImportExportService.class);
            container.registerComponent(ServiceManager.class);
            
            // Получение ServiceManager через DI
            ServiceManager manager = container.getInstance(ServiceManager.class);

            // Тестовые данные уже добавлены через insert_test_data.sql
            // Если нужно добавить дополнительные данные, можно раскомментировать:
            // manager.addMechanic(new Mechanic(1, "Андрей"));
            // manager.addGarageSlot(new GarageSlot(101));

            // Abstract Factory для пунктов меню
            AbstractMenuFactory abstractFactory = new DefaultMenuFactory(manager, container);

            // Builder, который с помощью фабрики собирает структуру меню
            MenuBuilder builder = new MenuBuilder(abstractFactory);
            Menu rootMenu = builder.buildRootMenu();

            // Контроллер UI (MVC) + главный цикл консольного меню
            MenuController controller = new MenuController(rootMenu);
            controller.run();
            
            // Закрываем соединение с БД при завершении
            connectionManager.closeConnection();
        } catch (Exception e) {
            System.err.println("Ошибка при запуске приложения: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


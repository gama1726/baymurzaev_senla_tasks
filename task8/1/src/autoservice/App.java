package autoservice;

import autoservice.config.Configuration;
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
            
            // Регистрация всех компонентов
            container.registerComponent(OrderService.class);
            container.registerComponent(MechanicService.class);
            container.registerComponent(GarageSlotService.class);
            container.registerComponent(CapacityService.class);
            container.registerComponent(MechanicImportExportService.class);
            container.registerComponent(GarageSlotImportExportService.class);
            container.registerComponent(OrderImportExportService.class);
            container.registerComponent(ServiceManager.class);
            
            // Создание и загрузка конфигурации
            Configuration configuration = new Configuration();
            ConfigInjector.injectConfig(configuration);
            // Регистрируем конфигурацию в контейнере для внедрения в Actions
            container.registerInstance(Configuration.class, configuration);
            
            // Получение ServiceManager через DI
            ServiceManager manager = container.getInstance(ServiceManager.class);

            // Немного стартовых данных, чтобы не вводить всё каждый раз
            manager.addMechanic(new Mechanic(1, "Андрей"));
            manager.addMechanic(new Mechanic(2, "Руслан"));
            manager.addMechanic(new Mechanic(3, "Магомед"));

            manager.addGarageSlot(new GarageSlot(101));
            manager.addGarageSlot(new GarageSlot(102));
            manager.addGarageSlot(new GarageSlot(103));

            // Abstract Factory для пунктов меню
            AbstractMenuFactory abstractFactory = new DefaultMenuFactory(manager, container);

            // Builder, который с помощью фабрики собирает структуру меню
            MenuBuilder builder = new MenuBuilder(abstractFactory);
            Menu rootMenu = builder.buildRootMenu();

            // Контроллер UI (MVC) + главный цикл консольного меню
            MenuController controller = new MenuController(rootMenu);
            controller.run();
        } catch (Exception e) {
            System.err.println("Ошибка при запуске приложения: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


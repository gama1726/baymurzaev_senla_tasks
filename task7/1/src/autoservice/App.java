package autoservice;

import autoservice.config.ConfigurationManager;
import autoservice.model.GarageSlot;
import autoservice.model.Mechanic;
import autoservice.persistence.ApplicationState;
import autoservice.persistence.StateManager;
import autoservice.service.ServiceManager;
import autoservice.ui.menu.factory.AbstractMenuFactory;
import autoservice.ui.menu.factory.DefaultMenuFactory;
import autoservice.ui.menu.Menu;
import autoservice.ui.menu.MenuBuilder;
import autoservice.ui.menu.MenuController;

/**
 * Точка входа в приложение автосервиса.
 *
 * Здесь:
 *  - загружается конфигурация из property-файла;
 *  - загружается сохраненное состояние приложения (если есть);
 *  - инициализируется Singleton ServiceManager;
 *  - добавляются тестовые данные (если состояние не загружено);
 *  - создаётся Abstract Factory для меню (MenuFactory);
 *  - через MenuBuilder строится корневое меню;
 *  - запускается MenuController (консольный UI по MVC);
 *  - при завершении сохраняется состояние приложения.
 */
public class App {
    private static ServiceManager manager;
    private static StateManager stateManager;
    
    public static void main(String[] args) {
        // Загружаем конфигурацию
        ConfigurationManager configManager = ConfigurationManager.getInstance();
        
        // Инициализируем менеджер состояния
        stateManager = new StateManager();
        
        // Загружаем сохраненное состояние
        ApplicationState savedState = stateManager.loadState();
        
        // сервис (Singleton)
        manager = ServiceManager.getInstance();
        
        // Восстанавливаем состояние или добавляем тестовые данные
        if (savedState != null) {
            manager.restoreApplicationState(savedState);
        } else {
            // Немного стартовых данных, если состояние не загружено
            manager.addMechanic(new Mechanic(1, "Андрей"));
            manager.addMechanic(new Mechanic(2, "Руслан"));
            manager.addMechanic(new Mechanic(3, "Магомед"));

            manager.addGarageSlot(new GarageSlot(101));
            manager.addGarageSlot(new GarageSlot(102));
            manager.addGarageSlot(new GarageSlot(103));
        }
        
        // Регистрируем shutdown hook для сохранения состояния при завершении
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nСохранение состояния приложения...");
            ApplicationState currentState = manager.getApplicationState();
            stateManager.saveState(currentState);
        }));

        // Abstract Factory для пунктов меню
        AbstractMenuFactory abstractFactory = new DefaultMenuFactory(manager);

        // Builder, который с помощью фабрики собирает структуру меню
        MenuBuilder builder = new MenuBuilder(abstractFactory);
        Menu rootMenu = builder.buildRootMenu();

        // Контроллер UI (MVC) + главный цикл консольного меню
        MenuController controller = new MenuController(rootMenu);
        controller.run();
        
        // Сохраняем состояние при нормальном завершении
        System.out.println("\nСохранение состояния приложения...");
        ApplicationState currentState = manager.getApplicationState();
        stateManager.saveState(currentState);
    }
}


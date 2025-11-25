package autoservice;

import autoservice.model.GarageSlot;
import autoservice.model.Mechanic;
import autoservice.service.ServiceManager;
import autoservice.ui.menu.factory.AbstractMenuFactory;
import autoservice.ui.menu.factory.DefaultMenuFactory;
import autoservice.ui.menu.Menu;
import autoservice.ui.menu.MenuBuilder;
import autoservice.ui.menu.MenuController;
import autoservice.ui.menu.factory.MenuFactory;

/**
 * Точка входа в приложение автосервиса.
 *
 * Здесь:
 *  - инициализируется Singleton ServiceManager;
 *  - добавляются тестовые данные (механики, гаражи);
 *  - создаётся Abstract Factory для меню (MenuFactory);
 *  - через MenuBuilder строится корневое меню;
 *  - запускается MenuController (консольный UI по MVC).
 */
public class App {
    public static void main(String[] args) {

        // сервис (Singleton)
        ServiceManager manager = ServiceManager.getInstance();

        // Немного стартовых данных, чтобы не вводить всё каждый раз
        manager.addMechanic(new Mechanic(1, "Андрей"));
        manager.addMechanic(new Mechanic(2, "Руслан"));
        manager.addMechanic(new Mechanic(3, "Магомед"));

        manager.addGarageSlot(new GarageSlot(101));
        manager.addGarageSlot(new GarageSlot(102));
        manager.addGarageSlot(new GarageSlot(103));

        // Abstract Factory для пунктов меню
        AbstractMenuFactory abstractFactory = new DefaultMenuFactory(manager);

        // Builder, который с помощью фабрики собирает структуру меню
        MenuBuilder builder = new MenuBuilder(abstractFactory);
        Menu rootMenu = builder.buildRootMenu();

        // Контроллер UI (MVC) + главный цикл консольного меню
        MenuController controller = new MenuController(rootMenu);
        controller.run();
    }
}


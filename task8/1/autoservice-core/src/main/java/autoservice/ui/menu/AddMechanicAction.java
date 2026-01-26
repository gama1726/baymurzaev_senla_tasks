package autoservice.ui.menu;

import autoservice.model.Mechanic;
import autoservice.service.ServiceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

/**
 * Добавить механика.
 */
public class AddMechanicAction implements IAction{
    private static final Logger logger = LogManager.getLogger(AddMechanicAction.class);
    private final ServiceManager serviceManager;
    public AddMechanicAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }
    @Override
    public void execute() {
        logger.info("Начало выполнения команды: добавление механика");
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("ID механика: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Имя механика: ");
            String name = scanner.nextLine().trim();

            serviceManager.addMechanic(new Mechanic(id, name));
            System.out.println("Механик добавлен");
            logger.info("Механик успешно добавлен: ID={}, Имя={}", id, name);
        }
        catch (Exception e) {
            logger.error("Ошибка при добавлении механика", e);
            System.out.println("Ошибка при добавлении механика: " + e.getMessage());
        }
    }
}

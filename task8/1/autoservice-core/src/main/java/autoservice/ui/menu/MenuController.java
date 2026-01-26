package autoservice.ui.menu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

/**
 * Главный контроллер UI: цикл работы консольного приложения.
 */
public class MenuController {
    private static final Logger logger = LogManager.getLogger(MenuController.class);
    private final Navigator navigator;
    private final Scanner scanner = new Scanner(System.in);
    public MenuController(Menu rootMenu) {
        if (rootMenu == null) {
            throw new IllegalArgumentException("rootMenu не может быть null");
        }
        this.navigator = new Navigator(rootMenu);
    }
    public void run(){
        boolean running = true;
        while(running){
            navigator.printMenu();
            System.out.print("Выберите пункт (0 - Выход): ");

            String input = scanner.nextLine().trim();
            if("0".equals(input)){
                running = false;
                continue;
            }

            try {
                int index = Integer.parseInt(input);
                logger.info("Пользователь выбрал пункт меню: {}", index);
                navigator.navigate(index);
                logger.info("Команда выполнена успешно");
            }
            catch (NumberFormatException e){
                logger.error("Ошибка: нужно ввести номер пункта меню", e);
                System.out.println("Ошибка: нужно ввести номер пункта меню.");
            }
            catch (IllegalArgumentException e){
                logger.error("Ошибка при выполнении команды: {}", e.getMessage(), e);
                System.out.println("Ошибка: " + e.getMessage());
            }
            catch (Exception e){
                logger.error("Неожиданная ошибка при выполнении команды", e);
                System.out.println("Неожиданная ошибка: " + e.getMessage());
                if (e.getCause() != null) {
                    System.out.println("Причина: " + e.getCause().getMessage());
                }
            }
        }
        System.out.println("Программа завершена.");
    }
}

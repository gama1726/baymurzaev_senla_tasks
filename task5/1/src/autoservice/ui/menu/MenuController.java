package autoservice.ui.menu;

import java.util.Scanner;

/**
 * Главный контроллер UI: цикл работы консольного приложения.
 */
public class MenuController {
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
                navigator.navigate(index);
            }
            catch (NumberFormatException e){
                System.out.println("Нужно ввести номер пункта.");
            }
        }
        System.out.println("Программа завершена.");
    }
}

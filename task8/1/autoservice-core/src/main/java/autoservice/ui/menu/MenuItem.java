package autoservice.ui.menu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Пункт меню: текст, действие и переход в другое меню.
 */
public class MenuItem {
    private static final Logger logger = LogManager.getLogger(MenuItem.class);
    private final String title;
    private final IAction action;
    private Menu nextMenu;
    public MenuItem(String title, IAction action, Menu nextMenu) {
        this.title = title;
        this.action = action;
        this.nextMenu = nextMenu;
    }
    public String getTitle() {
        return title;
    }
    public Menu getNextMenu() {
        return nextMenu;
    }

    public void doAction(Navigator navigator) {
        logger.info("Начало выполнения команды: {}", title);
        try {
            if (action != null) {
                action.execute();
            }
            if (nextMenu != null) {
                navigator.setCurrentMenu(nextMenu);
            }
            logger.info("Команда '{}' выполнена успешно", title);
        } catch (Exception e) {
            logger.error("Ошибка при выполнении команды '{}'", title, e);
            throw e;
        }
    }

}

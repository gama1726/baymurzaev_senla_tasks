package autoservice.ui.menu;

/**
 * Пункт меню: текст, действие и переход в другое меню.
 */
public class MenuItem {
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
        System.out.println("DEBUG: выполняем пункт меню '" + title + "'");
        if (action != null) {
            action.execute();
        }
        if (nextMenu  != null) {
            navigator.setCurrentMenu(nextMenu);
        }

    }

}

package autoservice.ui.menu;
import javax.naming.NameAlreadyBoundException;
import java.util.List;

/**
 * Навигатор по меню: знает текущее меню,
 * умеет его печатать и выполнять выбранный пункт.
 */
public class Navigator {
    private Menu currentMenu;

    public Navigator(Menu rootMenu) {
        if (rootMenu == null) {
            throw new IllegalArgumentException("rootMenu не может быть null");
        }
        this.currentMenu = rootMenu;
    }
    public Menu getCurrentMenu() {
        return currentMenu;
    }
    public void setCurrentMenu(Menu menu) {
        if (menu == null) {
            throw new IllegalArgumentException("currentMenu не может быть null");
        }
        this.currentMenu = currentMenu;
    }
    public void printMenu() {
        System.out.println("\n=== " + currentMenu.getName() + " ===");
        List<MenuItem> items = currentMenu.getItems();
        for(int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i).getTitle());
        }
        System.out.println("0. Выход");
    }

    public void navigate(int index){
        List<MenuItem> items = currentMenu.getItems();
        int realIndex = index - 1;
        if (realIndex < 0 || realIndex >= items.size()) {
            System.out.println("Нет пункта меню с таким номером.");
            return;
        }
        MenuItem item = items.get(realIndex);
        item.doAction(this);
    }
}

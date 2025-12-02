package autoservice.ui.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Экран меню: заголовок + список пунктов.
 */
public class Menu {
    private final String name;
    private final List<MenuItem> items = new ArrayList<>();

    public Menu(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void addItem(MenuItem item) {
        items.add(item);
    }
    public List<MenuItem> getItems() {
        return Collections.unmodifiableList(items);
    }

}

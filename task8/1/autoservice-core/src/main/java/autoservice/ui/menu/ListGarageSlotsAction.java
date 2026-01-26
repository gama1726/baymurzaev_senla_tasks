package autoservice.ui.menu;

import autoservice.model.GarageSlot;
import autoservice.service.ServiceManager;

import java.util.List;

/**
 * Показать все гаражные места.
 */

public class ListGarageSlotsAction implements IAction {
    private final ServiceManager serviceManager;
    public ListGarageSlotsAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public void execute(){
        List<GarageSlot> slots = serviceManager.getGarageSlots();
        System.out.println("\n=== Гаражные места ===");
        if (slots.isEmpty()) {
            System.out.println("(нет мест)");
        }else{
            slots.forEach(System.out::println);
        }
    }
}

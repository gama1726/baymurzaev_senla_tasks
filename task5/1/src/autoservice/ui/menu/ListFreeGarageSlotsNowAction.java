package autoservice.ui.menu;

import autoservice.model.GarageSlot;
import autoservice.service.ServiceManager;

import java.util.List;

/**
 * Показать свободные места на текущий момент.
 */
public class ListFreeGarageSlotsNowAction implements IAction {
    private ServiceManager serviceManager;
    public  ListFreeGarageSlotsNowAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public void execute() {
        List<GarageSlot> free = serviceManager.getFreeGarageSlotsNow();
        System.out.println("\n=== Свободные места (сейчас) ===");
        if (free.isEmpty()) {
            System.out.println("(Нет свободных мест)");
        }else{
            free.forEach(System.out::println);
        }

    }
}

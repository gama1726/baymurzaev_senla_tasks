package autoservice.ui.menu;

import autoservice.model.Mechanic;
import autoservice.model.MechanicSort;
import autoservice.service.ServiceManager;

import java.util.List;
import java.util.Scanner;

/**
 * Показать список механиков (с выбором сортировки).
 */
public class ListMechanicsAction implements IAction {
    private ServiceManager serviceManager;
    public ListMechanicsAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }
    @Override
    public void execute(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nСортировка механиков:");
        System.out.println("1.По имени");
        System.out.println("2. По занятости(текущей)");
        System.out.print("Выберите: ");
        String choice = scanner.nextLine().trim();

        MechanicSort sort = "2".equals(choice)
                ? MechanicSort.BY_WORKLOAD
                : MechanicSort.BY_NAME;
        List<Mechanic> mechanics = serviceManager.getMechanicSorted(sort);
        System.out.println("\n=== Список механиков ===:");
        if(mechanics.isEmpty()){
            System.out.println("(нет механиков)");
        }
        else{
            mechanics.forEach(System.out::println);
        }

    }
}

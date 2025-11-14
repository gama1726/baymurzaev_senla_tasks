package autoservice.ui.menu;

import autoservice.model.*;
import autoservice.service.ServiceManager;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Scanner;

/**
 * Действие: запросить данные и создать новый заказ.
 */
public class CreatedOrderAction implements IAction{
    private final ServiceManager serviceManager;
    public CreatedOrderAction(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    private LocalDateTime realDateTimeOrNow(Scanner scanner, String prompt){
        System.out.print(prompt + "(Формат yyyy-MM-ddTHH:mm, Enter = сейчас): ");
        String s = scanner.nextLine().trim();
        //Пустая строка - берем текущее время
        if(s.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();

            //Округлим до ближайших 15 минут
            int minute = now.getMinute();
            int rounded = (minute / 15) * 15;
            return now.withMinute(rounded).withSecond(0).withNano(0);
        }
        try {
                return LocalDateTime.parse(s);
        } catch(Exception e){
                throw new IllegalArgumentException("Неверный формат даты/времени: " + s);
        }
    }

    @Override
    public void execute(){
        Scanner scanner = new Scanner(System.in);

        try{
            System.out.print("ID заказа: ");
            int orderId = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("ID механика: ");
            int mechId = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("ID гаражного места: ");
            int slotId = Integer.parseInt(scanner.nextLine().trim());

            LocalDateTime start = realDateTimeOrNow(scanner,"Начало (yyyy-MM-ddTHH:mm)");
            LocalDateTime end = realDateTimeOrNow(scanner,"Конец (yyyy-MM-ddTHH:mm)");

            System.out.print("Цена: ");
            int price = Integer.parseInt(scanner.nextLine().trim());

            Optional<Mechanic> optMechanic = serviceManager.findMechanicById(mechId);
            Optional<GarageSlot> optSlot = serviceManager.findGarageSlotById(slotId);

            if (optMechanic.isEmpty() || optSlot.isEmpty()) {
                System.out.println("Механик или гаражное место не найдены.");
                return;
            }

            ServiceOrder order = new ServiceOrder.Builder()
                    .setId(orderId)
                    .setMechanic(optMechanic.get())
                    .setGarageSlot(optSlot.get())
                    .setTimeSlot(new TimeSlot(start, end))
                    .setPrice(price)
                    .build();

            serviceManager.addOrder(order);
            System.out.println("Заказ создан: " + order);

        } catch(Exception e){
            System.out.println("Ошибка  при создании заказа" + e.getMessage());
        }

    }
}
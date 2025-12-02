package autoservice.service;

import autoservice.annotation.Component;
import autoservice.annotation.Inject;

import java.time.LocalDateTime;

/**
 * Сервис для расчета свободных слотов и поиска свободных дат.
 */
@Component
public class CapacityService {
    @Inject
    private MechanicService mechanicService;
    
    @Inject
    private GarageSlotService garageSlotService;
    
    @Inject
    private OrderService orderService;

    /**
     * Получить количество свободных мест на момент времени (минимум между механиками и местами).
     */
    public int freeCapacityAt(LocalDateTime when) {
        int freeMechanics = mechanicService.getFreeMechanicsCount(when);
        int freeGarages = garageSlotService.getFreeGarageSlotsCount(when);
        return Math.min(freeMechanics, freeGarages);
    }

    /**
     * Найти ближайшую свободную дату (шаг 15 минут, до 30 дней).
     */
    public LocalDateTime findNextFreeDate(LocalDateTime from) {
        LocalDateTime t = from;
        for (int i = 0; i < 96 * 30; i++) { // Перебираем 96 * 30 = 2880 шагов, по 15 минут каждый.
            // Это примерно 30 суток (96 интервалов по 15 минут в сутках).
            if (freeCapacityAt(t) > 0) {
                return t;
            }
            t = t.plusMinutes(15);
        }
        return null;
    }
}



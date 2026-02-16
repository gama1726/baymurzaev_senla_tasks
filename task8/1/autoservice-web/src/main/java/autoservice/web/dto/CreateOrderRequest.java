package autoservice.web.dto;

import java.time.LocalDateTime;

/**
 * Запрос на создание заказа.
 */
public class CreateOrderRequest {
    private int mechanicId;
    private int garageSlotId;
    private LocalDateTime timeSlotStart;
    private LocalDateTime timeSlotEnd;
    private int price;

    public CreateOrderRequest() {
    }

    public int getMechanicId() {
        return mechanicId;
    }

    public void setMechanicId(int mechanicId) {
        this.mechanicId = mechanicId;
    }

    public int getGarageSlotId() {
        return garageSlotId;
    }

    public void setGarageSlotId(int garageSlotId) {
        this.garageSlotId = garageSlotId;
    }

    public LocalDateTime getTimeSlotStart() {
        return timeSlotStart;
    }

    public void setTimeSlotStart(LocalDateTime timeSlotStart) {
        this.timeSlotStart = timeSlotStart;
    }

    public LocalDateTime getTimeSlotEnd() {
        return timeSlotEnd;
    }

    public void setTimeSlotEnd(LocalDateTime timeSlotEnd) {
        this.timeSlotEnd = timeSlotEnd;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}

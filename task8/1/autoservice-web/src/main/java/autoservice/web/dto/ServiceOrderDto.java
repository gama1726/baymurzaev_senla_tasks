package autoservice.web.dto;

import java.time.LocalDateTime;

/**
 * DTO заказа для ответов API.
 */
public class ServiceOrderDto {
    private int id;
    private MechanicDto mechanic;
    private GarageSlotDto garageSlot;
    private TimeSlotDto timeSlot;
    private int price;
    private String status;
    private LocalDateTime submittedAt;
    private LocalDateTime finishedAt;

    public ServiceOrderDto() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MechanicDto getMechanic() {
        return mechanic;
    }

    public void setMechanic(MechanicDto mechanic) {
        this.mechanic = mechanic;
    }

    public GarageSlotDto getGarageSlot() {
        return garageSlot;
    }

    public void setGarageSlot(GarageSlotDto garageSlot) {
        this.garageSlot = garageSlot;
    }

    public TimeSlotDto getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(TimeSlotDto timeSlot) {
        this.timeSlot = timeSlot;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }
}

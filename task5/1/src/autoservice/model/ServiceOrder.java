package autoservice.model;

import java.time.LocalDateTime;
/**
 * Заказ-наряд автосервиса.
 */
public class ServiceOrder {
    private int id;
    public Mechanic mechanic;
    public GarageSlot garageSlot;
    public TimeSlot timeSlot;
    OrderStatus status;

    private int price;
    private LocalDateTime submittedAt;
    private LocalDateTime finishedAt;

    public ServiceOrder(int id,Mechanic mechanic,GarageSlot garageSlot,TimeSlot timeSlot,int price){
        this.id = id;
        this.mechanic = mechanic;
        this.garageSlot = garageSlot;
        this.timeSlot = timeSlot;
        this.price = price;
        this.status = OrderStatus.NEW;
        this.submittedAt = LocalDateTime.now();
        garageSlot.occupy();
    }
    public GarageSlot getGarageSlot(){
        return garageSlot;
    }

    public TimeSlot getTimeSlot(){
        return timeSlot;
    }
    public OrderStatus getStatus(){
        return status;
    }

    public int getId() {
        return id;
    }
    public Mechanic getMechanic(){
        return mechanic;
    }

    public int getPrice(){
        return price;
    }
    public LocalDateTime getSubmittedAt(){
        return submittedAt;
    }
    public LocalDateTime getFinishedAt(){
        return finishedAt;
    }

    /** Пометить заказ как удалённый. */
    public void markDeleted(){
        if (status == OrderStatus.NEW){
            this.garageSlot.unOccupy();
            this.status = OrderStatus.DELETED;
        }
    }
    /** Отменить заказ. */
    public void cancel(){
        this.status = OrderStatus.CANCELED;
        garageSlot.unOccupy();
    };
    /** Закрыть заказ (выполнен). */
    public void close(){
        this.status = OrderStatus.CLOSED;
        this.finishedAt = LocalDateTime.now();
        garageSlot.unOccupy();
    };

    /** Сместить временной слот заказа. */
    public void shift(int minutes){
        this.timeSlot.shiftBy(minutes);
    };

    @Override
    public String toString(){
       return "Заказ-наряд # " + id + "|Механик: " + mechanic.getName() 
       +"| Место: " + garageSlot.getId()
       +"| Время: " + timeSlot
       +"| Цена: " + price
       +"| Статус: " + status;
    }
}


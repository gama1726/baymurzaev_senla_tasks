package autoservice.model;

import java.time.LocalDateTime;
/**
 * Заказ-наряд автосервиса.
 */
public class ServiceOrder {
    private int id;
    private Mechanic mechanic;
    private GarageSlot garageSlot;
    private TimeSlot timeSlot;
    private OrderStatus status;

    private int price;
    private LocalDateTime submittedAt;
    private LocalDateTime finishedAt;

    ServiceOrder(int id, Mechanic mechanic, GarageSlot garageSlot, TimeSlot timeSlot, int price){
        this.id = id;
        this.mechanic = mechanic;
        this.garageSlot = garageSlot;
        this.timeSlot = timeSlot;
        this.price = price;
        this.status = OrderStatus.NEW;
        this.submittedAt = LocalDateTime.now();
        garageSlot.occupy();
    }

    /**
     * Builder для создания объектов ServiceOrder.
     */
    public static class Builder {
        private int id;
        private Mechanic mechanic;
        private GarageSlot garageSlot;
        private TimeSlot timeSlot;
        private int price;

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setMechanic(Mechanic mechanic) {
            this.mechanic = mechanic;
            return this;
        }

        public Builder setGarageSlot(GarageSlot garageSlot) {
            this.garageSlot = garageSlot;
            return this;
        }

        public Builder setTimeSlot(TimeSlot timeSlot) {
            this.timeSlot = timeSlot;
            return this;
        }

        public Builder setPrice(int price) {
            this.price = price;
            return this;
        }

        public ServiceOrder build() {
            if (mechanic == null || garageSlot == null || timeSlot == null) {
                throw new IllegalStateException("Mechanic, GarageSlot и TimeSlot обязательны для создания заказа");
            }
            return new ServiceOrder(id, mechanic, garageSlot, timeSlot, price);
        }
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
    }
    /** Закрыть заказ (выполнен). */
    public void close(){
        this.status = OrderStatus.CLOSED;
        this.finishedAt = LocalDateTime.now();
        garageSlot.unOccupy();
    }

    /** Сместить временной слот заказа. */
    public void shift(int minutes){
        this.timeSlot.shiftBy(minutes);
    }

    @Override
    public String toString(){
       return "Заказ-наряд # " + id + "|Механик: " + mechanic.getName() 
       +"| Место: " + garageSlot.getId()
       +"| Время: " + timeSlot
       +"| Цена: " + price
       +"| Статус: " + status;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceOrder that = (ServiceOrder) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}


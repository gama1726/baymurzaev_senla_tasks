public class ServiceOrder {
    private int id;
    public Mechanic mechanic;
    public GarageSlot garageSlot;
    public TimeSlot timeSlot;
    OrderStatus status;

    public ServiceOrder(int id,Mechanic mechanic,GarageSlot garageSlot,TimeSlot timeSlot){
        this.id = id;
        this.mechanic = mechanic;
        this.garageSlot = garageSlot;
        this.timeSlot = timeSlot;
        this.status = OrderStatus.NEW;
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
    
    public void cancel(){
        this.status = OrderStatus.CANCELED;
        garageSlot.unOccupy();
    };
    public void close(){
        this.status = OrderStatus.CLOSED;
        garageSlot.unOccupy();
    };
    public void shift(int minutes){
        this.timeSlot.shiftBy(minutes);
    };

    @Override
    public String toString(){
       return "Заказ-наряд # " + id + "|Механик: " + mechanic.getName() 
       +"| Место: " + garageSlot.getId()
       +"| Время: " + timeSlot
       +"| Статус: " + status;
    }
}


package autoservice.mapper;

import autoservice.entity.*;
import autoservice.model.*;

/**
 * Утилитный класс для преобразования между Entity и Domain моделями.
 */
public class EntityMapper {
    
    /**
     * Преобразует MechanicEntity в Mechanic.
     */
    public static Mechanic toMechanic(MechanicEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Mechanic(entity.getId(), entity.getName());
    }
    
    /**
     * Преобразует Mechanic в MechanicEntity.
     */
    public static MechanicEntity toMechanicEntity(Mechanic mechanic) {
        if (mechanic == null) {
            return null;
        }
        return new MechanicEntity(mechanic.getId(), mechanic.getName());
    }
    
    /**
     * Преобразует GarageSlotEntity в GarageSlot.
     */
    public static GarageSlot toGarageSlot(GarageSlotEntity entity) {
        if (entity == null) {
            return null;
        }
        GarageSlot slot = new GarageSlot(entity.getId());
        if (entity.getOccupied()) {
            slot.occupy();
        }
        return slot;
    }
    
    /**
     * Преобразует GarageSlot в GarageSlotEntity.
     */
    public static GarageSlotEntity toGarageSlotEntity(GarageSlot slot) {
        if (slot == null) {
            return null;
        }
        GarageSlotEntity entity = new GarageSlotEntity(slot.getId());
        entity.setOccupied(slot.getStatus());
        return entity;
    }
    
    /**
     * Преобразует OrderStatusEnum в OrderStatus.
     */
    public static OrderStatus toOrderStatus(OrderStatusEnum statusEnum) {
        if (statusEnum == null) {
            return null;
        }
        return OrderStatus.valueOf(statusEnum.name());
    }
    
    /**
     * Преобразует OrderStatus в OrderStatusEnum.
     */
    public static OrderStatusEnum toOrderStatusEnum(OrderStatus status) {
        if (status == null) {
            return null;
        }
        return OrderStatusEnum.valueOf(status.name());
    }
    
    /**
     * Преобразует ServiceOrderEntity в ServiceOrder.
     */
    public static ServiceOrder toServiceOrder(ServiceOrderEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Mechanic mechanic = toMechanic(entity.getMechanic());
        GarageSlot garageSlot = toGarageSlot(entity.getGarageSlot());
        TimeSlot timeSlot = new TimeSlot(entity.getTimeSlotStart(), entity.getTimeSlotEnd());
        OrderStatus status = toOrderStatus(entity.getStatus());
        
        ServiceOrder order = new ServiceOrder.Builder()
            .setId(entity.getId())
            .setMechanic(mechanic)
            .setGarageSlot(garageSlot)
            .setTimeSlot(timeSlot)
            .setPrice(entity.getPrice())
            .build();
        
        order.setStatusAndDates(status, entity.getSubmittedAt(), entity.getFinishedAt());
        return order;
    }
    
    /**
     * Преобразует ServiceOrder в ServiceOrderEntity.
     */
    public static ServiceOrderEntity toServiceOrderEntity(ServiceOrder order) {
        if (order == null) {
            return null;
        }
        
        ServiceOrderEntity entity = new ServiceOrderEntity();
        entity.setId(order.getId());
        entity.setMechanic(toMechanicEntity(order.getMechanic()));
        entity.setGarageSlot(toGarageSlotEntity(order.getGarageSlot()));
        entity.setTimeSlotStart(order.getTimeSlot().getStart());
        entity.setTimeSlotEnd(order.getTimeSlot().getEnd());
        entity.setPrice(order.getPrice());
        entity.setStatus(toOrderStatusEnum(order.getStatus()));
        entity.setSubmittedAt(order.getSubmittedAt());
        entity.setFinishedAt(order.getFinishedAt());
        
        return entity;
    }
}

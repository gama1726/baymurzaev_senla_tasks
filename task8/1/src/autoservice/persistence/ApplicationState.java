package autoservice.persistence;

import autoservice.model.GarageSlot;
import autoservice.model.Mechanic;
import autoservice.model.ServiceOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Состояние приложения для сериализации.
 * Содержит все данные, которые необходимо сохранить.
 */
public class ApplicationState implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<Mechanic> mechanics;
    private List<GarageSlot> garageSlots;
    private List<ServiceOrder> orders;
    
    public ApplicationState() {
        this.mechanics = new ArrayList<>();
        this.garageSlots = new ArrayList<>();
        this.orders = new ArrayList<>();
    }
    
    public ApplicationState(List<Mechanic> mechanics, List<GarageSlot> garageSlots, List<ServiceOrder> orders) {
        this.mechanics = new ArrayList<>(mechanics);
        this.garageSlots = new ArrayList<>(garageSlots);
        this.orders = new ArrayList<>(orders);
    }
    
    public List<Mechanic> getMechanics() {
        return mechanics;
    }
    
    public void setMechanics(List<Mechanic> mechanics) {
        this.mechanics = mechanics;
    }
    
    public List<GarageSlot> getGarageSlots() {
        return garageSlots;
    }
    
    public void setGarageSlots(List<GarageSlot> garageSlots) {
        this.garageSlots = garageSlots;
    }
    
    public List<ServiceOrder> getOrders() {
        return orders;
    }
    
    public void setOrders(List<ServiceOrder> orders) {
        this.orders = orders;
    }
}


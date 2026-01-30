package autoservice.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * JPA Entity для заказа-наряда автосервиса.
 */
@Entity
@Table(name = "service_orders")
public class ServiceOrderEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mechanic_id", nullable = false)
    private MechanicEntity mechanic;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "garage_slot_id", nullable = false)
    private GarageSlotEntity garageSlot;
    
    @Column(name = "time_slot_start", nullable = false)
    private LocalDateTime timeSlotStart;
    
    @Column(name = "time_slot_end", nullable = false)
    private LocalDateTime timeSlotEnd;
    
    @Column(name = "price", nullable = false)
    private Integer price;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatusEnum status;
    
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;
    
    @Column(name = "finished_at")
    private LocalDateTime finishedAt;
    
    public ServiceOrderEntity() {
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public MechanicEntity getMechanic() {
        return mechanic;
    }
    
    public void setMechanic(MechanicEntity mechanic) {
        this.mechanic = mechanic;
    }
    
    public GarageSlotEntity getGarageSlot() {
        return garageSlot;
    }
    
    public void setGarageSlot(GarageSlotEntity garageSlot) {
        this.garageSlot = garageSlot;
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
    
    public Integer getPrice() {
        return price;
    }
    
    public void setPrice(Integer price) {
        this.price = price;
    }
    
    public OrderStatusEnum getStatus() {
        return status;
    }
    
    public void setStatus(OrderStatusEnum status) {
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceOrderEntity)) return false;
        ServiceOrderEntity that = (ServiceOrderEntity) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return "ServiceOrderEntity{id=" + id + ", mechanic=" + mechanic + 
               ", garageSlot=" + garageSlot + ", price=" + price + ", status=" + status + "}";
    }
}

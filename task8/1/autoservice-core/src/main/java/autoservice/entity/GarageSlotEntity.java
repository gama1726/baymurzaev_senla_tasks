package autoservice.entity;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * JPA Entity для гаражного места.
 */
@Entity
@Table(name = "garage_slots")
public class GarageSlotEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "is_occupied", nullable = false)
    private Boolean occupied = false;
    
    public GarageSlotEntity() {
    }
    
    public GarageSlotEntity(Integer id) {
        this.id = id;
        this.occupied = false;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Boolean getOccupied() {
        return occupied;
    }
    
    public void setOccupied(Boolean occupied) {
        this.occupied = occupied;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GarageSlotEntity)) return false;
        GarageSlotEntity that = (GarageSlotEntity) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return "GarageSlotEntity{id=" + id + ", occupied=" + occupied + "}";
    }
}

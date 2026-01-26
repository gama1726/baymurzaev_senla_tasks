package autoservice.entity;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * JPA Entity для механика.
 */
@Entity
@Table(name = "mechanics")
public class MechanicEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    public MechanicEntity() {
    }
    
    public MechanicEntity(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MechanicEntity)) return false;
        MechanicEntity that = (MechanicEntity) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return "MechanicEntity{id=" + id + ", name='" + name + "'}";
    }
}

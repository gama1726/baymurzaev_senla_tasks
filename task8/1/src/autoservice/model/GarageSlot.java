package autoservice.model;

import java.io.Serializable;

/**
 * Гаражное место.
 */
public class GarageSlot implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private boolean isOccupied = false;

    public GarageSlot(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public boolean getStatus(){
        return isOccupied;
    }
    public void occupy(){
        isOccupied = true;
    }
    public void unOccupy(){
        isOccupied = false;
    }
    @Override
    public String toString(){
        return "Id слота: " + getId() + ",Статус: " + (getStatus()?"Занято" : "Свободно");
    }


}

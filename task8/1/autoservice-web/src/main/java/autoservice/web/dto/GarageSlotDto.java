package autoservice.web.dto;

/**
 * DTO гаражного места для ответов API.
 */
public class GarageSlotDto {
    private int id;
    private boolean occupied;

    public GarageSlotDto() {
    }

    public GarageSlotDto(int id, boolean occupied) {
        this.id = id;
        this.occupied = occupied;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }
}

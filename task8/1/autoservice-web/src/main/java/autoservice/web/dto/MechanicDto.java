package autoservice.web.dto;

/**
 * DTO механика для ответов API.
 */
public class MechanicDto {
    private int id;
    private String name;

    public MechanicDto() {
    }

    public MechanicDto(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

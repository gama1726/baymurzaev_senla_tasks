package autoservice.web.dto;

/**
 * Запрос на создание механика.
 */
public class CreateMechanicRequest {
    private String name;

    public CreateMechanicRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package autoservice.web.dto;

/**
 * Запрос на создание гаражного места (id задаётся или генерируется).
 */
public class CreateGarageSlotRequest {
    private Integer id;

    public CreateGarageSlotRequest() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}

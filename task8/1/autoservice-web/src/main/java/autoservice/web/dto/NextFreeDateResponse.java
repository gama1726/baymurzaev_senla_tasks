package autoservice.web.dto;

import java.time.LocalDateTime;

/**
 * Ответ с ближайшей свободной датой.
 */
public class NextFreeDateResponse {
    private LocalDateTime nextFreeDate;

    public NextFreeDateResponse() {
    }

    public NextFreeDateResponse(LocalDateTime nextFreeDate) {
        this.nextFreeDate = nextFreeDate;
    }

    public LocalDateTime getNextFreeDate() {
        return nextFreeDate;
    }

    public void setNextFreeDate(LocalDateTime nextFreeDate) {
        this.nextFreeDate = nextFreeDate;
    }
}

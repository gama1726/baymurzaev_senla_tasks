package autoservice.web.dto;

import java.time.LocalDateTime;

/**
 * DTO временного слота для ответов API.
 */
public class TimeSlotDto {
    private LocalDateTime start;
    private LocalDateTime end;

    public TimeSlotDto() {
    }

    public TimeSlotDto(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }
}

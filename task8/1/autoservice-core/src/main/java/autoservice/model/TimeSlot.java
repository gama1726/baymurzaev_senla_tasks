package autoservice.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Временной интервал для выполнения заказа.
 */
public class TimeSlot implements Serializable {
    private static final long serialVersionUID = 1L;
    LocalDateTime start;
    LocalDateTime end;

    public TimeSlot(LocalDateTime start,LocalDateTime end){
        this.start = start;
        this.end = end;
    }
    /** Сместить интервал на указанное количество минут. */
    void shiftBy(int minutes){
        start = start.plusMinutes(minutes);
        end = end.plusMinutes(minutes);
    };
    public LocalDateTime getStart(){
        return start;
    }
    public LocalDateTime getEnd(){
        return end;
    }
    //пересекаются ли заказы с другим слотом
    public boolean overlaps(TimeSlot other){
        return this.start.isBefore(other.end) && other.getStart().isBefore(this.end);

    }
    //момент времени попадает внутрь слота?
    public boolean contains(LocalDateTime t){
        return(!t.isBefore(start) && t.isBefore(end));
    }
    @Override
    public String toString(){
        return "Начало слота : " + start + ",Конец: " + end;
    }
}

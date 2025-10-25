
import java.time.LocalDateTime;

public class TimeSlot {
    LocalDateTime start;
    LocalDateTime end;

    public TimeSlot(LocalDateTime start,LocalDateTime end){
        this.start = start;
        this.end = end;
    }
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
    @Override
    public String toString(){
        return "Начало слота : " + start + ",Конец: " + end;
    }
}

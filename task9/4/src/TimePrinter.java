import java.time.LocalDateTime;

public class TimePrinter extends Thread{
    private final int nSeconds;
    TimePrinter(int nSeconds){
        this.nSeconds = nSeconds;
        if(nSeconds<=0){
            throw  new IllegalArgumentException();
        }
    }

    @Override
    public void run() {
        while(true){
            LocalDateTime now = LocalDateTime.now();
            System.out.println("Сейчас " + now);
            try {
                Thread.sleep(nSeconds*1000);//наша выставляемая пауза
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();//восстановили флаг прерывания
            }
        }
    }
}

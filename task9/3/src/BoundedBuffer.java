import java.util.ArrayList;
import java.util.List;

public class BoundedBuffer {
    List<Double> buffer = new ArrayList<Double>();
    private int capacity;
    Object lock = new Object();
    public BoundedBuffer(int capacity) {
        this.capacity = capacity;
    }
    public void put(double x){
        synchronized (lock){
            //System.out.println("Lock");
            while(buffer.size() == capacity) {
                try {
                    System.out.println("Producer ждет: буффер полон");
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);

                }
            }
                buffer.add(x);
                lock.notifyAll();
        }
    }
    public double take(){
        double value = 0;
        synchronized (lock){
            while(buffer.isEmpty()){
                try {
                    System.out.println("Consumer ждет: буффер пуст");
                    lock.wait();
                } catch (InterruptedException e) {}
            }
            value = buffer.get(0);
            buffer.remove(0);
            lock.notifyAll();
        }


        return value;
    }

}

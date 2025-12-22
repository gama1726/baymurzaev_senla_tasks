import java.util.concurrent.atomic.AtomicReference;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws InterruptedException{
        AtomicReference<Integer> turn = new AtomicReference<>(0);
        Object lock = new Object();
        Thread t1 = new Thread(() -> {
            for(int i = 0;i < 5;i++){
                synchronized (lock) {
                    while (turn.get() != 0) {
                            try{
                                lock.wait();
                            }
                            catch (Exception e){
                                throw new RuntimeException(e);
                            }
                    }
                    System.out.println(Thread.currentThread().getName());
                    turn.set(1);
                    lock.notifyAll();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        });
        Thread t2 = new Thread(() -> {
            for(int i = 0;i < 5;i++){
                synchronized (lock) {
                    while (turn.get() != 1) {
                        try{
                            lock.wait();
                        }
                        catch (Exception e){
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.println(Thread.currentThread().getName());
                    turn.set(0);
                    lock.notifyAll();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}
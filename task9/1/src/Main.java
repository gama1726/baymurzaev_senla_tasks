import static java.lang.Thread.sleep;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Object lock = new Object();
        Turn turn = new Turn();
        Thread t = new Thread(() ->{
            while(turn.isFlag() == false) {

            }
            synchronized (lock){
                try{
                    lock.wait();
                }
                    catch (InterruptedException e){
                        throw new RuntimeException(e);

                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                }
            };
        });
        System.out.println(t.getState());

        t.start();
        System.out.println(t.getState());
        turn.setFlag();
        synchronized (lock) {
            while(t.getState()!= Thread.State.BLOCKED){

            }
            t.getState();
        }

        System.out.println(t.getState());
        t.sleep(1000);

        if(t.getState() == Thread.State.WAITING) {System.out.println(t.getState());}
        synchronized(lock) {
            lock.notifyAll();
        }
        t.sleep(1000);
        if(t.getState() == Thread.State.TIMED_WAITING) {System.out.println(t.getState());}
        t.sleep(1000);
        System.out.println(t.getState());



    }
}
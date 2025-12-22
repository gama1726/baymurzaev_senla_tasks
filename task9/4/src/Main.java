//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        TimePrinter tp = new TimePrinter(2);
        tp.setDaemon(true);
        System.out.println(tp.isDaemon());
        tp.start();
        try {
            Thread.sleep(20000);//задержка для работы демона
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
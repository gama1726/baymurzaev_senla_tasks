//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws InterruptedException {
        int n = 20;
        BoundedBuffer buffer1 = new BoundedBuffer(5);
        Thread producer = new Thread(() -> {
            for(int i = 0; i < n; i++){
                double random = Math.random() * 100;
                buffer1.put(random);
                random = Math.random() * 100;
                buffer1.put(random);
                System.out.println("Добавлено пара чисел номер " + (i + 1));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        });
        Thread consumer = new Thread(() -> {
            for(int i = 0; i < n; i++){//consumer закончит работать быстрее чем producer,сделано так,чтобы показать функционал при заполненном буффере

                double value = buffer1.take();
                System.out.println("Получено число " + value);

            }
        });
        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
    }

}
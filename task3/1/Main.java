import java.util.Random;
class Main{
    public static void main(String[] args){
        Random random = new Random();
        int value = random.nextInt(900) + 100;
        int sum = 0;
        System.out.println("\nСлучайное трехзначное число:" + value);
        while (value > 0) { 
            sum = sum + value % 10;
            value = value / 10;
        }
        System.out.println("Сумма его цифр = " + sum + "\n");
        
    }
}
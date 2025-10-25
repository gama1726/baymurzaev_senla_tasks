
import java.util.Scanner;

class Main{
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        Buket buket = new Buket(sc);
        boolean running = true;
        while(running){
            System.out.println("""
                    Добро пожаловать в наш магазин Мир Цветов!
                    Меню:
                    1. Выбрать цветок.
                    2. Удалить цветок.
                    3. Список цветов и общая стоимость букета.
                    4. Выход из магазина.
                    Выберите действие:
                    """);
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    buket.addToBuket();

                    break;
                case 2:
                    buket.showBuket();
                    buket.deleteFromBuket();
                    break;
                case 3:
                    buket.showBuket();
                    break;
                case 4:
                    System.out.println("\nДо свидания,приходите еще!\n");
                    running = false;
                    break;
                default:
                    System.out.println("\nНеверный выбор,попробуйте заново!\n");
            }
        }
    }
}
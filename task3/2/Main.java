
import java.util.Scanner;

class Main{
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        Buket buket = new Buket(sc);
        while(true){
            System.out.println("Добро пожаловать в наш магазин Мир Цветов!");
            System.out.println("Меню:");
            System.out.println("1.Выбрать цветок.");
            System.out.println("2.Удалить цветок.");
            System.out.println("3.Список цветов и общая стоимость букета.");
            System.out.println("4.Выход из магазина.");
            System.out.println("Выберите действие:");
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
                    return;
                default:
                    System.out.println("\nНеверный выбор,попробуйте заново!\n");
            }
        }
    }
}
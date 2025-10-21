import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
class Buket{
    private final Scanner sc;
    Buket(Scanner sc){
        this.sc = sc;
    }
    private final List<Flower> flowers = new ArrayList<>();

    public void addToBuket(){
        System.out.println("Выберите Цветок:");
        System.out.println("1.Лилия - 50р.");
        System.out.println("2.Роза - 100 р.");
        System.out.println("3.Тюльпан - 75 р.");
        
        switch (sc.nextInt()) {
            case 1:
                flowers.add(new Lily());
                showBuket();
                break;
            case 2:
                flowers.add(new Rose());
                showBuket();
                break;
            case 3:
                flowers.add(new Tulip());
                showBuket();
                break;
            default:
                System.out.println("Неверный выбор!");
                break;
        }
    }
    public void deleteFromBuket(){
        if(flowers.isEmpty()){
            showBuket();
        }
        else{
            flowers.remove(sc.nextInt() - 1);
        }
    }
    public double getTotalPrice(){
        double totalPrice = 0;
        for(int i=0;i<flowers.size();i++){
            totalPrice = totalPrice + (flowers.get(i)).getPrice();
        }
        return totalPrice;
    }
    public void showBuket(){
        if(flowers.isEmpty()){
            System.out.println("\nБукет пуст!\n");
        }
        else{
             System.out.println("Цветы в букете:");
            for(int i=0;i<flowers.size();i++){
                System.out.printf("%d. %s\n",i + 1,flowers.get(i).getName());
            }
            System.out.println("Общая стоимость:" + getTotalPrice());
        }
    }
}
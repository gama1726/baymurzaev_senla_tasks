abstract class Flower{
    private String name;
    private double price;
     Flower(String name,double price){
         this.name=name;
         this.price=price;
     }
     protected double getPrice(){
         return price;
    }
     protected String getName(){
         return name;
    }

}
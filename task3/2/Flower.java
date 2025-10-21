abstract class Flower{
    protected String name;
    protected double price;
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
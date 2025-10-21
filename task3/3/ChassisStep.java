public class ChassisStep implements ILineStep {
    @Override
    public IProductPart buildProductPart(){
        System.out.println("Создается Шасси...");
        return new Chassis();
    }
}

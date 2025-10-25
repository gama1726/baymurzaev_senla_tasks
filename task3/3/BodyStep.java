public class BodyStep implements ILineStep {
    @Override
    public IProductPart buildProductPart(){
        System.out.println("Создается Кузов...");
        return new Body();
    }
}

public class EngineStep implements ILineStep {
    @Override
    public IProductPart buildProductPart(){
        System.out.println("Создается Двигатель...");
        return new Engine();
    }
}

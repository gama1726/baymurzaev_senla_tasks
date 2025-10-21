public class Car implements IProduct{
    private IProductPart body;
    private IProductPart chassis;
    private IProductPart engine;

    @Override
    public void installFirstPart(IProductPart part){
        body = part;
        System.out.println("Устанавливается: " + part);
    }
    
    @Override
    public void installSecondPart(IProductPart part){
        chassis = part;
        System.out.println("Устанавливается: " + part);
    }

    @Override
    public void installThirdPart(IProductPart part){
        engine = part;
        System.out.println("Устанавливается: " + part);
    }
    public String toString(){
        return "Собран Автомобиль из " + body +  ", " + chassis + ", " + engine;
    }

}

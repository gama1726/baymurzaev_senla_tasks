class Main{
    public static void main(String[] args) {
        ILineStep bodyStep = new BodyStep();
        ILineStep chassisStep = new ChassisStep();
        ILineStep engineStep = new EngineStep();

        IAssemblyLine carLine = new CarAssemblyLine(bodyStep,chassisStep,engineStep);
        Car car = new Car();
        IProduct finishedCar = carLine.assembleProduct(car);

        System.out.println("Итог: ");
        System.out.println(finishedCar);
    }
}
package autoservice.ui.menu.factory;

/**
 * Абстрактная фабрика для создания семейств связанных пунктов меню.
 * Реализует паттерн Abstract Factory.
 */
public interface AbstractMenuFactory {
    OrderMenuFactory createOrderMenuFactory();
    MechanicMenuFactory createMechanicMenuFactory();
    GarageSlotMenuFactory createGarageSlotMenuFactory();
    CapacityMenuFactory createCapacityMenuFactory();
}


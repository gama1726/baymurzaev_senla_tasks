package autoservice.ui.menu;

import autoservice.ui.menu.factory.AbstractMenuFactory;

/**
 * Строит структуру меню для консольного UI.
 * Использует Abstract Factory для создания семейств пунктов меню.
 */
public class MenuBuilder {
    private final AbstractMenuFactory abstractFactory;

    public MenuBuilder(AbstractMenuFactory abstractFactory) {
        this.abstractFactory = abstractFactory;
    }

    /**
     * Строит и возвращает корневое (главное) меню.
     */
    public Menu buildRootMenu(){
        Menu root = new Menu("Главное меню автосервиса Альянс");

        // Создаем семейства фабрик через Abstract Factory
        var orderFactory = abstractFactory.createOrderMenuFactory();
        var mechanicFactory = abstractFactory.createMechanicMenuFactory();
        var garageSlotFactory = abstractFactory.createGarageSlotMenuFactory();
        var capacityFactory = abstractFactory.createCapacityMenuFactory();

        // Добавляем пункты меню заказов
        root.addItem(orderFactory.createListOrdersItem());
        root.addItem(orderFactory.createListCurrentOrdersItem());
        root.addItem(orderFactory.createCreateOrderItem());
        root.addItem(orderFactory.createCloseOrderItem());
        root.addItem(orderFactory.createCancelOrderItem());
        root.addItem(orderFactory.createDeleteOrderItem());
        root.addItem(orderFactory.createShiftOrderItem());
        root.addItem(orderFactory.createOrderByMechanicNowItem());
        root.addItem(orderFactory.createMechanicByOrderItem());
        root.addItem(orderFactory.createOrdersByPeriodItem());
        root.addItem(orderFactory.createExportOrdersItem());
        root.addItem(orderFactory.createImportOrdersItem());

        // Добавляем пункты меню механиков
        root.addItem(mechanicFactory.createListMechanicsItem());
        root.addItem(mechanicFactory.createAddMechanicItem());
        root.addItem(mechanicFactory.createRemoveMechanicItem());
        root.addItem(mechanicFactory.createExportMechanicsItem());
        root.addItem(mechanicFactory.createImportMechanicsItem());

        // Добавляем пункты меню гаражных мест
        root.addItem(garageSlotFactory.createListGarageSlotsItem());
        root.addItem(garageSlotFactory.createListFreeGarageSlotsNowItem());
        root.addItem(garageSlotFactory.createAddGarageSlotItem());
        root.addItem(garageSlotFactory.createRemoveGarageSlotItem());
        root.addItem(garageSlotFactory.createExportGarageSlotsItem());
        root.addItem(garageSlotFactory.createImportGarageSlotsItem());

        // Добавляем пункты меню расчета емкости
        root.addItem(capacityFactory.createFreeCapacityAtItem());
        root.addItem(capacityFactory.createNextFreeDateItem());

        return root;
    }
}

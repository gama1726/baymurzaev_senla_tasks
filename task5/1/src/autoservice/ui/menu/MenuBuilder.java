package autoservice.ui.menu;



import autoservice.ui.menu.factory.MenuFactory;
/**
 * Строит структуру меню для консольного UI.
 */

public class MenuBuilder {
    private final MenuFactory factory;

    public MenuBuilder(MenuFactory factory) {
        this.factory = factory;
    }
    /**
     * Строит и возвращает корневое (главное) меню.
     */
    public Menu buildRootMenu(){
        Menu root = new Menu("Главное меню автосервиса Альянс");
        root.addItem(factory.createListOrdersItem());
        root.addItem(factory.createListCurrentOrdersItem());
        root.addItem(factory.createCreateOrderItem());
        root.addItem(factory.createCloseOrderItem());
        root.addItem(factory.createCancelOrderItem());
        root.addItem(factory.createDeleteOrderItem());
        root.addItem(factory.createShiftOrderItem());
        root.addItem(factory.createOrderByMechanicNowItem());
        root.addItem(factory.createMechanicByOrderItem());
        root.addItem(factory.createOrdersByPeriodItem());

        root.addItem(factory.createListMechanicsItem());
        root.addItem(factory.createAddMechanicItem());
        root.addItem(factory.createRemoveMechanicItem());

        root.addItem(factory.createListGarageSlotsItem());
        root.addItem(factory.createListFreeGarageSlotsNowItem());
        root.addItem(factory.createAddGarageSlotItem());
        root.addItem(factory.createRemoveGarageSlotItem());

        root.addItem(factory.createFreeCapacityAtItem());
        root.addItem(factory.createNextFreeDateItem());

        return root;
    }
}
